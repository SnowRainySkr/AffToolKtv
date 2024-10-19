package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.HideGroupType
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.SceneControlClass
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup

data class HideGroup(override var time: Int, var type: HideGroupType) : SceneControl {
	override lateinit var aff: Aff
	override lateinit var timingGroup: TimingGroup

	override fun toAffLine() = "$itemClass($time,$sceneControlClass,0.00,${type.toParam()});"

	override val sceneControlClass: SceneControlClass
		get() = HideGroup.sceneControlClass

	companion object : SceneControlCompanion(SceneControlClass.HideGroup) {
		override fun fromParams(params: List<String>) =
			HideGroup(params[0].toInt(), HideGroupType.fromParam(params[3].toInt()))
	}
}
