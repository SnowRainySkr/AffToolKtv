package cn.snowrainyskr.aff.structure.item

import cn.snowrainyskr.aff.structure.item.enums.ItemClass
import cn.snowrainyskr.aff.structure.item.note.Arc
import cn.snowrainyskr.aff.structure.item.note.SkyLine
import cn.snowrainyskr.aff.structure.item.sceneControl.SceneControlCompanion
import cn.snowrainyskr.aff.utils.format

abstract class ItemCompanion(val itemClass: ItemClass) {
	init {
		itemCompanionMap.put(itemClass, this)
	}

	abstract fun fromParams(params: List<String>): Item

	companion object {
		val itemCompanionMap = mutableMapOf<ItemClass, ItemCompanion>()

		private val e = RuntimeException("ItemConvertException")

		private val skyLinePattern = Regex("""arc\((.*?)\)\[(.*?)]""")
		private val arcTapPattern = Regex("""\((.*?)\)""")

		fun fromAffLine(line: String) = line.format().run {
			Item
			if (last() == ']') {
				val params = skyLinePattern.find(this)?.let { matchResult ->
					matchResult.groupValues.let {
						it[1].split(",") + arcTapPattern.findAll(it[2]).map { it.groupValues[1] }.toList()
					}
				} ?: throw e
				SkyLine.fromParams(params)
			} else {
				val (name, afterName) = split("(", limit = 2)
				val params = afterName.dropLast(1).split(",")
				val itemClass = when (name) {
					Arc.itemClass.itemName -> when {
						params[9].toBoolean() -> ItemClass.SKYLINE
						params[7] == "3" -> ItemClass.ENWIDEN_ARCTAP
						else -> ItemClass.ARC
					}

					else -> ItemClass.fromParam(name)
				}
				if (itemClass == ItemClass.SCENE_CONTROL)
					SceneControlCompanion.fromParams(params)
				else
					itemCompanionMap[itemClass]?.fromParams(params) ?: throw e
			}
		}
	}
}