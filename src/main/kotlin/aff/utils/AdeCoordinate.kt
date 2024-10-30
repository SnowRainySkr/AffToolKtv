package cn.snowrainyskr.aff.utils

import cn.snowrainyskr.aff.structure.item.note.enums.Lane
import kotlin.math.ln

data class AdeCoordinate(var x: Double, var y: Double, var z: Double) {
	operator fun minus(rhs: AdeCoordinate) = AdeCoordinate(x - rhs.x, y - rhs.y, z - rhs.z)

	companion object {
		fun posToXY(pos: Coordinate): Pair<Double, Double> {
			val x = 10.625 - 4.25 * pos.x
			val y = 1 + 2.25 * pos.y
			return Pair(x, y)
		}

		fun laneToX(lane: Lane) = 13.8125 - 2.125 * when (lane) {
			is Lane.IntLane -> lane.lane
			is Lane.FloatLane -> lane.lane
		}.toDouble()

		fun z(
			speed: Double,
			baseBpm: Double,
			bpm: Double,
			fromZ: Double,
			fromTime: Int,
			time: Int
		): Double {
			val speed = speed * minOf(bpm / baseBpm, 1.5)
			val deltaZ = (time - fromTime) * (16 * bpm / 240_000) * 2.1465 * speed
			return fromZ + minOf(deltaZ, 190_000 * (16 * baseBpm / 240_000) * 2.1465 * speed)
		}
	}
}
