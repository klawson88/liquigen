package com._16minutes.liquigen.generators

import com._16minutes.liquigen.settings.LiquigenExtension
import com._16minutes.liquigen.util.createTokenRegex
import com._16minutes.liquigen.util.procureFullFileName
import java.io.File
import java.nio.file.Path
import java.util.regex.Pattern

class TemplateBasedGenerator: Generator {
    companion object {
        fun procureFileNameWithExtension(changeLogGeneratorExtension: LiquigenExtension): String {
            val fullFileName = procureFullFileName(changeLogGeneratorExtension.fileSettings)

            val templatePath = changeLogGeneratorExtension.templateSettings.templatePath
            val lastIndexOfExtensionDelimiter = templatePath.lastIndexOf('.')

            val extension =  if (lastIndexOfExtensionDelimiter >= 0) {
                templatePath.substring(lastIndexOfExtensionDelimiter)
            } else {
                ""
            }

            return "$fullFileName$extension"
        }
    }


    override fun generate(liquigenExtension: LiquigenExtension): File {
        val extensionTemplateSettings = liquigenExtension.templateSettings
        val tokenPattern = Pattern.compile(createTokenRegex(extensionTemplateSettings.templateParamInterpolationToken))
        val tokenValuesByParam = extensionTemplateSettings.templateParamNamesAndValues

        val templateFileContent = File(extensionTemplateSettings.templatePath).readText()
        val outputFileContent =
            tokenPattern
                .matcher(templateFileContent)
                .replaceAll { tokenValuesByParam[it.group(1)].toString() }

        val outputFile =
            Path
                .of(liquigenExtension.fileSettings.outputDirectoryPath, procureFileNameWithExtension(liquigenExtension))
                .toFile()

        val outputWriter = outputFile.bufferedWriter()
        outputWriter.write(outputFileContent)
        outputWriter.flush()

        return outputFile
    }
}
