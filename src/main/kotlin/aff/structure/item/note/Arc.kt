package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.ItemCompanion
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.enums.ArcColor
import cn.snowrainyskr.aff.structure.item.note.enums.ArcEasings
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup
import cn.snowrainyskr.aff.utils.AdeCoordinate
import cn.snowrainyskr.aff.utils.Coordinate
import cn.snowrainyskr.aff.utils.format
import kotlin.math.absoluteValue

data class Arc(
	override var time: Int,
	override var toTime: Int,
	override val pos: Coordinate,
	override var toPos: Coordinate,
	override var easing: ArcEasings,
	var color: ArcColor
): ArcLike, HoldJudgmentLike {
	override lateinit var aff: Aff
	override lateinit var timingGroup: TimingGroup
	override lateinit var adeCoordinate: AdeCoordinate
	override lateinit var toAdeCoordinate: AdeCoordinate

	override fun toAffLine(): String {
		val xs = "${pos.x.format()},${toPos.x.format()}"
		val ys = "${pos.y.format()},${toPos.y.format()}"
		val easing = if (time == toTime || pos == toPos) ArcEasings.S.toParam() else easing.toParam()
		return "$itemClass($time,$toTime,$xs,$easing,$ys,${color.toParam()},none,false);"
	}

	override val itemClass: ItemClass
		get() = Arc.itemClass

	override var bpm: Double? = null
	var isHead = true

	override val judgments: List<Int>
		get() = bpm?.let { bpm ->
			if (time == toTime) listOf()
			else bpm.absoluteValue.let { bpm ->
				if (bpm == 0.0) listOf(time)
				else {
					val interval =
						(60000.0 / bpm / (if (bpm >= 255) 1 else 2)).absoluteValue / aff.timingPointDensityFactor
					val u = if (isHead) 0 else 1
					val total = ((toTime - time) / interval).toInt()
					if (u xor 1 >= total) listOf((time + (toTime - time) * 0.5).toInt())
					else buildList {
						var t = time
						var n = u xor 1
						while (true) {
							t = (time + n * interval).toInt()
							if (t < toTime) add(t)
							if (total == ++n) break
						}
					}
				}
			}
		} ?: throw RuntimeException("ArcBpmNotInitializedException")

	override fun mirror() {
		color = color.mirror
		super<ArcLike>.mirror()
	}

	override val next: Arc?
		get() = aff.find<Arc>(toTime).find { toPos == it.pos }

	override fun copy() = Arc(time, toTime, pos.copy(), toPos.copy(), easing, color)

	override fun split(count: Int): List<ArcLike> =
		easing.makeSplitList(this, count) { (time, toTime), (pos, toPos) ->
			Arc(time, toTime, pos, toPos, ArcEasings.S, color)
		}

	fun toSkyLine() = SkyLine(time, toTime, pos.copy(), toPos.copy(), easing)

	companion object: ItemCompanion(ItemClass.ARC) {
		override fun fromParams(params: List<String>) = Arc(
			params[0].toInt(),
			params[1].toInt(),
			Coordinate(params[2].toDouble(), params[5].toDouble()),
			Coordinate(params[3].toDouble(), params[6].toDouble()),
			ArcEasings.fromParam(params[4]),
			ArcColor.fromParam(params[7].toInt())
		)

		@Suppress("UNUSED")
		fun blue(
			time: Int,
			toTime: Int,
			pos: Coordinate = Coordinate.leftTop,
			toPos: Coordinate = Coordinate.leftTop,
			easing: ArcEasings = ArcEasings.S
		) = Arc(time, toTime, pos, toPos, easing, ArcColor.BLUE)
		@Suppress("UNUSED")
		fun red(
			time: Int,
			toTime: Int,
			pos: Coordinate = Coordinate.rightTop,
			toPos: Coordinate = Coordinate.rightTop,
			easing: ArcEasings = ArcEasings.S
		) = Arc(time, toTime, pos, toPos, easing, ArcColor.RED)
	}
}