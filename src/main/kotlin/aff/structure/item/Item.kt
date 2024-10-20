package cn.snowrainyskr.aff.structure.item

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.Arc
import cn.snowrainyskr.aff.structure.item.note.EnwidenArctap
import cn.snowrainyskr.aff.structure.item.note.Hold
import cn.snowrainyskr.aff.structure.item.note.SkyLine
import cn.snowrainyskr.aff.structure.item.note.Tap
import cn.snowrainyskr.aff.structure.item.sceneControl.SceneControl
import cn.snowrainyskr.aff.structure.timingGroup.TimingGroup

interface Item {
	var aff: Aff
	var timingGroup: TimingGroup
	var time: Int

	fun toTime(): Int = time

	fun toAffLine(): String

	fun moveTo(time: Int) {
		this.time = time
	}
	fun moveForward(dTime: Int) {
		time += dTime
	}

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

		fun fromAffLines(line: String) = ItemCompanion.fromAffLine(line)
	}
}