package cn.snowrainyskr.aff.structure.header

data class AudioOffset(var value: Int) : AffHeader {
	override val paramName: String
		get() = PARAM_NAME
	override var valueAsString: String
		get() = value.toString()
		set(value) {
			this.value = value.toInt()
		}

	companion object {
		const val PARAM_NAME = "AudioOffset"
	}
}
