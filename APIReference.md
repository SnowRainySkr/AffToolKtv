# AffKt - API Reference

This document aims to provide a comprehensive overview of the AffKt library’s API.
API that appear in this document are recommended.

## Util Classes
`Coordinate`: Represents the start and end positions of `Arc` and `SkyLine` objects.

`Vector3`: Describes the `movePx` and `angle` properties of `Camera` objects.

`XRange`: Defines the boundary for EnwidenArctap objects.

## Core Interfaces

### `Item`
The base interface for all objects within a timing group.

#### `constructors`
- Create `Item` from aff sentence(no ";" at the last).
```kotlin
companion object {
	fromAffLines(line: String): Item
}
// usage
val arc = fromAffLines("arc(1562,5000,0.50,1.00,sisi,1.00,1.00,1,none,true)[arctap(5000)]")
```

#### `fields`
- `time`: Time of the item, or start time for `Hold` and `Arc`.
```kotlin
var time: Int
```

### `Note`

The base interface for `Tap`, `Hold`, `Arc`, `SkyLine`, and `EnwidenArctap`.

### `HoldLike`

The base interface for `Hold` and `Arc`, providing shared functionality like `toTime`.

#### field
```kotlin
var toTime: Int // End time of the item
```

### `SceneControl`

Base interface of `TrackDisplay`, `RedLine`, and other scene control classes.

## Key Classes and Interfaces

### `Tap`

#### constructors
- `lane` should be `Int` in most of the cases.
```kotlin
constructor(time: Int, lane: Int)
constructor(time: Int, lane: Double)
```

### `Hold`

#### constructors
- `lane` should be `Int` in most of the cases.
```kotlin
constructor(time: Int, toTime: Int, lane: Int)
constructor(time: Int, toTime: Int, lane: Double)
```

### `Arc`

Attention: Unlike Aff files, AffKt treats `Arc` and `SkyLine` as separate classes.

#### constructors
```kotlin
constructor(
	override var time: Int,
	override var toTime: Int,
	override val pos: Coordinate,
	override var toPos: Coordinate,
	override var easing: ArcEasing,
	val color: ArcColor
)
```
- Companion Object provides convenience functions for creating blue and red arcs with default parameters.
```kotlin
companion object {
    fun blue(time: Int, toTime: Int, pos: Coordinate = Coordinate.leftUp, toPos: Coordinate = Coordinate.leftUp, easing: ArcEasing = ArcEasing.S)
    fun red(time: Int, toTime: Int, pos: Coordinate = Coordinate.rightUp, toPos: Coordinate = Coordinate.rightUp, easing: ArcEasing = ArcEasing.S)
}
// usage
val blueArc = Arc.blue(0, 1000)
val redArc = Arc.red(0, 1000, Coordinate(1.0, 0.0), Coordinate.leftUp, ArcEasing.SISI)
```

### `SkyLine`

#### constructors
```kotlin
constructor(
	override var time: Int,
	override var toTime: Int,
	override var pos: Coordinate,
	override var toPos: Coordinate,
	override var easing: ArcEasing,
	val arcTaps: MutableList<Int> = mutableListOf(),
	override var hitSound: String = "none"
)
```

#### fields
- arcTaps: List of arctaps' time.
```kotlin
val arcTaps: MutableList<Int>
```

### `Timing`
Attention: `Timing` objects do not contain any items directly.
#### constructors
```kotlin
constructor(override var time: Int, var bpm: Double, var beats: Double)
```
#### functions
- `align`: Align single note with LCM of consonants(`n`) and allowableError.
- Attention: Param `note` must belong to the same timing group of `this@Timing` and must be in `this@Timing`.
```kotlin
fun align(note: Note, n: Number, allowableError: Int? = null)
```
- `align`: Align all notes from `time` to `toTime` with LCM of consonants(`n`) and allowableError.
- Attention: Param `time` and `toTime` will be restricted to `time` and `toTime` of `this@Timing`.
```kotlin
fun align(n: Number, time: Int = this.time, toTime: Int = this.toTime, allowableError: Int? = null)
```

### `TimingGroup`
Unlike `Timing`, `TimingGroup` really contains all items in it.

#### functions
- `align`: Align single note with LCM of consonants(`n`) and allowableError.
- Attention: Param `note` must be in `this@TimingGroup`.
```kotlin
fun align(note: Note, n: Number, allowableError: Int? = null, timingIncluding: Timing? = null)
```
- `align`: Align all notes in `this@TimingGroup` with LCM of consonants(`n`) and allowableError.
```kotlin
fun align(n: Number, allowableError: Int? = null)
```
- `itemsOffset`: Moves all notes within the timing group forward by a specified offset.
```kotlin
fun itemsOffset(offset: Int)
```

### AffHeader
`AffHeader` stores information in aff before line "-".

`AffHeader` is base of `AudioOffset`, `TimingPointDensityFactor`.

#### constructors
- Create a set of `AffHeader` from aff headlines.
```kotlin
companion object {
    fun fromAffHeaderLines(lines: List<String>): MutableSet<AffHeader>
}
```
- Create single `AffHeader` from its param.
```kotlin
companion object {
	fun create(name: String, value: String): AffHeader
}
```

### Aff
`Aff` contains aff headers and all timing groups in an aff file.

#### constructors
- `fromAff`: Create an `Aff` from given aff file.
```kotlin
companion object {
	fun fromAff(aff: File): Aff
}
```
- Construct aff from headers and `TimingGroup` list.
```kotlin
constructor(val headers: MutableMap<String, AffHeader>, val timingGroups: MutableList<TimingGroup>)
```

#### fields and functions
- IO
- `output`: Output `Aff` to given path.
```kotlin
fun output(file: File, option: AffSortOptions = AffSortOptions.SortByClass)
```

- Headers
```kotlin
var audioOffset: Int
var timingPointDensityFactor: Double
var version: String = "0"
var constant: Double? = null
```
```kotlin
fun addAffHeader(affHeader: AffHeader)
fun addAffHeader(name: String, value: String)
fun removeAffHeader(name: String)
```

- TimingGroups
```kotlin
val defaultTimingGroup: TimingGroups
val timingGroups: MutableList<TimingGroup>
```

- Items
- `find<T>`: Find all items whose `time` equals to param `time`.
- `findUnique<T>`: Find any item whose `time` equals to param `time`.
- `add`: Add item to the given timing group.
- `align`: Align all notes with LCM of consonants(`n`) and allowableError.
- `itemsOffset`: Moves all notes forward by a specified offset.
```kotlin
val items: List<Item>
```
```kotlin
inline fun <reified T: Item> find(time: Int): List<T>
inline fun <reified T: Item> findUnique(time: Int): T?
fun add(item: Item, timingGroup: TimingGroup = defaultTimingGroup)
fun align(n: Number, allowableError: Int? = null)
fun itemsOffset(offset: Int)
```

- Judgments
- Attention: Each Element of `judgments` contains note and its judging times.
- `quantity`: Max combo of the chart.
- `fractureRayInfo`: Contains info if playing the charts with Hikari(Fracture Ray).
```kotlin
val judgments: List<Pair<Note, List<Int>>>
val judgeTimes: List<Int>
val quantity: Int
val fractureRayInfo: FractureRayInfo
```