package cn.snowrainyskr.aff.structure.timingGroup

interface TimingGroupSpecialEffect {
	fun toParam(): String

	companion object {
		fun fromParam(param: String) = param.lowercase().split("_").filter { it.isNotEmpty() }.map {
			when {
				it == "noinput" -> NoInput
				it == "fadingholds" -> FadingHolds
				it.startsWith("anglex") -> AngleX(it.substring(6).toInt())
				it.startsWith("angley") -> AngleY(it.substring(6).toInt())
				else -> throw RuntimeException("TimingGroupSpecialEffectParamConvertException")
			}
		}.toMutableSet()
	}
}