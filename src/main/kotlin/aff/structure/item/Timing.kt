package cn.snowrainyskr.aff.structure.item

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.utils.format

data class Timing(override var time: Int, var bpm: Double, var beats: Double) : Item {
	override lateinit var aff: Aff

	override fun toAffLine() = "$itemClass($time,${bpm.format()},${beats.format()});"

	override val itemClass: ItemClass
		get() = Timing.itemClass

	lateinit var range: IntRange

	companion object : ItemCompanion(ItemClass.TIMING) {
		override fun fromParams(params: List<String>) =
			Timing(params[0].toInt(), params[1].toDouble(), params[2].toDouble())

		val InfTiming = Timing(Int.MAX_VALUE, 0.0, 0.0)
	}
}