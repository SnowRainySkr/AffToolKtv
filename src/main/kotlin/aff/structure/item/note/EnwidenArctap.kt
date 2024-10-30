package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.Item
import cn.snowrainyskr.aff.structure.item.ItemCompanion
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup
import cn.snowrainyskr.aff.utils.AdeCoordinate
import cn.snowrainyskr.aff.utils.Coordinate
import cn.snowrainyskr.aff.utils.XRange
import cn.snowrainyskr.aff.utils.format

data class EnwidenArctap(
	override var time: Int, val range: XRange, var y: Double, override var hitSound: String = "none"
) : CanSetHitSound, SkyNote {
	override lateinit var aff: Aff
	override lateinit var timingGroup: TimingGroup
	override lateinit var adeCoordinate: AdeCoordinate

	override fun toAffLine() =
		"$itemClass($time,$time,${range.left.format()},${range.right.format()},s,$y,$y,3,$hitSound,false);"

	override val itemClass: ItemClass
		get() = EnwidenArctap.itemClass

	override val judgments: List<Int>
		get() = listOf(time)

	override fun mirror() = range.mirror()
	override fun copy() = EnwidenArctap(time, range.copy(), y, "$hitSound")
	override val pos: Coordinate
		get() = Coordinate(range.center, y)

	override fun moveHorizontal(deltaX: Double, deltaY: Double) {
		range.left += deltaX
		range.right += deltaX
		y += deltaX
	}

	companion object : ItemCompanion(ItemClass.ENWIDEN_ARCTAP) {
		override fun fromParams(params: List<String>) = EnwidenArctap(
			params[0].toInt(),
			XRange(params[2].toDouble(), params[3].toDouble()),
			params[5].toDouble(),
			params[8]
		)
	}
}
