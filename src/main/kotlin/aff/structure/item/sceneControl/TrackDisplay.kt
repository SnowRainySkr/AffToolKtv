package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.SceneControlClass
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup
import cn.snowrainyskr.aff.utils.format

data class TrackDisplay(
	override var time: Int, override var secondsForTransform: Double, override var toAlpha: Int
) : SceneControl, TrackDisplaySceneControl {
	override lateinit var aff: Aff
	override lateinit var timingGroup: TimingGroup

	override fun toAffLine() = "$itemClass($time,$sceneControlClass,${secondsForTransform.format()},$toAlpha);"

	override val sceneControlClass: SceneControlClass
		get() = TrackDisplay.sceneControlClass

	companion object : SceneControlCompanion(SceneControlClass.TrackDisplay) {
		override fun fromParams(params: List<String>) =
			TrackDisplay(params[0].toInt(), params[2].toDouble(), params[3].toInt())
	}
}
