/*
 * Designed and developed by 2022 SungbinLand, Team Duckie
 *
 * [CustomRuleProcessorProvider.kt] created by Ji Sungbin on 22. 8. 29. 오전 2:56
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/sungbinland/quack-quack/blob/main/LICENSE
 */

package team.duckie.quackquack.lint.custom.rule.processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class CustomRuleProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) = CustomRuleProcessor(
        logger = environment.logger,
        options = environment.options,
    )
}
