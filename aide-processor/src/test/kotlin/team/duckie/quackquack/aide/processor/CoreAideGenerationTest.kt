/*
 * Designed and developed by Duckie Team 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/quack-quack-android/blob/main/LICENSE
 */

@file:Suppress(
  "RedundantUnitReturnType",
  "RedundantVisibilityModifier",
  "RedundantUnitExpression",
  "RedundantSuppression",
  "LongMethod",
  "HasPlatformType",
  "UnusedReceiverParameter",
)

package team.duckie.quackquack.aide.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import team.duckie.quackquack.util.backend.test.findGeneratedFileOrNull

class CoreAideGenerationTest : StringSpec() {
  private val useKspIncrementals = listOf(true, false)
  private val tempDir = tempdir()

  init {
    useKspIncrementals.forEach { kspIncremental ->
      "@DecorateModifier가 달린 Modifier은 AideModifiers.kt 파일에 모여짐 - [kspIncremental: $kspIncremental]" {
        val result = compile(
          useKspIncremental = kspIncremental,
          kotlin(
            "text.kt",
            """
import team.duckie.quackquack.aide.annotation.DecorateModifier
import androidx.compose.ui.Modifier

@DecorateModifier
public fun Modifier.span(): Modifier = Modifier

@DecorateModifier
public fun Modifier.spans() = this

@DecorateModifier
public fun Modifier.spans(duplicate: Any): Modifier {
    return Modifier
}

@DecorateModifier
public fun emptyModifier() {}

@DecorateModifier
public fun Modifier.noReturnModifier(): Unit {
    return Unit
}

@DecorateModifier
private fun Modifier.privateModifier(): Modifier {
    return Modifier
}
            """,
          ),
          kotlin(
            "button.kt",
            """
import team.duckie.quackquack.aide.annotation.DecorateModifier
import androidx.compose.ui.Modifier

@DecorateModifier
public fun Modifier.click(): Modifier {
    return Modifier
}

@DecorateModifier
public fun Modifier.longClick(): Modifier = Modifier

@DecorateModifier
public fun Modifier.doubleClick(): Modifier = this

// return type 생략시 type mismatch: Modifier & Modifier.Companion
@DecorateModifier
public fun Modifier.typeMismatchModifier() = Modifier

@DecorateModifier
public fun Modifier.noReturnModifier2(): Unit {}

@DecorateModifier
internal fun Modifier.internalModifier(): Modifier {
    return Modifier
}
            """,
          ),
        )

        @Language("kotlin")
        val expect = """
// This file was automatically generated by aide-processor.
// Do not modify it arbitrarily.
// @formatter:off
@file:Suppress("NoConsecutiveBlankLines", "PackageDirectoryMismatch", "Wrapping",
    "TrailingCommaOnCallSite", "ArgumentListWrapping", "ktlint")

import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

internal val aideModifiers: Map<String, List<String>> = run {
  val aide = mutableMapOf<String, List<String>>()

  aide["button"] = listOf("click", "longClick", "doubleClick")
  aide["_click"] = emptyList()
  aide["_longClick"] = emptyList()
  aide["_doubleClick"] = emptyList()

  aide["text"] = listOf("span", "spans")
  aide["_span"] = emptyList()
  aide["_spans"] = emptyList()

  aide
}


                     """.trimIndent()

        result.exitCode shouldBe KotlinCompilation.ExitCode.OK
        tempDir.findGeneratedFileOrNull("AideModifiers.kt")?.readText() shouldBe expect
      }

      "꽥꽥 컴포넌트는 QuackComponents.kt 파일에 모여짐 - [kspIncremental: $kspIncremental]" {
        val result = compile(
          useKspIncremental = kspIncremental,
          kotlin(
            "text.kt",
            """
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
public fun QuackText() {}

@Composable
public fun Text() {}

@Composable
public fun Modifier.QuackText2() {}

@Composable
public fun QuackText3(): Int = 1

@Composable
public fun QuackText4() = 1

@Composable
private fun QuackText5() {}
            """,
          ),
          kotlin(
            "button.kt",
            """
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
public fun QuackButton() {}

@Composable
public fun Button() {}

@Composable
public fun QuackButton2() {}

@Composable
public fun QuackButton2(duplicate: Any) {}

@Composable
public fun QuackButton3(): Int = 1

@Composable
public fun Modifier.QuackButton4() {}

@Composable
internal fun QuackButton5() {}
            """,
          ),
        )

        @Language("kotlin")
        val expect = """
// This file was automatically generated by aide-processor.
// Do not modify it arbitrarily.
// @formatter:off
@file:Suppress("NoConsecutiveBlankLines", "PackageDirectoryMismatch", "Wrapping",
    "TrailingCommaOnCallSite", "ArgumentListWrapping", "ktlint")

import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map

internal val quackComponents: Map<String, String> = run {
  val aide = mutableMapOf<String, String>()

  aide["QuackButton"] = "button"
  aide["QuackButton2"] = "button"
  aide["QuackButton4"] = "button"

  aide["QuackText"] = "text"
  aide["QuackText2"] = "text"

  aide
}


                     """.trimIndent()

        result.exitCode shouldBe KotlinCompilation.ExitCode.OK
        tempDir.findGeneratedFileOrNull("QuackComponents.kt")?.readText() shouldBe expect
      }
    }
  }

  private fun compile(useKspIncremental: Boolean, vararg sourceFiles: SourceFile): KotlinCompilation.Result {
    return prepareCompilation(useKspIncremental, *sourceFiles).compile()
  }

  private fun prepareCompilation(useKspIncremental: Boolean, vararg sourceFiles: SourceFile): KotlinCompilation {
    return KotlinCompilation().apply {
      workingDir = tempDir
      sources = sourceFiles.asList() + stubs
      kspIncremental = useKspIncremental
      allWarningsAsErrors = true
      symbolProcessorProviders = listOf(AideSymbolProcessorProvider())
    }
  }
}
