package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.item.Item

sealed interface Note : Item {
	val judgments: List<Int>
}