package com._16minutes.liquigen.util.spec

import com._16minutes.liquigen.settings.LiquigenExtension
import com._16minutes.liquigen.util.createTokenRegex
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.regex.Pattern

class TokenUtilsSpec: DescribeSpec({

    describe("createTokenRegex") {
        it("""returns a string that replaces instances of LiquigenExtension.TemplateSettings.PARAM_PLACEHOLDER
            | in the argument token with a namesake capturing group, preserving the semantic of 
            | regular expression special characters in the token""".trimMargin() ) {
            val token = "\${${LiquigenExtension.TemplateSettings.PARAM_PLACEHOLDER}}"
            val expectedResult =
                Pattern.quote("\${") +
                "(?<${LiquigenExtension.TemplateSettings.PARAM_PLACEHOLDER}>.+)" +
                Pattern.quote("}")

            val actualResult = createTokenRegex(token)

            actualResult.shouldBe(expectedResult)
        }
    }
})
