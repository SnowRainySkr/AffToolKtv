package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.structure.item.Item
import cn.snowrainyskr.aff.utils.AdeCoordinate

sealed interface Note : Item {
	val judgments: List<Int>

	var adeCoordinate: AdeCoordinate

	fun mirror()

	fun copy(): Note
}