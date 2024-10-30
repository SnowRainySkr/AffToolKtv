package cn.snowrainyskr.aff.composite.ease

interface EaseLike {
	val easeIn: EaseFunction
	val easeOut: EaseFunction
	val easeInOut: EaseFunction
}