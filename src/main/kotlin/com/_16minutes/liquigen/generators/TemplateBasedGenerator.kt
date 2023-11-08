package com._16minutes.liquigen.generators

import com._16minutes.liquigen.settings.LiquigenExtension
import com._16minutes.liquigen.util.createTokenRegex
import com._16minutes.liquigen.util.procureFullFileName
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
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


    override fun generate(liquigenExtension: LiquigenExtension, relativePathParentDirectory: File?): File {
        val extensionTemplateSettings = liquigenExtension.templateSettings
        val tokenPattern = Pattern.compile(createTokenRegex(extensionTemplateSettings.templateParamInterpolationToken))
        val tokenValuesByParam = extensionTemplateSettings.templateParamNamesAndValues

        val relativePathParentDirectoryPath = relativePathParentDirectory?.absolutePath ?: ""

        val effectiveTemplatePath =
            Paths
                .get(relativePathParentDirectoryPath)
                .resolve(extensionTemplateSettings.templatePath)
                .normalize()
                .toString()

        val templateFileContent = File(effectiveTemplatePath).readText()
        val outputFileContent =
            tokenPattern
                .matcher(templateFileContent)
                .replaceAll { tokenValuesByParam[it.group(1)].toString() }

        val effectiveOutputDirectoryPath =
            Paths
                .get(relativePathParentDirectoryPath)
                .resolve(liquigenExtension.fileSettings.outputDirectoryPath)
                .normalize()
                .toString()

        val outputFile =
            Path
                .of(effectiveOutputDirectoryPath, procureFileNameWithExtension(liquigenExtension))
                .toFile()

        val outputWriter = outputFile.bufferedWriter()
        outputWriter.write(outputFileContent)
        outputWriter.flush()

        return outputFile
    }
}
