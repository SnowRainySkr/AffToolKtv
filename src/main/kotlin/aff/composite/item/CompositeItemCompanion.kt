package cn.snowrainyskr.aff.composite.item

import cn.snowrainyskr.aff.structure.item.enums.ItemClass

abstract class CompositeItemCompanion(val itemClass: ItemClass) {
	init {
		compositeItemCompanionMap.put(itemClass, this)
	}

	companion object {
		val compositeItemCompanionMap = mutableMapOf<ItemClass, CompositeItemCompanion>()
	}
}