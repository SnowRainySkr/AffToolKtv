package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.item.note.enums.Lane

sealed interface GroundNote: Note {
	val lane: Lane

	override fun mirror() = lane.mirror()
}