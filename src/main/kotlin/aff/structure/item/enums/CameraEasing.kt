package cn.snowrainyskr.aff.structure.item.enums

enum class CameraEasing : ItemParamEnum<String> {
	QI, QO, RESET, LINEAR;

	override fun toParam() = name.lowercase()

	companion object : ItemParamEnumCompanion<String> {
		private val enumMap = entries.associateBy { it.toParam() }.toMap()

		override fun fromParam(param: String) = enumMap.getOrElse(param.lowercase()) { LINEAR }
	}
}