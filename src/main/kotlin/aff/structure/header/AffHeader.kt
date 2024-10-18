package cn.snowrainyskr.aff.structure.header

import cn.snowrainyskr.aff.utils.format

sealed interface AffHeader {
	val paramName: String
	var valueAsString: String

	fun toAffLine() = "$paramName:$valueAsString"

	companion object {
		fun fromAffHeaderLines(lines: List<String>) = lines.map {
			val (name, value) = it.format().split(":", limit = 2)
			create(name, value)
		}.toMutableSet()

		fun create(name: String, value: String) = when (name) {
			AudioOffset.PARAM_NAME -> AudioOffset(value.toInt())
			TimingPointDensityFactor.PARAM_NAME -> TimingPointDensityFactor(value.toDouble())
			else -> DefaultAffHeader(name, value)
		}
	}
}