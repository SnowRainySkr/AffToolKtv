package cn.snowrainyskr.aff.utils

data class XRange(var left: Double, var right: Double) {
	fun mirror() {
		left = 1.0 - left
		right = 1.0 - right
	}

	val center: Double
		get() = (left + right) / 2
}
