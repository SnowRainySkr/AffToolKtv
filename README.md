# AffKt - README

AffKt is a Kotlin library designed to facilitate handling of the Arcaea File Format (aff).  With AffKt, you can read, modify, and write aff files using Kotlin.

## Quick Start

1. Clone the AffKt repository to your computer and open it with IntelliJ IDEA.
2. Create a new Kotlin entry file within the `src/kotlin` directory. Copy the example code provided below and update the file paths as indicated in the code.
3. Run the entry file. The program will insert a tap note (at time = 0, lane = 1) into the aff file, save the modified file, and print the max combo count to the console.
```kotlin
package cn.snowrainyskr

import cn.snowrainyskr.aff.structure.Aff
import cn.snowrainyskr.aff.structure.item.note.Tap
import java.io.File

fun main() {
	val aff = Aff.fromAff(File("Your aff file path"))
	val tap = Tap(0, 1)
	aff.add(tap)
	aff.output(File("Your desired output path for the modified aff file"))
	println("Total notes in aff: ${aff.quantity}")
}
```