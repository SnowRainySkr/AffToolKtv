package cn.snowrainyskr.aff.composite.ease

open class Ease(inFunc: (Double) -> Double): EaseLike {
	override val easeIn = EaseFunction(inFunc)
	override val easeOut = EaseFunction { t: Double -> 1 - inFunc(1 - t) }
	override val easeInOut = EaseFunction { t: Double ->
		if (t < 0.5) 0.5 * easeIn(t * 2)
		else 1 - easeIn((1 - t) * 2)
	}
}