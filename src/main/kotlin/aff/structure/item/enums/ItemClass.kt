package cn.snowrainyskr.aff.structure.item.enums

enum class ItemClass(val itemName: String) {
	TIMING("timing"), CAMERA("camera"), SCENE_CONTROL("scenecontrol"),
	TAP(""), HOLD("hold"), ARC("arc"), ENWIDEN_ARCTAP("arc"), SKYLINE("arc");

	override fun toString() = itemName

	companion object {
		val itemClassMap = entries.associateBy { it.toString() }.toMap()

		fun fromParam(param: String) = itemClassMap.getOrElse(param) {
			throw RuntimeException("ItemClassParamConvertException")
		}
	}
}