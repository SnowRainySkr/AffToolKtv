package cn.snowrainyskr.aff.structure.item

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.Arc
import cn.snowrainyskr.aff.structure.item.note.EnwidenArctap
import cn.snowrainyskr.aff.structure.item.note.Hold
import cn.snowrainyskr.aff.structure.item.note.SkyLine
import cn.snowrainyskr.aff.structure.item.note.Tap
import cn.snowrainyskr.aff.structure.item.sceneControl.SceneControl

interface Item {
	var aff: Aff
	var time: Int

	fun toAffLine(): String

	val itemClass: ItemClass

	companion object {
		init {
			Tap
			Hold
			Arc
			SkyLine
			EnwidenArctap
			Timing
			Camera
			SceneControl
		}
	}
}