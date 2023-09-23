package com._16minutes.liquigen.util

import com._16minutes.liquigen.settings.LiquigenExtension
import java.util.regex.Pattern

fun createTokenRegex(paramInterpolationToken: String): String {
    val paramPlaceholder = LiquigenExtension.TemplateSettings.PARAM_PLACEHOLDER
    val paramPlaceholderBeginIndex = paramInterpolationToken.indexOf(paramPlaceholder)
    val onePastPlaceholderEndIndex = paramPlaceholderBeginIndex + paramPlaceholder.length

    val substringBeforePlaceholder = Pattern.quote(paramInterpolationToken.substring(0, paramPlaceholderBeginIndex))
    val placeholderNamedCapturingGroup = "(?<${LiquigenExtension.TemplateSettings.PARAM_PLACEHOLDER}>.+)"
    val substringAfterPlaceholder = Pattern.quote(paramInterpolationToken.substring(onePastPlaceholderEndIndex))

    return "$substringBeforePlaceholder$placeholderNamedCapturingGroup$substringAfterPlaceholder"
}
