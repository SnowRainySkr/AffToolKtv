package cn.snowrainyskr.aff.utils

data class Coordinate(var x: Double, var y: Double) {
	override fun toString() = String.format("%.2f,%2f", x, y)

	companion object {
		val leftUp = Coordinate(0.0, 1.0)
		val rightUp = Coordinate(1.0, 1.0)
		val middleUp = Coordinate(0.5, 1.0)
	}
}
