package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.ItemCompanion
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.enums.Lane
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup
import cn.snowrainyskr.aff.utils.AdeCoordinate

data class Tap(override var time: Int, override var lane: Lane): GroundNote {
	override lateinit var aff: Aff
	override lateinit var timingGroup: TimingGroup

	override lateinit var adeCoordinate: AdeCoordinate

	@Suppress("UNUSED")
	constructor(time: Int, lane: Int): this(time, Lane.from(lane))

	@Suppress("UNUSED")
	constructor(time: Int, lane: Double): this(time, Lane.from(lane))

	override fun toAffLine() = "$itemClass($time,${lane.toParam()});"

	override val itemClass: ItemClass
		get() = Tap.itemClass

	override val judgments: List<Int>
		get() = listOf(time)

	override fun copy() = Tap(time, lane.copy())

	companion object: ItemCompanion(ItemClass.TAP) {
		override fun fromParams(params: List<String>) = Tap(params[0].toInt(), Lane.fromParam(params[1]))
	}
}