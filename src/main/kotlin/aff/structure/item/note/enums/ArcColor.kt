package cn.snowrainyskr.aff.structure.item.note.enums

import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnum
import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnumCompanion

enum class ArcColor: ItemParamEnum<Int> {
	BLUE {
		override val mirror
			get() = RED
	},
	RED {
		override val mirror
			get() = BLUE
	},
	GREEN {
		override val mirror
			get() = GREEN
	};

	abstract val mirror: ArcColor

	override fun toParam() = ordinal

	companion object: ItemParamEnumCompanion<Int> {
		override fun fromParam(param: Int) = entries.getOrElse(param) {
			throw RuntimeException("ArcColorParamConvertException")
		}
	}
}