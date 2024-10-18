package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.SceneControlClass

data class TrackHide(override var time: Int) : SceneControl, TrackDisplaySceneControl by TrackDisplay(time, 1.0, 0) {
	override lateinit var aff: Aff

	override fun toAffLine() = "$itemClass($time,$sceneControlClass);"

	override val sceneControlClass: SceneControlClass
		get() = TrackHide.sceneControlClass

	companion object : SceneControlCompanion(SceneControlClass.TrackHide) {
		override fun fromParams(params: List<String>) = TrackHide(params[0].toInt())
	}
}