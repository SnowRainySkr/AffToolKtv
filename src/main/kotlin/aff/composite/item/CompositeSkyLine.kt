package cn.snowrainyskr.aff.composite.item

import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.SkyLine
import cn.snowrainyskr.aff.structure.item.note.enums.ArcEaseLike
import cn.snowrainyskr.aff.structure.item.note.enums.ArcEasings
import cn.snowrainyskr.aff.utils.Coordinate

class CompositeSkyLine(val skyLines: MutableList<SkyLine>): CompositeArcLike {
	constructor(skyLine: SkyLine, count: Int): this(skyLine.split(count) as MutableList)

	constructor(
		time: Int,
		toTime: Int,
		pos: Coordinate,
		toPos: Coordinate,
		ease: ArcEaseLike,
		count: Int,
		filter: (Int, SkyLine) -> Boolean = { _, _ -> true }
	): this(ease.makeSplitList(time..toTime, pos to toPos, count) { (time, toTime), (pos, toPos) ->
		SkyLine(time, toTime, pos, toPos, ArcEasings.S)
	}.filterIndexed(filter).toMutableList())

	constructor(
		time: Int,
		toTime: Int,
		pos: Coordinate,
		toPos: Coordinate,
		ease: ArcEaseLike,
		count: Int,
		filterOption: FilterOption
	): this(ease.makeSplitList(time..toTime, pos to toPos, count) { (time, toTime), (pos, toPos) ->
		SkyLine(time, toTime, pos, toPos, ArcEasings.S)
	}.filterIndexed(filterOption.filter).toMutableList())

	override val size: Int
		get() = skyLines.size

	override val items: Sequence<SkyLine>
		get() = skyLines.asSequence()

	override val itemClass: ItemClass
		get() = Companion.itemClass

	operator fun invoke(time: Int) = skyLines.find { time in it.timeRange }


	fun posAt(time: Int) = this(time)!!.posAt(time)

		companion object: CompositeItemCompanion(ItemClass.SKYLINE)
}