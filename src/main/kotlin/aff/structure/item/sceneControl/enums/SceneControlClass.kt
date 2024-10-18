package cn.snowrainyskr.aff.structure.item.sceneControl.enums

enum class SceneControlClass {
	ArcahvDebris, ArcahvDistort, EnwidenCamera, EnwidenLanes, HideGroup, RedLine, TrackDisplay, TrackHide, TrackShow;

	override fun toString() = name.lowercase()

	companion object {
		val sceneControlClassMap = entries.associateBy { it.toString() }.toMap()

		fun fromParam(param: String) = sceneControlClassMap.getOrElse(param) {
			throw RuntimeException("SceneControlClassParamConvertException")
		}
	}
}