package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.item.Item
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.SceneControlClass

sealed interface SceneControl : Item {
	override val itemClass: ItemClass
		get() = ItemClass.SCENE_CONTROL

	val sceneControlClass: SceneControlClass

	companion object {
		init {
			ArcahvDebris
			ArcahvDistort
			EnwidenCamera
			EnwidenLanes
			HideGroup
			RedLine
			TrackDisplay
			TrackHide
			TrackShow
		}
	}
}