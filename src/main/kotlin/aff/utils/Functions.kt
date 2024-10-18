package cn.snowrainyskr.aff.utils

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