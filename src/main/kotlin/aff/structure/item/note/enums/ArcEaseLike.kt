package cn.snowrainyskr.aff.structure.item.note.enums

import cn.snowrainyskr.aff.composite.ease.EaseFunction
import cn.snowrainyskr.aff.structure.item.note.ArcLike
import cn.snowrainyskr.aff.utils.Coordinate
import kotlin.math.roundToInt

interface ArcEaseLike {
	val xEase: EaseFunction
	val yEase: EaseFunction

	operator fun plus(rhs: ArcEaseLike) = object: ArcEaseLike {
		override val xEase = this@ArcEaseLike.xEase + rhs.xEase
		override val yEase = this@ArcEaseLike.yEase + rhs.yEase
	}

	data class TimeRange(val time: Int, val toTime: Int)
	data class PosRange(val pos: Coordinate, val toPos: Coordinate) {
		val x
			get() = pos.x..toPos.x
		val y
			get() = pos.y..toPos.y

		fun posAt(ratio: Double, ease: ArcEaseLike): Coordinate {
			fun ClosedFloatingPointRange<Double>.linearInterpolate(r: Double) = (1 - r) * start + r * endInclusive
			return Coordinate(x.linearInterpolate(ease.xEase(ratio)), y.linearInterpolate(ease.yEase(ratio)))
		}
	}

	fun makeSplitList(arc: ArcLike, count: Int): List<Pair<TimeRange, PosRange>> =
		makeSplitList(arc.timeRange, arc.posRange, count)

	fun <R> makeSplitList(arc: ArcLike, count: Int, transform: (TimeRange, PosRange) -> R) =
		makeSplitList(arc, count).map { (timeRange, posRange) -> transform(timeRange, posRange) }.toMutableList()

	fun makeSplitList(timeRange: IntRange, posRange: PosRange, count: Int): List<Pair<TimeRange, PosRange>> {
		val (times, xs) = xEase.makeList(timeRange, posRange.x, count).unzip()
		val (_, ys) = yEase.makeList(timeRange, posRange.y, count).unzip()
		return List(count) {
			Pair(
				TimeRange(times[it].roundToInt(), times[it + 1].roundToInt()),
				PosRange(Coordinate(xs[it], ys[it]), Coordinate(xs[it + 1], ys[it + 1]))
			)
		}
	}

	fun <R> makeSplitList(timeRange: IntRange, posRange: PosRange, count: Int, transform: (TimeRange, PosRange) -> R) =
		makeSplitList(timeRange, posRange, count).map { (timeRange, posRange) ->
			transform(timeRange, posRange)
		}.toMutableList()
}