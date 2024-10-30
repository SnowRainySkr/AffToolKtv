package cn.snowrainyskr.aff.structure.item.note.enums

import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnum
import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnumCompanion

sealed class Lane: ItemParamEnum<String> {
	class IntLane(var lane: Int): Lane() {
		override fun toParam() = lane.toString()
		override fun mirror() {
			lane = 5 - lane
		}

		override fun copy() = IntLane(lane)
	}

	class FloatLane(var lane: Double): Lane() {
		override fun toParam() = String.format("%.2f", lane)
		override fun mirror() {
			lane = 5.0 - lane
		}

		override fun copy() = FloatLane(lane)
	}

	abstract fun copy(): Lane

	abstract fun mirror()

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