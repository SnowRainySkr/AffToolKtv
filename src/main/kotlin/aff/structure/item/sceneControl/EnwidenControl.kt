package cn.snowrainyskr.aff.structure.item.sceneControl

import cn.snowrainyskr.aff.structure.item.Item
import cn.snowrainyskr.aff.structure.item.sceneControl.enums.EnwidenControlType

interface EnwidenControl : Item {
	var duration: Double
	var type: EnwidenControlType
}