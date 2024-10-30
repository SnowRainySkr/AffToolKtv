package cn.snowrainyskr.aff.composite.item

import cn.snowrainyskr.aff.structure.item.note.ArcLike
import cn.snowrainyskr.aff.utils.Coordinate

interface CompositeArcLike: CompositeItem {
	fun moveHorizontal(deltaX: Double, deltaY: Double = 0.0) {
		items.forEach { (it as ArcLike).moveHorizontal(deltaX, deltaY) }
	}

	companion object {
		enum class FilterOption(val filter: (Int, ArcLike) -> Boolean) {
			KeepEventh({ index, _ -> index % 2 == 0 }),
			KeepOddth({ index, _ -> index % 2 == 1 }),
		}
	}
}

typealias FilterOption = CompositeArcLike.Companion.FilterOption