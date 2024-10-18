package cn.snowrainyskr.aff.structure.timingGroup

data class AngleY(val rotate10Times: Int) : TimingGroupSpecialEffect {
	override fun toParam() = "angley$rotate10Times"
}