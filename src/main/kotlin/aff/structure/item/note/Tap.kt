package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.ItemCompanion
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.enums.Lane

data class Tap(override var time: Int, override var lane: Lane) : Note, GroundItem {
	@Suppress("UNUSED")
	constructor(time: Int, lane: Int): this(time, Lane.from(lane))
	@Suppress("UNUSED")
	constructor(time: Int, lane: Double): this(time, Lane.from(lane))

	override lateinit var aff: Aff

	override fun toAffLine() = "$itemClass($time,${lane.toParam()});"

	override val itemClass: ItemClass
		get() = Tap.itemClass

	override val judgments: List<Int>
		get() = listOf(time)

	companion object : ItemCompanion(ItemClass.TAP) {
		override fun fromParams(params: List<String>) = Tap(params[0].toInt(), Lane.fromParam(params[1]))
	}
}