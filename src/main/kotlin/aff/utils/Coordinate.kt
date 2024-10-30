package cn.snowrainyskr.aff.utils

import cn.snowrainyskr.aff.structure.item.note.enums.ArcEaseLike

data class Coordinate(var x: Double, var y: Double) {
	override fun toString() = String.format("%.2f,%2f", x, y)

	fun mirror() {
		x = 1.0 - x
	}

	val leftBoundary
		get() = Coordinate(x - 0.25, y)

	val rightBoundary
		get() = Coordinate(x + 0.25, y)

	infix fun to(rhs: Coordinate) = ArcEaseLike.PosRange(this, rhs)
	infix fun mid(rhs: Coordinate) = Coordinate((x + rhs.x) / 2, (y + rhs.y) / 2)

	companion object {
		val leftTop = Coordinate(0.0, 1.0)
		val rightTop = Coordinate(1.0, 1.0)
		val centerTop = Coordinate(0.5, 1.0)
		val leftBottom = Coordinate(1.5, 0.0)
		val rightBottom = Coordinate(-0.5, 0.0)
		val centerBottom = Coordinate(0.5, 0.0)

		val leftLaneX = arrayOf(-1.0, -0.5, 0.0, 0.5, 1.0, 1.5)
		val rightLaneX = arrayOf(-0.5, 0.0, 0.5, 1.0, 1.5, 2.0)
		val centerLaneX = Array(leftLaneX.size) { (leftLaneX[it] + rightLaneX[it]) / 2 }
		const val CENTER_X = 0.5
		const val BOTTOM_Y = 0.0
		const val CENTER_Y = 0.5
		const val TOP_Y = 1.0
	}
}
