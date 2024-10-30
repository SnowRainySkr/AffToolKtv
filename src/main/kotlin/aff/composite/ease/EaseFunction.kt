package cn.snowrainyskr.aff.composite.ease

import cn.snowrainyskr.aff.utils.Point


data class EaseFunction(val func: (Double) -> Double) {
	operator fun invoke(t: Double) = func(t)

	var factor = 1

	operator fun plus(rhs: EaseFunction) = EaseFunction { t ->
		(this(t) * factor + rhs(t) * rhs.factor) / (factor + rhs.factor)
	}.apply { factor = this@EaseFunction.factor + rhs.factor }

	companion object {
		fun bisectionSearch(a: Double, it: Double, f: (Double, Double) -> Double): Double {
			var (lower, upper) = Pair(a, 1.0)
			var midpoint: Double
			while ((upper - lower) / 2.0 > 1e-10) {
				midpoint = lower + (upper - lower) / 2.0
				val value = f(midpoint, it)
				when {
					value == 0.0 -> return midpoint
					f(lower, it) * value < 0.0 -> upper = midpoint
					else -> lower = midpoint
				}
			}
			return lower + (upper - lower) / 2.0
		}

		fun <T, U> bezier(
			tRange: ClosedRange<T>,
			valueRange: ClosedRange<U>,
			x: T,
			p: Point<Number> = Point(1 / 3.0, 0.0),
			q: Point<Number> = Point(2 / 3.0, 1.0)
		): Double where T: Number, T: Comparable<T>, U: Number, U: Comparable<U> {
			val zeroT = tRange.start.toDouble()
			val tRangeLength = tRange.endInclusive.toDouble() - zeroT
			val zeroValue = valueRange.start.toDouble()
			val valueRangeLength = valueRange.endInclusive.toDouble() - zeroValue
			val x1 = p.x.toDouble()
			val y1 = p.y.toDouble()
			val x2 = q.x.toDouble()
			val y2 = q.y.toDouble()
			val p1 = 3 * x1 - 3 * x2 + 1
			val p2 = 3 * x2 - 6 * x1
			val p3 = 3 * x1
			val f = { t: Double, xt: Double -> ((p1 * t + p2) * t + p3) * t - xt }
			val y = { t: Double -> (3 * ((1 - t) * y1 + t * y2) * (1 - t) + t * t) * t }
			val it = (x.toDouble() - tRange.start.toDouble()) / tRange.endInclusive.toDouble()
			return y(bisectionSearch(0.0, it, f)) * valueRangeLength + zeroValue
		}

		fun <T, U> makeBezierList(
			tRange: ClosedRange<T>,
			valueRange: ClosedRange<U>,
			count: Int,
			p: Point<Number> = Point(1 / 3.0, 0.0),
			q: Point<Number> = Point(2 / 3.0, 1.0),
		): MutableList<Pair<Double, Double>> where T: Number, T: Comparable<T>, U: Number, U: Comparable<U> {
			val zeroT = tRange.start.toDouble()
			val tRangeLength = tRange.endInclusive.toDouble() - zeroT
			val zeroValue = valueRange.start.toDouble()
			val valueRangeLength = valueRange.endInclusive.toDouble() - zeroValue
			val x1 = p.x.toDouble()
			val y1 = p.y.toDouble()
			val x2 = q.x.toDouble()
			val y2 = q.y.toDouble()
			val p1 = 3 * x1 - 3 * x2 + 1
			val p2 = 3 * x2 - 6 * x1
			val p3 = 3 * x1
			val f = { t: Double, xt: Double -> ((p1 * t + p2) * t + p3) * t - xt }
			val y = { t: Double -> (3 * ((1 - t) * y1 + t * y2) * (1 - t) + t * t) * t }
			var x = 0.0
			return mutableListOf(Pair(zeroT, zeroValue)).apply {
				addAll(List(count) { (it + 1).toDouble() / count }.map {
					Pair(it * tRangeLength + zeroT, y(bisectionSearch(x, it, f)) * valueRangeLength + zeroValue)
				})
			}
		}

		inline fun <T, U, R> makeBezierList(
			tRange: ClosedRange<T>,
			valueRange: ClosedRange<U>,
			count: Int,
			p: Point<Number> = Point(1 / 3.0, 0.0),
			q: Point<Number> = Point(2 / 3.0, 1.0),
			transform: (Double, Double) -> R
		): MutableList<R> where T: Number, T: Comparable<T>, U: Number, U: Comparable<U> =
			makeBezierList(tRange, valueRange, count, p, q).map { (t, v) -> transform(t, v) }.toMutableList()
	}

	fun makeList(count: Int) = List(count + 1) { it.toDouble() / count }.associateWith { this(it) }.toList()

	fun <T, U> makeList(
		tRange: ClosedRange<T>, valueRange: ClosedRange<U>, count: Int
	): List<Pair<Double, Double>> where T: Number, T: Comparable<T>, U: Number, U: Comparable<U> {
		val zeroT = tRange.start.toDouble()
		val tRangeLength = tRange.endInclusive.toDouble() - zeroT
		val zeroValue = valueRange.start.toDouble()
		val valueRangeLength = valueRange.endInclusive.toDouble() - zeroValue
		return makeList(count).map { (t, value) ->
			Pair(t * tRangeLength + zeroT, value * valueRangeLength + zeroValue)
		}.toList()
	}

	inline fun <T, U, R> makeList(
		tRange: ClosedRange<T>, valueRange: ClosedRange<U>, count: Int, transform: (Double, Double) -> R
	): MutableList<R> where T: Number, T: Comparable<T>, U: Number, U: Comparable<U> =
		makeList(tRange, valueRange, count).map { (t, v) -> transform(t, v) }.toMutableList()

}