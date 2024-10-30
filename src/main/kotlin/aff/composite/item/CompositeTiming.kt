package cn.snowrainyskr.aff.composite.item

import cn.snowrainyskr.aff.composite.ease.EaseFunction
import cn.snowrainyskr.aff.composite.ease.EaseFunction.Companion.makeBezierList
import cn.snowrainyskr.aff.structure.item.Timing
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.utils.Point
import kotlin.math.roundToInt

class CompositeTiming(val timings: MutableList<Timing>): CompositeItem {
	constructor(
		timeRange: IntRange,
		bpmBoundary: ClosedFloatingPointRange<Double>,
		count: Int,
		ease: EaseFunction,
		beats: Double = 4.0
	): this(ease.makeList(timeRange, bpmBoundary, count) { time, bpm -> Timing(time.roundToInt(), bpm, beats) })

	constructor(
		timeRange: IntRange,
		bpmBoundary: ClosedFloatingPointRange<Double>,
		count: Int,
		beats: Double = 4.0,
		p: Point<Number> = Point(1 / 3.0, 0.0),
		q: Point<Number> = Point(2 / 3.0, 1.0)
	): this(makeBezierList(timeRange, bpmBoundary,count, p, q) { time, bpm -> Timing(time.roundToInt(), bpm, beats) })

	override val size: Int
		get() = timings.size

	override val items
		get() = timings.asSequence()

	override val itemClass: ItemClass
		get() = Companion.itemClass

	companion object: CompositeItemCompanion(ItemClass.TIMING)
}