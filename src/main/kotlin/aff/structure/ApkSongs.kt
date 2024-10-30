package cn.snowrainyskr.aff.structure

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object ApkSongs {
	lateinit var songs: File

	val songList: File
		get() = File(songs.path + """\songlist""")

	enum class Difficulty {
		PST, PRS, FTR, BYD, ETR;

		override fun toString() = name.lowercase()
		companion object {
			fun fromRatingClass(ratingClass: Int) = entries[ratingClass]
		}
	}

	data class Chart(val name: String, val difficulty: Difficulty)

	val affs: Map<Chart, Aff>
		get() {
			@Serializable
			data class TitleLocalized(val en: String)

			@Serializable
			data class DifficultyInfo(val ratingClass: Int, val chartDesigner: String, val bpm_base: Double? = null)

			@Serializable
			data class Song(
				val id: String,
				val title_localized: TitleLocalized,
				val bpm_base: Double,
				val difficulties: List<DifficultyInfo>,
				val set: String
			)

			@Serializable
			data class SongList(val songs: List<Song>)

			return Json { ignoreUnknownKeys = true }.decodeFromString<SongList>(songList.readText()).songs
				.filter { it.set != "april" && File("""$songs\${it.id}""").exists() }
				.map { song ->
				val id = song.id
				val name = song.title_localized.en.let { name ->
					when (name.hashCode()) {
						1278151819 -> "II"
						else -> name
					}
				}
				val songBaseBpm = song.bpm_base
				song.difficulties.filter { it.chartDesigner.isNotBlank() }.map { (ratingClass, _, baseBpm) ->
					val baseBpm = baseBpm ?: songBaseBpm
					val aff = Aff.fromAff(File("""$songs\$id\$ratingClass.aff"""))
					aff.baseBpm = Aff.BaseBpm(baseBpm)
					Pair(Chart(name, Difficulty.fromRatingClass(ratingClass)), aff)
				}
			}.flatten().toMap()
		}
}