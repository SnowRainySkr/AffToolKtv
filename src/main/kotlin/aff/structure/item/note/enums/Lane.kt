package cn.snowrainyskr.aff.structure.item.note.enums

import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnum
import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnumCompanion

sealed class Lane: ItemParamEnum<String> {
	class IntLane(val lane: Int): Lane() {
		override fun toParam() = lane.toString()
	}

	class FloatLane(val lane: Double): Lane() {
		override fun toParam() = String.format("%.2f", lane)
	}

	override fun equals(other: Any?) = if (other is Number) {
		when (this) {
			is IntLane -> lane == other
			is FloatLane -> lane == other
		}
	} else super.equals(other)

	override fun hashCode(): Int {
		return javaClass.hashCode()
	}

	override fun toString() = when (this) {
		is IntLane -> lane.toString()
		is FloatLane -> lane.toString()
	}

	companion object: ItemParamEnumCompanion<String> {
		override fun fromParam(param: String) =
			if ('.' in param) FloatLane(param.toDouble()) else IntLane(param.toInt())

		fun from(lane: Int) = IntLane(lane)
		fun from(lane: Double) = FloatLane(lane)
	}
}