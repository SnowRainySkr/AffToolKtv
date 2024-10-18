package cn.snowrainyskr.aff.structure.item.enums

fun interface ItemParamEnumCompanion<Param> {
	fun fromParam(param: Param): ItemParamEnum<Param>
}