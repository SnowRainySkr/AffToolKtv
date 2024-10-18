package cn.snowrainyskr.aff.structure.header

data class TimingPointDensityFactor(var value: Double) : AffHeader {
	override val paramName: String
		get() = PARAM_NAME
	override var valueAsString: String
		get() = value.toString()
		set(value) {
			this.value = value.toDouble()
		}

	companion object {
		const val PARAM_NAME = "TimingPointDensityFactor"
	}
}
