package cn.snowrainyskr.aff.composite.item

import cn.snowrainyskr.aff.structure.item.Item
import cn.snowrainyskr.aff.structure.item.enums.ItemClass

interface CompositeItem {
	val size: Int
	val items: Sequence<Item>
	val itemClass: ItemClass
	val companion
		get() = CompositeItemCompanion.compositeItemCompanionMap[itemClass]

	fun moveForward(deltaTime: Int) = items.forEach { it.moveForward(deltaTime) }
	fun moveTo(time: Int) {
		val deltaTime = time - items.first().time
		moveForward(deltaTime)
	}

	companion object {
		init {
			CompositeTiming
			CompositeSkyLine
		}
	}
}