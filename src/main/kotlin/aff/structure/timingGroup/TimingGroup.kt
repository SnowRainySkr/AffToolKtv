package cn.snowrainyskr.aff.structure.timingGroup

import cn.snowrainyskr.aff.composite.item.CompositeItem
import cn.snowrainyskr.aff.composite.item.CompositeSkyLine
import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.AffSortOptions
import cn.snowrainyskr.aff.structure.item.Item
import cn.snowrainyskr.aff.structure.item.Timing
import cn.snowrainyskr.aff.structure.item.note.Arc
import cn.snowrainyskr.aff.structure.item.note.ArcLike
import cn.snowrainyskr.aff.structure.item.note.GroundNote
import cn.snowrainyskr.aff.structure.item.note.Hold
import cn.snowrainyskr.aff.structure.item.note.HoldJudgmentLike
import cn.snowrainyskr.aff.structure.item.note.HoldLike
import cn.snowrainyskr.aff.structure.item.note.Note
import cn.snowrainyskr.aff.structure.item.note.SkyNote
import cn.snowrainyskr.aff.utils.AdeCoordinate
import cn.snowrainyskr.aff.utils.assignToRanges
import cn.snowrainyskr.aff.utils.find
import cn.snowrainyskr.aff.utils.format
import kotlin.math.roundToInt

