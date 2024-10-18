package cn.snowrainyskr.aff.structure.item

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.enums.CameraEasing
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.utils.Vector3

data class Camera(
	override var time: Int,
	val movePx: Vector3<Int>,
	val angle: Vector3<Double>,
	val easing: CameraEasing,
	val duration: Int
) : Item {
	override lateinit var aff: Aff
	
	override fun toAffLine() =
		"$itemClass($time,${movePx.toParamInt()},${angle.toParamFloat()},${easing.toParam()},$duration);"

	override val itemClass: ItemClass
		get() = Camera.itemClass

	companion object : ItemCompanion(ItemClass.CAMERA) {
		private fun Vector3<Int>.toParamInt() = "$x,$y,$z"
		private fun Vector3<Double>.toParamFloat() = String.format("%.2f,%.2f,%.2f", x, y, z)

		override fun fromParams(params: List<String>) = Camera(
			params[0].toInt(),
			Vector3(params[1].toInt(), params[2].toInt(), params[3].toInt()),
			Vector3(params[4].toDouble(), params[5].toDouble(), params[6].toDouble()),
			CameraEasing.fromParam(params[7]),
			params[8].toInt()
		)
	}
}