/*
 * Designed and developed by Duckie Team 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/quack-quack-android/blob/main/LICENSE
 */

package team.duckie.quackquack.util

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class CollectionTest : FreeSpec() {
  init {
    "fastFirstIsInstanceOrNul" - {
      "캐스팅 가능한 요소가 있다면 null이 반환되지 않음" {
        val elements = listOf(1, 'Q', Float, "4", Any())
        val stringElement = elements.fastFirstIsInstanceOrNull<String>()

        stringElement.shouldNotBeNull()
      }

      "null를 반환하면 주어진 리스트에 존재하지 않음" {
        val elements = listOf(1, 'Q', Float, "4", Any())
        val stringElement = elements.fastFirstIsInstanceOrNull<Double>()

        stringElement.shouldBeNull()
      }

      "여러개의 요소가 제공됐다면 첫 번째에 해당하는 요소가 반환됨" {
        val elements = listOf(1, 'Q', Float, "4", Any(), "6")
        val stringElement = elements.fastFirstIsInstanceOrNull<String>()

        stringElement shouldBe "4"
      }
    }
    "fastFilterIsInstanceOrNull" - {
      "캐스팅 가능한 요소가 있다면 null이 반환되지 않음" {
        val elements = listOf(1, 'Q', Float, "4", Any(), "10")
        val stringElement = elements.fastFilterIsInstanceOrNull<String>()

        stringElement.shouldNotBeNull()
      }

      "null를 반환하면 주어진 리스트에 존재하지 않음" {
        val elements = listOf(1, 'Q', Float, "4", Any())
        val stringElement = elements.fastFilterIsInstanceOrNull<Double>()

        stringElement.shouldBeNull()
      }

      "여러개의 요소가 제공됐다면 해당되는 모든 요소가 반환됨" {
        val elements = listOf(1, 'Q', Float, "4", Any(), "6")
        val stringElement = elements.fastFilterIsInstanceOrNull<String>()

        stringElement.shouldContainExactly("4", "6")
      }
    }
  }
}
