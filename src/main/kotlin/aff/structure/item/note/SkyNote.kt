package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.utils.Coordinate

sealed interface SkyNote: Note {
	val pos: Coordinate

	fun moveHorizontal(deltaX: Double, deltaY: Double = 0.0)
}