package cn.snowrainyskr.aff.structure

import cn.snowrainyskr.aff.structure.header.AffHeader
import cn.snowrainyskr.aff.structure.header.AudioOffset
import cn.snowrainyskr.aff.structure.header.TimingPointDensityFactor
import cn.snowrainyskr.aff.structure.item.Item
import cn.snowrainyskr.aff.structure.item.note.Arc
import cn.snowrainyskr.aff.structure.item.note.Note
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup
import java.io.File
import java.util.Stack
import kotlin.math.absoluteValue
import kotlin.math.ceil

class Aff(val headers: MutableMap<String, AffHeader>, val timingGroups: MutableList<TimingGroup>) {
	init {
		timingGroups.forEach {
			it.aff = this
			it.items.forEach { it.aff = this }
		}
		recalculateArcGroups()
	}

	@Suppress("UNUSED")
	fun toAff(option: AffSortOptions = AffSortOptions.SortByClass): String {
		timingGroups.forEach {
			it.items.sortWith(compareBy(option.sorter, option.another.sorter))
		}
		return arrayOf(
			headers.values.map { it.toAffLine() }.joinToString("\n"),
			"-",
			timingGroups[0].toAffLines(true),
			timingGroups.drop(1).map { it.toAffLines(false) }.joinToString("\n")
		).joinToString("\n")
	}

	@Suppress("UNUSED")
	fun output(file: File, option: AffSortOptions = AffSortOptions.SortByClass) {
		file.writeText(toAff(option))
	}

	// Headers Edit
	fun addAffHeader(affHeader: AffHeader) = affHeader.run {
		headers[paramName]?.run { valueAsString = affHeader.valueAsString } ?: headers.put(paramName, this)
	}

	fun addAffHeader(name: String, value: String) = addAffHeader(AffHeader.create(name, value))

	fun removeAffHeader(name: String) = headers.remove(name)

	@Suppress("UNUSED")
	var audioOffset: Int
		get() = (headers[AudioOffset.PARAM_NAME]!! as AudioOffset).value
		set(value) {
			addAffHeader(AudioOffset.PARAM_NAME, value.toString())
		}

	var timingPointDensityFactor: Double
		get() = headers[TimingPointDensityFactor.PARAM_NAME]?.let { (it as TimingPointDensityFactor).value } ?: 1.0
		set(value) {
			if (value == 1.0) removeAffHeader(TimingPointDensityFactor.PARAM_NAME)
			else addAffHeader(TimingPointDensityFactor.PARAM_NAME, value.toString())
		}

	@Suppress("UNUSED")
	var version: String
		get() = headers["version"]?.valueAsString ?: "0"
		set(value) {
			addAffHeader("version", value)
		}

	var constant: Double?
		get() = headers["constant"]?.valueAsString?.toDouble()
		set(value) {
			headers["constant"]?.run { valueAsString = value.toString() } ?: addAffHeader(
				"constant",
				value.toString()
			)
		}

	//Aff Controlling
	val defaultTimingGroup
		get() = timingGroups.first()

	var needRecalculateArcGroups = false

	fun recalculateArcGroups() {
		arcGroupsBehind = run {
			val arcTrees = buildMap<Arc, MutableList<MutableSet<Arc>>> {
				val arcs = timingGroups.map { it.items.filterIsInstance<Arc>() }.flatten().sortedBy { it.time }.toList()
				val headList = buildMap<Arc, Set<Arc>> {
					val heads = arcs.sortedBy { it.toTime }
					val tails = arcs
					var indexTails = 0
					fun item2() = tails[indexTails]
					for (item1 in heads) {
						buildSet<Arc> {
							while (indexTails < tails.size && (item2() === item1 || item2().time < item1.toTime - 10)) indexTails++
							val startIndexTails = indexTails
							while (indexTails < tails.size && item2().time < item1.toTime + 10) {
								val item2 = item2()
								fun near(a: Double, b: Double, r: Double) = (a - b).absoluteValue < r
								if (near(item1.toPos.x, item2.pos.x, 0.1) && near(
										item1.toPos.y,
										item2.pos.y,
										0.01
									)
								) add(item2.apply { isHead = false })
								indexTails++
							}
							indexTails = startIndexTails
						}.takeIf { it.isNotEmpty() }?.let { put(item1, it) }
					}
				}
				for ((head, _) in headList) {
					if (head !in this) {
						fun MutableSet<Arc>.dfs(root: Arc) {
							val stack = Stack<Arc>()
							stack.push(root)

							while (stack.isNotEmpty()) {
								val current = stack.pop()
								if (current !in this) {
									add(current)
									headList[current]?.let { list ->
										list.forEach { stack.push(it) }
									}
								}
							}
						}

						val set = mutableSetOf<Arc>().apply { dfs(head) }
						set.forEach { arc -> getOrPut(arc) { mutableListOf() }.add(set) }
					}
				}
			}
			arcTrees.values.forEach {
				if (it.size > 1) {
					val conSet = it.flatten().toMutableSet()
					conSet.forEach { arc ->
						arcTrees[arc]!!.run {
							clear()
							add(conSet)
						}
					}
				}
			}
			arcTrees.map { (arc, trees) -> Pair(arc, trees.first()) }.toMap().toMutableMap()
		}
	}

