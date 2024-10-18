package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.SceneControlClass

data class TrackShow(override var time: Int) : SceneControl, TrackDisplaySceneControl by TrackDisplay(time, 1.0, 255) {
	override lateinit var aff: Aff

	override fun toAffLine() = "$itemClass($time,$sceneControlClass);"

	override val sceneControlClass: SceneControlClass
		get() = TrackShow.sceneControlClass

	companion object : SceneControlCompanion(SceneControlClass.TrackShow) {
		override fun fromParams(params: List<String>) = TrackShow(params[0].toInt())
	}
}