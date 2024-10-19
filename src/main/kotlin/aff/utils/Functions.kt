package cn.snowrainyskr.aff.utils

import cn.snowrainyskr.aff.structure.item.Timing

fun Double.format() = String.format("%.2f", this)

val removeBlankCharPattern = Regex("""\s""")
fun String.format() = replace(removeBlankCharPattern, "")

inline fun <R, T> List<T>.assignToRanges(
	ranges: List<R>,
	rangeTransform: (R) -> IntRange,
	valueTransform: (T) -> Int
) = buildMap<R, MutableList<T>> {
	val values = this@assignToRanges
	var (rangeIndex, valueIndex) = Pair(0, 0)
	while (valueIndex < values.size && rangeIndex < ranges.size) {
		val range = rangeTransform(ranges[rangeIndex])
		val value = valueTransform(values[valueIndex])
		when {
			value < range.first -> valueIndex++
			value > range.last -> rangeIndex++
			else -> {
				getOrPut(ranges[rangeIndex]) { mutableListOf() }.add(values[valueIndex])
				valueIndex++
			}
		}
	}
} as Map<R, List<T>>

private val noteCannotFindTimingException = RuntimeException("NoteCannotFindTimingException")

fun List<Timing>.find(time: Int): Timing =
	if (size < 10) find { time in it.range }
	else {
		val index = binarySearch { it.time - time }
		if (index >= 0) this[index]
		else this[-index - 2]
	} ?: throw noteCannotFindTimingException

fun <T> List<T>.lazySubList(i: Int, j: Int): Sequence<T> = asSequence().drop(i).take(j - i + 1)