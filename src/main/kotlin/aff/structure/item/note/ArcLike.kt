package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.item.note.enums.ArcEaseLike
import cn.snowrainyskr.aff.structure.item.note.enums.ArcEasings
import cn.snowrainyskr.aff.utils.AdeCoordinate
import cn.snowrainyskr.aff.utils.Coordinate
import cn.snowrainyskr.aff.utils.find
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

sealed interface ArcLike: HoldLike, SkyNote, HoldJudgmentLike {
	val toPos: Coordinate
	var easing: ArcEasings

	val posRange
		get() = ArcEaseLike.PosRange(pos, toPos)

	override fun mirror() {
		pos.mirror()
		toPos.mirror()
	}

	override fun moveHorizontal(deltaX: Double, deltaY: Double) {
		pos.x += deltaX
		pos.y += deltaY
		toPos.x += deltaX
		toPos.y += deltaY
	}

	fun ratio(time: Int) = (time - this.time) / length.toDouble()

	fun posAt(time: Int) = posRange.posAt(ratio(time), easing)

	val next: ArcLike?

	fun split(count: Int): List<ArcLike>

	val euclideanLengthInfo: Map<Int, Double>
		get() {
			var (dx, dy, dz) = toAdeCoordinate - adeCoordinate
			fun squareRoot(vararg params: Number) = sqrt(params.sumOf { it.toDouble().pow(2) })
			val toTimeBpm = timingGroup.timings.find(toTime).bpm
			if (bpm!! / aff.baseBpm.bpm >= 2.0 && toTimeBpm < bpm!! / 1.995 ) {
				dz *= aff.baseBpm.bpm / bpm!!
			}
			when (easing) {
				ArcEasings.S -> {
					val euclideanLength = squareRoot(dx, dy, dz)
					return buildMap {
						var t = (time / 500 + 1) * 500
						while (t < toTime) {
							put(t, euclideanLength * (t - time).toDouble() / (toTime - time))
							t += 500
						}
						put(t, euclideanLength)
					}
				}
				else -> {
					val splitCount = ceil(length / 240_000.0 * 32 * bpm!!).toInt()
					val dz = dz / splitCount
					val fragmentEuclideanLength = (0..<splitCount).map { i ->
						fun time(i: Int) = (time + (i / splitCount.toDouble()) * length).roundToInt()
						fun pos(i: Int): AdeCoordinate {
							val (x, y) = AdeCoordinate.posToXY(posAt(time(i)))
							return AdeCoordinate(x, y, 0.0)
						}
						val (dx, dy) = pos(i + 1) - pos(i)
						Pair(time(i)..time(i + 1), squareRoot(dx, dy, dz))
					}.toList()
					return buildMap {
						var t = (time / 500 + 1) * 500
						var index = 0
						var s = 0.0
						while (t < toTime) {
							while (t !in fragmentEuclideanLength[index].first) {
								s += fragmentEuclideanLength[index].second
								index++
							}
							put(t, s + fragmentEuclideanLength[index].second * (t - time).toDouble() / (toTime - time))
							t += 500
						}
						put(t, fragmentEuclideanLength.sumOf { (_, length) -> length })
					}
				}
			}
		}
}