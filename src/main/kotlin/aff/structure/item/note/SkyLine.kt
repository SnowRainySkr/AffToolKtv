package cn.snowrainyskr.aff.structure.item.note

import cn.snowrainyskr.aff.composite.item.CompositeSkyLine
import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.ItemCompanion
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.enums.ArcEasings
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup
import cn.snowrainyskr.aff.utils.AdeCoordinate
import cn.snowrainyskr.aff.utils.Coordinate
import cn.snowrainyskr.aff.utils.format
import kotlin.math.roundToInt

data class SkyLine(
	override var time: Int,
	override var toTime: Int,
	override var pos: Coordinate,
	override var toPos: Coordinate,
	override var easing: ArcEasings,
	val arcTaps: MutableList<Int> = mutableListOf(),
	override var hitSound: String = "none"
): ArcLike, CanSetHitSound {
	init {
		arcTaps.sort()
	}

	override lateinit var aff: Aff
	override lateinit var timingGroup: TimingGroup
	override lateinit var adeCoordinate: AdeCoordinate
	override lateinit var toAdeCoordinate: AdeCoordinate

	override fun toAffLine(): String {
		val xs = "${pos.x.format()},${toPos.x.format()}"
		val ys = "${pos.y.format()},${toPos.y.format()}"
		val easing = if (time == toTime || pos == toPos) ArcEasings.S.toParam() else easing.toParam()
		val arcTaps = if (arcTaps.isEmpty()) "" else arcTaps.joinToString(",", "[", "]") { "arctap($it)" }
		return "$itemClass($time,$toTime,$xs,$easing,$ys,0,$hitSound,true)$arcTaps;"
	}

	override val itemClass: ItemClass
		get() = SkyLine.itemClass

	override fun moveTo(time: Int) {
		for (i in arcTaps.indices) arcTaps[i] -= this.time - time
		super<ArcLike>.moveTo(time)
	}

	override fun moveForward(dTime: Int) {
		for (i in arcTaps.indices) arcTaps[i] += dTime
		super<ArcLike>.moveForward(dTime)
	}

	fun clear() = arcTaps.clear()

	fun arctap(time: Int, coordinateTransform: (Double, Double) -> Coordinate): SkyLine {
		val currPos = posAt(time)
		val pos = coordinateTransform(currPos.x, currPos.y)
		return SkyLine.arctap(time, pos)
	}

	fun leftArctap(time: Int) = arctap(time) { x, y -> Coordinate(x - 0.25, y) }
	fun rightArctap(time: Int) = arctap(time) { x, y -> Coordinate(x + 0.25, y) }

	fun connectedArctap(time: Int, coordinateTransform: (Double, Double) -> Coordinate): CompositeSkyLine {
		val currPos = posAt(time)
		val pos = coordinateTransform(currPos.x, currPos.y)
		val arctap = SkyLine.arctap(time, pos)
		val connect = SkyLine(time, time, currPos, pos, ArcEasings.S)
		return CompositeSkyLine(mutableListOf(arctap, connect))
	}

	fun connectedLeftArctap(time: Int, coordinateTransform: (Double, Double) -> Coordinate): CompositeSkyLine {
		val currPos = posAt(time)
		val pos = coordinateTransform(currPos.x, currPos.y)
		val arctap = SkyLine.arctap(time, pos)
		val connect = SkyLine(time, time, currPos, pos.leftBoundary, ArcEasings.S)
		return CompositeSkyLine(mutableListOf(arctap, connect))
	}

	fun connectedRightArctap(time: Int, coordinateTransform: (Double, Double) -> Coordinate): CompositeSkyLine {
		val currPos = posAt(time)
		val pos = coordinateTransform(currPos.x, currPos.y)
		val arctap = SkyLine.arctap(time, pos)
		val connect = SkyLine(time, time, currPos, pos.rightBoundary, ArcEasings.S)
		return CompositeSkyLine(mutableListOf(arctap, connect))
	}

	fun connectAnotherArctap(rhs: SkyLine, vararg connectLineXOffsets: Double = doubleArrayOf(0.0)): CompositeSkyLine {
		val connectLines = connectLineXOffsets.map {
			SkyLine(time, rhs.time, Coordinate(pos.x - it, pos.y), Coordinate(rhs.pos.x - it, rhs.pos.y), ArcEasings.S)
		}.toMutableList()
		return CompositeSkyLine(connectLines)
	}

	override fun scale(scale: Double) {
		if (!isArcTap) {
			for (i in arcTaps.indices) arcTaps[i] = ((arcTaps[i] - time) / scale).roundToInt() + time
			super.scale(scale)
		}
	}

	val arctapCount: Int
		get() = arcTaps.size

	val isArcTap: Boolean
		get() = length == 1 && arctapCount == 1

	override val judgments: List<Int>
		get() = arcTaps

	override fun copy() = SkyLine(time, toTime, pos.copy(), toPos.copy(), easing,
		MutableList(arcTaps.size) { arcTaps[it] }, "$hitSound"
	)

	override val next: SkyLine?
		get() = aff.find<SkyLine>(toTime).find { toPos == it.pos }

	val set: CompositeSkyLine
		get() = CompositeSkyLine(buildList<SkyLine> {
			var curr = this@SkyLine
			add(curr)
			var next = curr.next
			while (next != null) {
				add(next)
				curr = next
				next = curr.next
			}
		}.toMutableList())

	override fun split(count: Int): List<SkyLine> =
		easing.makeSplitList(this, count) { (time, toTime), (pos, toPos) ->
			SkyLine(time, toTime, pos, toPos, ArcEasings.S)
		}

	override var bpm: Double? = null

	companion object: ItemCompanion(ItemClass.SKYLINE) {
		override fun fromParams(params: List<String>) = SkyLine(
			params[0].toInt(),
			params[1].toInt(),
			Coordinate(params[2].toDouble(), params[5].toDouble()),
			Coordinate(params[3].toDouble(), params[6].toDouble()),
			ArcEasings.fromParam(params[4]),
			arcTaps = params.drop(10).map { it.toInt() }.toMutableList(),
			hitSound = params[8]
		)

		fun arctap(time: Int, pos: Coordinate) = SkyLine(time, time + 1, pos, pos, ArcEasings.S, mutableListOf(time))

		fun connect(
			arc1: ArcLike,
			time: Int,
			arc2: ArcLike,
			ease: ArcEasings = ArcEasings.S,
			toTime: Int = time
		): SkyLine {
			val pos = arc1.posAt(time)
			val toPos = arc2.posAt(toTime)
			return SkyLine(time, toTime, pos, toPos, ease)
		}

		fun connectLeft(
			arc1: ArcLike,
			time: Int,
			arctap: ArcLike,
			ease: ArcEasings = ArcEasings.S,
			toTime: Int = time
		): SkyLine {
			val pos = arc1.posAt(time)
			val toPos = arctap.posAt(toTime).leftBoundary
			return SkyLine(time, toTime, pos, toPos, ease)
		}

		fun connectRight(
			arc1: ArcLike,
			time: Int,
			arctap: ArcLike,
			ease: ArcEasings = ArcEasings.S,
			toTime: Int = time
		): SkyLine {
			val pos = arc1.posAt(time)
			val toPos = arctap.posAt(toTime).rightBoundary
			return SkyLine(time, toTime, pos, toPos, ease)
		}
	}
}