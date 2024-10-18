package cn.snowrainyskr.aff.structure.item.sceneControl.enums

import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnum
import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnumCompanion

enum class EnwidenControlType : ItemParamEnum<Int> {
	FADE_OUT, FADE_IN;

	override fun toParam() = ordinal

	companion object : ItemParamEnumCompanion<Int> {
		override fun fromParam(param: Int) = entries.getOrElse(param) {
			throw RuntimeException("EnwidenControlTypeParamConvertException")
		}
	}
}