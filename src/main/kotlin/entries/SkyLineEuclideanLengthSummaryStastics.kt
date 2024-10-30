package cn.snowrainyskr.entries

import cn.snowrainyskr.aff.structure.ApkSongs
import java.io.File
import java.io.PrintWriter

fun main() = ApkSongs.apply { songs = File("""D:\.Desktop\Arcaea\Arcaea\Arc_5.10.0\songs""") }.run {
	val affs = affs.toList().map { (chart, aff) ->
		aff.recalculateAdeCoordinate()
		Triple(chart, aff, aff.skyLineEuclideanLengthSummary)
	}.filter { (_, _, list) -> list.last() > 0 }.sortedByDescending { (_, _, list) -> list.last() }
	PrintWriter(File("""D:\.Desktop\Arcaea\Arcade\statics.csv""")).use { out ->
		out.println("Chart,difficulty," + List(190 * 2) { (it / 2).toInt().toString() }.joinToString(","))
		for ((chart, aff, lengthInfo) in affs) {
			val lengthInfo = List(190 * 2) {
				if (it in lengthInfo.indices) lengthInfo[it] else lengthInfo.last()
			}
			out.println(""""${chart.name} ${chart.difficulty}",${chart.difficulty},""" + lengthInfo.joinToString(","))
		}
	}
}