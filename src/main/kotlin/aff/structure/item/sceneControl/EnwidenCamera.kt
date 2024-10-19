package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.EnwidenControlType
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.SceneControlClass
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup
import cn.snowrainyskr.aff.utils.format

data class EnwidenCamera(
	override var time: Int, override var duration: Double, override var type: EnwidenControlType
) : SceneControl, EnwidenControl {
	override lateinit var aff: Aff
	override lateinit var timingGroup: TimingGroup

	override fun toAffLine() = "$itemClass($time,$sceneControlClass,${duration.format()},${type.toParam()});"

	override val sceneControlClass: SceneControlClass
		get() = EnwidenCamera.sceneControlClass

	companion object : SceneControlCompanion(SceneControlClass.EnwidenCamera) {
		override fun fromParams(params: List<String>) = EnwidenCamera(
			params[0].toInt(), params[2].toDouble(), EnwidenControlType.fromParam(params[3].toInt())
		)
	}
}