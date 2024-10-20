package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.ItemCompanion
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.enums.ArcEasing
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup
import cn.snowrainyskr.aff.utils.Coordinate
import cn.snowrainyskr.aff.utils.format

data class SkyLine(
	override var time: Int,
	override var toTime: Int,
	override var pos: Coordinate,
	override var toPos: Coordinate,
	override var easing: ArcEasing,
	val arcTaps: MutableList<Int> = mutableListOf(),
	override var hitSound: String = "none"
): Note, ArcLike, CanSetHitSound {
	init {
		arcTaps.sort()
	}

	override lateinit var aff: Aff
	override lateinit var timingGroup: TimingGroup

	override fun toAffLine(): String {
		val xs = "${pos.x.format()},${toPos.x.format()}"
		val ys = "${pos.y.format()},${toPos.y.format()}"
		val easing = if (time == toTime || pos == toPos) ArcEasing.S.toParam() else easing.toParam()
		val arcTaps = if (arcTaps.isEmpty()) "" else arcTaps.joinToString(",", "[", "]") { "arctap($it)" }
		return "$itemClass($time,$toTime,$xs,$easing,$ys,0,$hitSound,true)$arcTaps;"
	}

	override val itemClass: ItemClass
		get() = SkyLine.itemClass

	override fun moveTo(time: Int) {
		for (i in arcTaps.indices) arcTaps[i] -= this.time - time
		super<ArcLike>.moveTo(time)
	}

	override fun moveForward(dTime: Int) {
		for (i in arcTaps.indices) arcTaps[i] += dTime
		super<ArcLike>.moveForward(dTime)
	}

	val length: Int
		get() = toTime - time

	val arctapCount: Int
		get() = arcTaps.size

	val isArcTap: Boolean
		get() = length == 1 && arctapCount == 1

	override val judgments: List<Int>
		get() = arcTaps

	companion object: ItemCompanion(ItemClass.SKYLINE) {
		override fun fromParams(params: List<String>) = SkyLine(
			params[0].toInt(),
			params[1].toInt(),
			Coordinate(params[2].toDouble(), params[5].toDouble()),
			Coordinate(params[3].toDouble(), params[6].toDouble()),
			ArcEasing.fromParam(params[4]),
			arcTaps = params.drop(10).map { it.toInt() }.toMutableList(),
			hitSound = params[8]
		)

		fun arctap(time: Int, pos: Coordinate) = SkyLine(time, time + 1, pos, pos, ArcEasing.S, mutableListOf(time))
	}
}