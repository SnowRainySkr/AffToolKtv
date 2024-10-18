package cn.snowrainyskr.aff.structure.timingGroup

data class AngleX(val rotate10Times: Int) : TimingGroupSpecialEffect {
	override fun toParam() = "anglex$rotate10Times"
}