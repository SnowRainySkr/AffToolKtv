package cn.snowrainyskr.aff.composite.ease

import java.lang.Math.pow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class Easings(ease: Ease): EaseLike by ease {
	Linear({ it }),
	Sine({ 1 - cos((it * Math.PI) / 2) }),
	Quad({ it * it }),
	Cubic({ it * it * it }),
	Quart({ it * it * it * it }),
	Quint({ it * it * it * it * it }),
	Expo({ if (it == 0.0) 0.0 else pow(2.0, 10 * it - 10) }),
	Circ({ 1 - sqrt(1 - it * it) }), Back({ (2.70158 * it - 1.70158) * it * it }),
	Elastic({
		when (it) {
			0.0, 1.0 -> it
			else -> -pow(2.0, 10 * it - 10) * sin((it * 10 - 10.75) * (2 * Math.PI) / 3)
		}
	}),
	Bounce({ t ->
		when (t) {
			in 0.0..<1 / 2.75 -> 7.5625 * t * t
			in 0.0..<2 / 2.75 -> 7.5625 * pow(t - 1.5 / 2.75, 2.0) + 0.75
			in 0.0..<2.5 / 2.75 -> 7.5625 * pow(t - 2.25 / 2.75, 2.0) + 0.9375
			else -> 7.5625 * pow(t - 2.625 / 2.75, 2.0) + 0.984375
		}
	});

	constructor(easeInFunc: (Double) -> Double): this(Ease(easeInFunc))
}