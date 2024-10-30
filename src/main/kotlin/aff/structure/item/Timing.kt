package cn.snowrainyskr.aff.structure.item

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.HoldLike
import cn.snowrainyskr.aff.structure.item.note.Note
import cn.snowrainyskr.aff.structure.item.note.SkyLine
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup
import cn.snowrainyskr.aff.utils.find
import cn.snowrainyskr.aff.utils.format
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

data class Timing(override var time: Int, var bpm: Double, var beats: Double): Item {
	override lateinit var aff: Aff
	override lateinit var timingGroup: TimingGroup

	override fun toAffLine() = "$itemClass($time,${bpm.format()},${beats.format()});"

	override val itemClass: ItemClass
		get() = Timing.itemClass

	lateinit var range: IntRange

	val toTime: Int
		get() {
			timingGroup.tryRecalculateTimings()
			return range.last
		}

	private fun alignHoldLikeToTimeInAnotherTiming(hold: HoldLike, n: Double, allowableError: Int?) {
		val toTimeTiming = timingGroup.timings.find(hold.toTime)
		val interval = 240_000 / (n * toTimeTiming.bpm)
		if (interval == 0.0) return
		val nInterval = ((hold.toTime - toTimeTiming.time) / interval.absoluteValue).roundToInt()
		val toTimeAfterAligned = toTimeTiming.time + (interval * nInterval).roundToInt()
		if (allowableError == null || (toTimeAfterAligned - hold.toTime).absoluteValue < allowableError) hold.toTime =
			toTimeAfterAligned
	}

	private fun alignHoldLikeToTime(
		hold: HoldLike, n: Double, allowableError: Int?, interval: Double, timeAfterAligned: Int
	) {
		if (hold.toTime <= toTime) {
			val nInterval = ((hold.toTime - time) / interval.absoluteValue).roundToInt()
			val toTimeAfterAligned = time + (interval * nInterval).roundToInt()
			if (allowableError == null || (toTimeAfterAligned - hold.toTime).absoluteValue <= allowableError) hold.toTime =
				toTimeAfterAligned
		} else alignHoldLikeToTimeInAnotherTiming(hold, n.toDouble(), allowableError)
		if (allowableError == null || (timeAfterAligned - hold.time).absoluteValue <= allowableError) hold.time =
			timeAfterAligned
	}

	private fun alignNoteTime(tap: Note, timeAfterAligned: Int, allowableError: Int?) {
		if (allowableError == null || (timeAfterAligned - tap.time).absoluteValue <= allowableError) tap.moveTo(
			timeAfterAligned
		)
	}

	fun align(note: Note, n: Number, allowableError: Int? = null) {
		val interval = 240_000 / (n.toDouble() * bpm)
		if (interval == 0.0) return
		val nInterval = ((note.time - time) / interval.absoluteValue).roundToInt()
		val timeAfterAligned = time + (interval * nInterval).roundToInt()
		if (note is SkyLine && note.isArcTap) return alignNoteTime(note, timeAfterAligned, allowableError)
		alignNoteTime(note, timeAfterAligned, allowableError)
		if (note is HoldLike) alignHoldLikeToTime(note, n.toDouble(), allowableError, interval, timeAfterAligned)
	}

	fun align(n: Number, time: Int = this.time, toTime: Int = this.toTime, allowableError: Int? = null) {
		timingGroup.tryRecalculateTimings()
		val time = maxOf(time, this.time)
		val toTime = minOf(toTime, range.last, timingGroup.items.last().toTime())
		val notes = timingGroup.notesInRange(time..toTime).toList()
		if (notes.size < 5) {
			notes.forEach { align(it, n, allowableError) }
			return
		}
		val interval = 240_000 / (n.toDouble() * bpm).absoluteValue
		notes.forEach {
			val nInterval = ((it.time - time) / interval).roundToInt()
			val timeAfterAligned = time + (interval * nInterval).roundToInt()
			when {
				it is SkyLine && it.isArcTap -> alignNoteTime(it, timeAfterAligned, allowableError)
				it is HoldLike -> alignHoldLikeToTime(it, n.toDouble(), allowableError, interval, timeAfterAligned)
				else -> alignNoteTime(it, timeAfterAligned, allowableError)
			}
			if (it is SkyLine && !it.isArcTap) for (i in it.arcTaps.indices) {
				if (allowableError == null || (timeAfterAligned - it.arcTaps[i]).absoluteValue <= allowableError) {
					val nInterval = ((it.arcTaps[i] - time) / interval).roundToInt()
					it.arcTaps[i] = time + (interval * nInterval).roundToInt()
				}
			}
		}

	}

	val notes: List<Note>
		get() {
			timingGroup.tryRecalculateTimings()
			return timingGroup.notesInRange(range)
		}
	val items: List<Item>
		get() {
			timingGroup.tryRecalculateTimings()
			return timingGroup.itemsInRange(range)
		}

	fun scale(scale: Double, fromTime: Int? = null) {
		if (scale != 1.0) {
			val fromTime = maxOf(fromTime ?: 0, time)
			bpm *= scale
			when (fromTime) {
				time -> items
				in (time + 1)..toTime -> timingGroup.itemsInRange(fromTime..toTime)
				else -> mutableListOf()
			}.forEach {
				if (it is HoldLike) it.scale(scale)
				it.moveTo(((it.time - fromTime) / scale).roundToInt() + fromTime)
			}
		}
		timingGroup.needRecalculateTimings = true
	}

	companion object: ItemCompanion(ItemClass.TIMING) {
		override fun fromParams(params: List<String>) =
			Timing(params[0].toInt(), params[1].toDouble(), params[2].toDouble())

		val zero = Timing(0, 100.0, 4.28)
		val inf = Timing(Int.MAX_VALUE, 0.0, 0.0)

		fun zero(timing: Timing? = null) = timing?.copy(0) ?: zero
	}
}