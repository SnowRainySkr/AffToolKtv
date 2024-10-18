package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.item.note.enums.ArcEasing
import cn.snowrainyskr.aff.utils.Coordinate

sealed interface ArcLike : HoldLike {
	val pos: Coordinate
	val toPos: Coordinate
	var easing: ArcEasing
}