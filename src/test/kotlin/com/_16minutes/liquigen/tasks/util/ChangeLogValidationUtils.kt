package com._16minutes.liquigen.tasks.util

import com._16minutes.liquigen.Liquigen
import com._16minutes.liquigen.generators.timestamp.TimestampGenerator
import com._16minutes.liquigen.settings.LiquigenExtension
import com._16minutes.liquigen.util.createTokenRegex
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import java.io.File
import java.text.SimpleDateFormat
import java.util.regex.Pattern

fun validateChangeLogName(
    changeLogFile: File,
    liquigenExtension: LiquigenExtension,
    generateTaskName: Liquigen.TaskName
) {
    val fileSettings = liquigenExtension.fileSettings

    val timestampGenerator = fileSettings.timestampGenerator
    val baseFileName = fileSettings.baseFileName
    val fileNameDelimiter = fileSettings.fileNameDelimiter

    val changeLogFileName = changeLogFile.name
    val onePastTimestampEndIndex = changeLogFileName.indexOf("$fileNameDelimiter$baseFileName")
    val changeLogFileTimestamp = changeLogFileName.substring(0, onePastTimestampEndIndex)
    val changeLogFileBaseNameAndExtension = changeLogFileName.substring(onePastTimestampEndIndex + 1)

    val expectedFileExtension = when (generateTaskName) {
        Liquigen.TaskName.GENERATE_CHANGELOG_FROM_TEMPLATE -> {
            val templatePath = liquigenExtension.templateSettings.templatePath
            val lastIndexOfExtensionDelimiter = templatePath.lastIndexOf('.')

            if (lastIndexOfExtensionDelimiter >= 0) {
                templatePath.substring(lastIndexOfExtensionDelimiter)
            } else {
                ""
            }
        }
        else -> ""
    }

    val expectedChangeLogFileBaseNameAndExtension = "$baseFileName$expectedFileExtension"

    when(timestampGenerator) {
        is TimestampGenerator.EpochTimestamp -> {
            changeLogFileTimestamp.toLongOrNull().shouldNotBeNull()
            changeLogFileBaseNameAndExtension.shouldBe(expectedChangeLogFileBaseNameAndExtension)
        }
        is TimestampGenerator.DateTimeTimestamp -> {
            SimpleDateFormat(timestampGenerator.format).parse(changeLogFileTimestamp).shouldNotBeNull()
            changeLogFileBaseNameAndExtension.shouldBe(expectedChangeLogFileBaseNameAndExtension)
        }
        else  -> {
            changeLogFileTimestamp.shouldBeEmpty()
            changeLogFileBaseNameAndExtension.shouldBe(expectedChangeLogFileBaseNameAndExtension)
        }
    }
}

fun validateTemplateBasedChangeLogContent(changeLogFile: File, templateSettings: LiquigenExtension.TemplateSettings) {
    val paramPlaceholder = LiquigenExtension.TemplateSettings.PARAM_PLACEHOLDER
    val tokenValuesByParam = templateSettings.templateParamNamesAndValues

    val tokenRegex = createTokenRegex(templateSettings.templateParamInterpolationToken)
    val templateFileContent = File(templateSettings.templatePath).readText()

    val expectedChangeLogFileContent =
        Pattern
            .compile(tokenRegex)
            .matcher(templateFileContent)
            .replaceAll { tokenValuesByParam[it.group(1)].toString() }
    changeLogFile.readText().shouldBe(expectedChangeLogFileContent)
}
