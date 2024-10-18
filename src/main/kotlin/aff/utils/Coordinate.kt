package cn.snowrainyskr.aff.utils

data class Coordinate(var x: Double, var y: Double) {
	override fun toString() = String.format("%.2f,%2f", x, y)
}
