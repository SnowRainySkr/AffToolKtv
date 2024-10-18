package cn.snowrainyskr.aff.structure

import cn.snowrainyskr.aff.structure.item.Item

enum class AffSortOptions {
	SortByClass {
		override val sorter: (Item) -> Int = {
			it.itemClass.ordinal
		}
		override val another: AffSortOptions
			get() = SortByTime
	},
	SortByTime {
		override val sorter: (Item) -> Int = {
			it.time
		}
		override val another: AffSortOptions
			get() = SortByClass
	};

	abstract val sorter: (Item) -> Int
	abstract val another: AffSortOptions
}