	lateinit var arcGroupsBehind: Map<Arc, Set<Arc>>

	@Suppress("UNUSED")
	val arcGroups: Map<Arc, Set<Arc>>
		get() {
			if (needRecalculateArcGroups) recalculateArcGroups()
			needRecalculateArcGroups = false
			return arcGroupsBehind
		}

	@Suppress("UNUSED")
	val items
		get() = timingGroups.map { it.items }.flatten().sortedBy { it.time }

	inline fun <reified T: Item> find(time: Int): List<T> {
		val possibleList = timingGroups.map { it.items.filterIsInstance<T>() }.flatten()
		val index = possibleList.binarySearch { it.time - time }
		return if (index < 0) listOf()
		else buildList {
			var i = index
			while (i >= 0 && possibleList[i].time == time) {
				add(possibleList[i])
				i--
			}
			i = index + 1
			while (i < possibleList.size && possibleList[i].time == time) {
				add(possibleList[i])
				i++
			}
		}
	}

	@Suppress("UNUSED")
	inline fun <reified T: Item> findUnique(time: Int) = find<T>(time).takeIf { it.isNotEmpty() }?.first()

	fun add(item: Item, timingGroup: TimingGroup = defaultTimingGroup) = timingGroup.add(item)

	@Suppress("UNUSED")
	fun align(n: Number, allowableError: Int? = null) = timingGroups.forEach { it.align(n, allowableError) }

	@Suppress("UNUSED")
	fun itemsOffset(offset: Int) = timingGroups.forEach { it.itemsOffset(offset) }

	//Judgments
	val judgments
		get() = timingGroups.map {
			it.tryRecalculateTimings()
			it.items.filterIsInstance<Note>().map { Pair(it, it.judgments) }
		}.flatten()

	val judgeTimes
		get() = judgments.map { it.second }.flatten().sorted()

	@Suppress("UNUSED")
	val quantity
		get() = judgeTimes.size

	interface FractureRayInfo {
		interface RecollectionGaugeInfo {
			val combo: Int?
			val time: Int?
		}

		val memoryFactor: Double
		val trackComplete: RecollectionGaugeInfo
		val hardcore: RecollectionGaugeInfo
	}

	@Suppress("UNUSED")
	val fractureRayInfo
		get() = object: FractureRayInfo {
		private val judgeTimes = this@Aff.judgeTimes
		private val quantity = judgeTimes.size
		override val memoryFactor = when (quantity) {
			in 0..<400 -> 80.0 / quantity + 0.2
			in 400..<600 -> 32.0 / quantity + 0.2
			else -> 96.0 / quantity + 0.08
		}.let { baseMemoryFactor ->
			constant?.takeIf { it >= 11 }?.let { baseMemoryFactor * 0.8 } ?: baseMemoryFactor
		}

		override val trackComplete = object: FractureRayInfo.RecollectionGaugeInfo {
			override val combo = ceil(70 / memoryFactor).toInt()
			override val time = if (combo < quantity) judgeTimes[combo - 1] else null
		}

		override val hardcore = object: FractureRayInfo.RecollectionGaugeInfo {
			override val combo = ceil(100 / memoryFactor).toInt()
			override val time = if (combo < quantity) judgeTimes[combo - 1] else null
		}
	}

	companion object {
		private val e = RuntimeException("AffConvertException")

		private val defaultTimingGroupPattern = Regex(""".*?-\s+(.*?)timinggroup""", RegexOption.DOT_MATCHES_ALL)
		private val pureTimingGroupPattern = Regex(""".*?-\s+(.*)""", RegexOption.DOT_MATCHES_ALL)
		private val timingGroupPattern = Regex("""timinggroup\s*\((.*?)\)\s*\{(.*?)};""", RegexOption.DOT_MATCHES_ALL)

		@Suppress("UNUSED")
		fun fromAff(aff: File) = aff.readText().let { aff ->
			val header = AffHeader.fromAffHeaderLines(aff.lineSequence().takeWhile { it != "-" }.toList())
				.associateBy { it.paramName }.toMutableMap()
			val timingGroups = buildList {
				if (aff.contains("timinggroup")) {
					add(
						TimingGroup.fromAffLines(
							defaultTimingGroupPattern.find(aff)?.groupValues?.get(1)?.split(";") ?: throw e
						)
					)
					timingGroupPattern.findAll(aff).forEach { matchResult ->
						matchResult.groupValues.let {
							add(TimingGroup.fromAffLines(it[1], it[2].split(";")))
						}
					}
				} else add(
					TimingGroup.fromAffLines(
						pureTimingGroupPattern.find(aff)?.groupValues?.get(1)?.split(";") ?: throw e
					)
				)
			}.toMutableList()
			Aff(header, timingGroups)
		}
	}
}