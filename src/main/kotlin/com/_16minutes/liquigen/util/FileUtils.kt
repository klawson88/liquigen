package com._16minutes.liquigen.util

import com._16minutes.liquigen.settings.LiquigenExtension

fun procureFullFileName(extensionFileSettings: LiquigenExtension.FileSettings): String {
    val settingsTimestampGenerator = extensionFileSettings.timestampGenerator

    return if (settingsTimestampGenerator != null) {
        val timestamp = settingsTimestampGenerator.generate()
        "${timestamp}${extensionFileSettings.fileNameDelimiter}${extensionFileSettings.baseFileName}"
    } else {
        extensionFileSettings.baseFileName
    }
}
