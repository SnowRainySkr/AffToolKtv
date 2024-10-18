package cn.snowrainyskr.aff.structure.item.note.enums

import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnum
import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnumCompanion

enum class ArcColor : ItemParamEnum<Int> {
	BLUE, RED, GREEN;

	override fun toParam() = ordinal

	companion object : ItemParamEnumCompanion<Int> {
		override fun fromParam(param: Int) = entries.getOrElse(param) {
			throw RuntimeException("ArcColorParamConvertException")
		}
	}
}