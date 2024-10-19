package cn.snowrainyskr.aff.structure.timingGroup

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.Item
import cn.snowrainyskr.aff.structure.item.ItemCompanion
import cn.snowrainyskr.aff.structure.item.Timing
import cn.snowrainyskr.aff.structure.item.note.Arc
import cn.snowrainyskr.aff.structure.item.note.HoldJudgmentLike
import cn.snowrainyskr.aff.structure.item.note.Note
import cn.snowrainyskr.aff.utils.assignToRanges
import cn.snowrainyskr.aff.utils.find
import cn.snowrainyskr.aff.utils.format

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

	private lateinit var timingsBehind: MutableList<Timing>
	private var needRecalculateTimings = false

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

	fun <T: Item> add(item: T) {
		val index = items.binarySearch { it.time - item.time }
		val insertionPoint = if (index < 0) -index - 1 else index + 1
		items.add(insertionPoint, item.apply {
			aff = this@TimingGroup.aff
			timingGroup = this@TimingGroup
		})
		when (item) {
			is Timing -> needRecalculateTimings = true
			is HoldJudgmentLike -> {
				if (item is Arc) aff.needRecalculateArcGroups = true
				item.bpm = timings.find { item.time in it.range }!!.bpm
			}
		}

	}

	@Suppress("UNUSED")
	fun <T: Item> remove(item: T) {
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
	}

	fun bpm(time: Int) = timings.find(time).bpm
	@Suppress("UNUSED")
	fun getNoteDurationForNParts(time: Int, n: Number) = (240_000 / (n.toDouble() * bpm(time))).toInt()

	@Suppress("UNUSED")
	fun align(note: Note, n: Number, allowableError: Int? = null, timingIncluding: Timing? = null) =
		(timingIncluding ?: timings.find(note.time)).align(note, n, allowableError)

	fun align(n: Number, allowableError: Int? = null) = timings.forEach { it.align(n, allowableError = allowableError) }

	companion object {
		fun fromAffLines(contentLines: List<String>) = fromAffLines("", contentLines)

		fun fromAffLines(specialEffects: String, contentLines: List<String>) =
			TimingGroup(TimingGroupSpecialEffect.fromParam(specialEffects), buildList {
				contentLines.filter { it.format().isNotEmpty() }.forEach { add(ItemCompanion.fromAffLine(it)) }
			}.toMutableList())
	}
}