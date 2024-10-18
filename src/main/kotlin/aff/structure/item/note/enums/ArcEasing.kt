package cn.snowrainyskr.aff.structure.item.note.enums

import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnum
import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnumCompanion

enum class ArcEasing : ItemParamEnum<String> {
	B, S, SI, SO, SISI, SOSO, SISO, SOSI;

	override fun toParam() = name.lowercase()

	companion object : ItemParamEnumCompanion<String> {
		private val enumMap = entries.associateBy { it.toParam() }.toMap()

		override fun fromParam(param: String) = enumMap.getOrElse(param.lowercase()) {
			throw RuntimeException("ArcEasingParamConvertException")
		}
	}
}