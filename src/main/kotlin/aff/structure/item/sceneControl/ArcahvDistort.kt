package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.SceneControlClass

data class ArcahvDistort(override var time: Int) : SceneControl {
	override lateinit var aff: Aff

	override fun toAffLine() = "$itemClass($time,$sceneControlClass);"

	override val sceneControlClass: SceneControlClass
		get() = ArcahvDistort.sceneControlClass

	companion object : SceneControlCompanion(SceneControlClass.ArcahvDistort) {
		override fun fromParams(params: List<String>) = ArcahvDistort(params[0].toInt())
	}
}
