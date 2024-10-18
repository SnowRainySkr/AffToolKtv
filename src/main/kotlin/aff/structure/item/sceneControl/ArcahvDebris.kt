package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.SceneControlClass
import cn.snowrainyskr.aff.utils.format

data class ArcahvDebris(override var time: Int, var secondsForTransform: Double, var toAlpha: Int) : SceneControl {
	override lateinit var aff: Aff

	override fun toAffLine() = "$itemClass($time,$sceneControlClass,${secondsForTransform.format()},$toAlpha);"

	override val sceneControlClass: SceneControlClass
		get() = ArcahvDebris.sceneControlClass

	companion object : SceneControlCompanion(SceneControlClass.ArcahvDebris) {
		override fun fromParams(params: List<String>) =
			ArcahvDebris(params[0].toInt(), params[2].toDouble(), params[3].toInt())
	}
}