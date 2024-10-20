package cn.snowrainyskr.aff.structure.item.note

sealed interface HoldLike: Note {
	var toTime: Int

	override fun moveTo(time: Int) {
		toTime -= this.time - time
		super.moveTo(time)
	}

	override fun moveForward(dTime: Int) {
		toTime += dTime
		super.moveForward(dTime)
	}

	override fun toTime() = toTime
}