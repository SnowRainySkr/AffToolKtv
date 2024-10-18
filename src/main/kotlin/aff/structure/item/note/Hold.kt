package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.ItemCompanion
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.enums.Lane
import kotlin.math.absoluteValue

data class Hold(
	override var time: Int, override var toTime: Int, override var lane: Lane
): Note, HoldLike, GroundItem, HoldJudgmentLike {
	@Suppress("UNUSED")
	constructor(time: Int, toTime: Int, lane: Int): this(time, toTime, Lane.from(lane))
	@Suppress("UNUSED")
	constructor(time: Int, toTime: Int, lane: Double): this(time, toTime, Lane.from(lane))

	override lateinit var aff: Aff

	override fun toAffLine() = "$itemClass($time,$toTime,${lane.toParam()});"

	override val itemClass: ItemClass
		get() = Hold.itemClass

	override var bpm: Double? = null

	override val judgments: List<Int>
		get() = bpm?.absoluteValue?.let { bpm ->
			if (time == toTime) listOf()
			else {
				val interval = (60000.0 / bpm / (if (bpm >= 255) 1 else 2)).absoluteValue / aff.timingPointDensityFactor
				val total = ((toTime - time) / interval).toInt()
				if (total == 0) listOf((time + (toTime - time) * 0.5).toInt())
				else buildList {
					var t = time
					var n = 1
					while (true) {
						t = (time + n * interval).toInt()
						if (t <= toTime) add(t)
						if (total <= ++n) break
					}
				}
			}
		} ?: throw RuntimeException("HoldBpmNotInitializedException")

	companion object: ItemCompanion(ItemClass.HOLD) {
		override fun fromParams(params: List<String>) =
			Hold(params[0].toInt(), params[1].toInt(), Lane.fromParam(params[2]))
	}
}