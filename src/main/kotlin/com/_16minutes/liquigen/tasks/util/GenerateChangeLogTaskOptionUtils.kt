package com._16minutes.liquigen.tasks.util

import com._16minutes.liquigen.generators.timestamp.TimestampGenerator

fun createTimestampGeneratorForFormatOption(timestampFormat: String): TimestampGenerator {
    return when (timestampFormat) {
        "epoch" -> TimestampGenerator.EpochTimestamp()
        else -> {
            TimestampGenerator.DateTimeTimestamp(timestampFormat)
        }
    }
}

fun parseTemplateParamsOption(templateParamsPropertiesStr: String): HashMap<String, String> {
    val tokenValuesByParam = HashMap<String, String>()

    templateParamsPropertiesStr.split(";").forEach {
        val keyAndValue = it.split("=", ":")
        tokenValuesByParam[keyAndValue[0].trim()] = keyAndValue[1].trim()
    }

    return tokenValuesByParam
}
