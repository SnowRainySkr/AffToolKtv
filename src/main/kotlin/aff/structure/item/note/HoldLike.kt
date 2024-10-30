package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.utils.AdeCoordinate
import kotlin.math.roundToInt

sealed interface HoldLike: Note {
	var toTime: Int
	var toAdeCoordinate: AdeCoordinate

	val length: Int
		get() = toTime - time

	val timeRange
		get() = time..toTime

	fun scale(scale: Double) {
		toTime = time + (length / scale).roundToInt()
	}


	fun include(time: Int) = this.time <= time && time <= toTime

	override fun moveTo(time: Int) {
		toTime -= this.time - time
		super.moveTo(time)
	}

	override fun moveForward(dTime: Int) {
		toTime += dTime
		super.moveForward(dTime)
	}

	override fun toTime() = toTime
}