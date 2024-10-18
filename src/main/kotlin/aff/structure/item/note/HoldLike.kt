package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.item.Item

sealed interface HoldLike : Item {
	var toTime: Int
}