package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.SceneControlClass
import cn.snowrainyskr.aff.utils.format

data class RedLine(override var time: Int, var secondsForRedLine: Double) : SceneControl {
	override lateinit var aff: Aff

	override fun toAffLine() = "$itemClass($time,$sceneControlClass,${secondsForRedLine.format()},0);"

	override val sceneControlClass: SceneControlClass
		get() = RedLine.sceneControlClass

	companion object : SceneControlCompanion(SceneControlClass.RedLine) {
		override fun fromParams(params: List<String>) = RedLine(params[0].toInt(), params[2].toDouble())
	}
}