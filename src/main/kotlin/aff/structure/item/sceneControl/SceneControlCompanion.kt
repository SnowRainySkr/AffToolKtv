package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.item.ItemCompanion
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.SceneControlClass

abstract class SceneControlCompanion(val sceneControlClass: SceneControlClass) :
	ItemCompanion(ItemClass.SCENE_CONTROL) {
	init {
		sceneControlCompanionMap.put(sceneControlClass, this)
	}

	companion object {
		val sceneControlCompanionMap = mutableMapOf<SceneControlClass, SceneControlCompanion>()

		private val e = RuntimeException("SceneControlConvertException")

		fun fromParams(params: List<String>) =
			sceneControlCompanionMap[SceneControlClass.fromParam(params[1])]?.fromParams(params) ?: throw e
	}
}