package cn.snowrainyskr.aff.structure.item.note.enums

import cn.snowrainyskr.aff.composite.ease.EaseFunction
import cn.snowrainyskr.aff.composite.ease.Easings
import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnum
import cn.snowrainyskr.aff.structure.item.enums.ItemParamEnumCompanion
import cn.snowrainyskr.aff.structure.item.note.ArcLike
import cn.snowrainyskr.aff.structure.item.note.enums.ArcEaseLike.PosRange
import cn.snowrainyskr.aff.structure.item.note.enums.ArcEaseLike.TimeRange
import cn.snowrainyskr.aff.utils.Coordinate
import kotlin.math.roundToInt

enum class ArcEasings(
	override val xEase: EaseFunction = Easings.Linear.easeIn, override val yEase: EaseFunction = Easings.Linear.easeIn
): ItemParamEnum<String>, ArcEaseLike {
	B(EaseFunction { it * it * (3 - 2 * it) }, EaseFunction { it * it * (3 - 2 * it) }),
	S, SI(Easings.Sine.easeOut), SO(Easings.Sine.easeIn),
	SISI(Easings.Sine.easeOut, Easings.Sine.easeOut), SOSO(Easings.Sine.easeIn, Easings.Sine.easeIn),
	SISO(Easings.Sine.easeOut, Easings.Sine.easeIn), SOSI(Easings.Sine.easeIn, Easings.Sine.easeOut);

	override fun toParam() = name.lowercase()

	companion object: ItemParamEnumCompanion<String> {
		private val enumMap = entries.associateBy { it.toParam() }.toMap()

		override fun fromParam(param: String) = enumMap.getOrElse(param.lowercase()) {
			throw RuntimeException("ArcEasingParamConvertException")
		}
	}
}