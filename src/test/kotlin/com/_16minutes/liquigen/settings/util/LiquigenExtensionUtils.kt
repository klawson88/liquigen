package com._16minutes.liquigen.settings.util

import com._16minutes.liquigen.settings.LiquigenExtension
import com._16minutes.liquigen.tasks.util.createTimestampGeneratorForFormatOption
import com._16minutes.liquigen.tasks.util.parseTemplateParamsOption

fun mergeDefaultsAndTaskOptionsIntoSettings(
    liquigenExtension: LiquigenExtension,
    settingsTaskOptionsByName: Map<String, String>
) {
    liquigenExtension.mergeDefaultsInToSettings()

    liquigenExtension.fileSettings {
        settingsTaskOptionsByName["outputDirectoryPath"]?.let {
            outputDirectoryPath = it
        }

        settingsTaskOptionsByName["baseFileName"]?.let {
            baseFileName = it
        }

        settingsTaskOptionsByName["fileNameDelimiter"]?.let {
            fileNameDelimiter = it
        }

        settingsTaskOptionsByName["timestampFormat"]?.let {
            timestampGenerator = createTimestampGeneratorForFormatOption(it)
        }
    }

    liquigenExtension.templateSettings {
        settingsTaskOptionsByName["templateParams"]?.let {
            templateParamNamesAndValues = parseTemplateParamsOption(it)
        }
    }
}
