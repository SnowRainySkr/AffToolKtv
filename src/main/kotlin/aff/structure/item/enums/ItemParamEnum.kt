package cn.snowrainyskr.aff.structure.item.enums

fun interface ItemParamEnum<Param> {
	fun toParam(): Param
}