class TimingGroup(
	val specialEffects: MutableSet<TimingGroupSpecialEffect> = mutableSetOf(),
	val items: MutableList<Item> = mutableListOf()
) {
	lateinit var aff: Aff

	private fun recalculateTimings() {
		timingsBehind = items.filterIsInstance<Timing>().sortedBy { it.time }.toMutableList()
		val timings = timingsBehind
		for ((curr, next) in timings.zip(timings.drop(1) + Timing.inf)) curr.range = curr.time..<next.time
		val timingMap = items.filterIsInstance<HoldJudgmentLike>().assignToRanges(timings, { it.range }) { it.time }
		timingMap.forEach { (timing, notes) -> notes.forEach { it.bpm = timing.bpm } }
	}

	fun tryRecalculateTimings() {
		if (needRecalculateTimings) recalculateTimings()
		needRecalculateTimings = false
	}

	fun recalculateNoteAdeCoordinate() {
		tryRecalculateTimings()
		val timingMap = notes.assignToRanges(timings, { it.range }) { it.time }.toList().sortedBy { it.first.time }
		var lastTimingZ = 0.0
		val speed = aff.speed
		for ((timing, notes) in timingMap) {
			for (note in notes) {
				val z = AdeCoordinate.z(speed, aff.baseBpm.bpm, timing.bpm, lastTimingZ, timing.time, note.time)
				val (x, y) = when (note) {
					is GroundNote -> Pair(AdeCoordinate.laneToX(note.lane), 0.55)
					is SkyNote -> AdeCoordinate.posToXY(note.pos)
				}
				note.adeCoordinate = AdeCoordinate(x, y, z)
				if (note is HoldLike) {
					val z =
						AdeCoordinate.z(speed, aff.baseBpm.bpm, timing.bpm, lastTimingZ, timing.time, note.toTime)
					val (x, y) = when (note) {
						is Hold -> Pair(x, y)
						is ArcLike -> AdeCoordinate.posToXY(note.toPos)
					}
					note.toAdeCoordinate = AdeCoordinate(x, y, z)
				}
			}
			lastTimingZ =
				AdeCoordinate.z(aff.speed, aff.baseBpm.bpm, timing.bpm, lastTimingZ, timing.time, timing.toTime + 1)
		}
	}

	private lateinit var timingsBehind: MutableList<Timing>
	var needRecalculateTimings = false

	val timings: List<Timing>
		get() {
			tryRecalculateTimings()
			return timingsBehind
		}

	init {
		items.sortBy { it.time }
		if (items.first { it is Timing }.time > 0) items.addFirst(Timing.zero)
		recalculateTimings()
		items.forEach { it.timingGroup = this }
	}

	fun toAffLines(isDefaultTimingGroup: Boolean) =
		if (isDefaultTimingGroup) items.map { it.toAffLine() }.joinToString("\n")
		else listOf(
			"timinggroup(${specialEffects.map { it.toParam() }.joinToString("_")}){",
			items.map { "  " + it.toAffLine() }.joinToString("\n"),
			"};",
		).joinToString("\n")

	fun add(item: Item) {
		val index = items.binarySearch { it.time - item.time }
		var insertionPoint = if (index < 0) -index - 1 else index + 1
		if (item is Timing) {
			var (i, j) = Pair(insertionPoint - 1, insertionPoint - 1)
			while (i >= 0 && items[i].time == item.time) {
				if (items[i] is Timing) {
					items.removeAt(i)
					insertionPoint--
				} else --i
			}
			while (j < items.size && items[j].time == item.time) {
				if (items[j] is Timing) {
					items.removeAt(i)
					insertionPoint--
				} else ++j
			}
		}
		items.add(insertionPoint, item.apply {
			aff = this@TimingGroup.aff
			timingGroup = this@TimingGroup
		})
		when (item) {
			is Timing -> needRecalculateTimings = true
			is HoldJudgmentLike -> {
				if (item is Arc) aff.needRecalculateArcGroups = true
				needRecalculateTimings = true
			}
		}
	}

	fun add(compositeItem: CompositeItem) {
		if (compositeItem.size < 10) {
			compositeItem.items.forEach { add(it) }
			when (compositeItem) {
				is CompositeSkyLine -> {}
			}
		} else {
			val a = compositeItem.items.first().time
			val b = compositeItem.items.last().time
			items.addAll(compositeItem.items)
			itemsInRange(a..b).sortBy(AffSortOptions.SortByTime.sorter)
		}
	}

	fun remove(item: Item): Boolean {
		val index = items.binarySearch { it.time - item.time }
		if (index >= 0) {
			var (i, j) = Pair(index, index)
			while (true) {
				if (i >= 0 && items[i] === item) {
					items.removeAt(i)
					break
				} else i--
				if (j < items.size && items[j] === item) {
					items.removeAt(j)
					break
				} else j++
			}
		}
		return index >= 0
	}

	fun bpm(time: Int) = timings.find(time).bpm

	@Suppress("UNUSED")
	fun getNoteDurationForNParts(time: Int, n: Number) = (240_000 / (n.toDouble() * bpm(time))).toInt()

	@Suppress("UNUSED")
	fun align(note: Note, n: Number, allowableError: Int? = null, timingIncluding: Timing? = null) =
		(timingIncluding ?: timings.find(note.time)).align(note, n, allowableError)

	fun align(n: Number, allowableError: Int? = null) = timings.forEach { it.align(n, allowableError = allowableError) }

	fun itemsOffset(offset: Int) {
		items.forEach { it.moveForward(offset) }
		recalculateTimings()
		items.addFirst(Timing.zero(items.first { it is Timing } as Timing))
	}

	val notes
		get() = items.filterIsInstance<Note>()

	private fun List<Item>.binaryFindFirst(time: Int) = binarySearch { it.time - time }.let {
		if (it >= 0) {
			var i = it
			while (i >= 0 && this[i].time == time) --i
			i + 1
		} else -it - 1
	}

	private fun List<Item>.binaryFindLast(time: Int) = binarySearch { it.time - time }.let {
		if (it >= 0) {
			var i = it
			while (i < size && this[i].time == time) ++i
			i - 1
		} else -it - 2
	}

	fun itemsInRange(timeRange: IntRange) =
		items.subList(items.binaryFindFirst(timeRange.first), items.binaryFindLast(timeRange.last) + 1)

	fun notesInRange(timeRange: IntRange) =
		notes.subList(notes.binaryFindFirst(timeRange.first), notes.binaryFindLast(timeRange.last) + 1)

	fun holdLikeIncluding(time: Int, fromTime: Int = 0): List<HoldLike> {
		val firstCheckIndex = items.binaryFindFirst(fromTime)
		val lastCheckIndex = items.binaryFindLast(time)
		return items.subList(firstCheckIndex, lastCheckIndex + 1)
			.filter { it is HoldLike && it.include(time) }.map { it as HoldLike }.toList()
	}

	fun mirror() = notes.forEach { it.mirror() }

	fun scale(scale: Double, fromTime: Int? = null) {
		val fromTime = fromTime ?: 0
		items.filter { it.time >= fromTime }.forEach {
			when (it) {
				is Timing -> it.bpm *= scale
				is HoldLike -> it.scale(scale)
			}
			it.moveTo(((it.time - fromTime) / scale + fromTime).roundToInt())
			needRecalculateTimings = true
		}
	}

	companion object {
		fun fromAffLines(contentLines: List<String>) = fromAffLines("", contentLines)

		fun fromAffLines(specialEffects: String, contentLines: List<String>) =
			TimingGroup(TimingGroupSpecialEffect.fromParam(specialEffects), buildList {
				contentLines.filter { it.format().isNotEmpty() }.forEach { add(Item.fromAffLine(it)) }
			}.toMutableList())
	}
}