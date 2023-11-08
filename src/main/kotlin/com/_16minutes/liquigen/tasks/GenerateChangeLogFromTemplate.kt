package com._16minutes.liquigen.tasks

import com._16minutes.liquigen.generators.TemplateBasedGenerator
import com._16minutes.liquigen.settings.LiquigenExtension
import com._16minutes.liquigen.tasks.util.createTimestampGeneratorForFormatOption
import com._16minutes.liquigen.tasks.util.parseTemplateParamsOption
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import javax.inject.Inject


open class GenerateChangeLogFromTemplate @Inject constructor (
    private val liquigenExtension: LiquigenExtension,
    private val generator: TemplateBasedGenerator
): GenerateChangeLogTask() {
    @Internal
    @Option(description = "The token used to interpolate parameter values in the template")
    var templateParamInterpolationToken: String? = null

    @Internal
    @Option(description = """A ';' delimited Java Properties string of parameter names and
        | values to be interpolated (ex. param1=value1;param2=value2""")
    var templateParams: String? = null

    @Internal
    @Option(description = "The path to the template")
    var templatePath: String? = null

    private fun mergeFileSettingsOptionsIntoExtension() {
        val extensionFileSettings = liquigenExtension.fileSettings

        val localOutputDirectory = outputDirectoryPath
        if (localOutputDirectory != null) {
            extensionFileSettings.outputDirectoryPath = localOutputDirectory
        }

        val localBaseFileName = baseFileName
        if (localBaseFileName != null) {
            extensionFileSettings.baseFileName = localBaseFileName
        }

        val localFileNameDelimiter = fileNameDelimiter
        if (localFileNameDelimiter != null) {
            extensionFileSettings.fileNameDelimiter = localFileNameDelimiter
        }

        val localTimestampFormat = timestampFormat
        if (localTimestampFormat != null) {
            extensionFileSettings.timestampGenerator = createTimestampGeneratorForFormatOption(localTimestampFormat)
        }
    }

    private fun mergeTemplateSettingsOptionsIntoExtension() {
        val extensionTemplateSettings = liquigenExtension.templateSettings

        val localTemplateParamInterpolationToken = templateParamInterpolationToken
        if (localTemplateParamInterpolationToken != null) {
            extensionTemplateSettings.templateParamInterpolationToken = localTemplateParamInterpolationToken
        }

        val localTemplateParams = templateParams
        if (localTemplateParams != null) {
            extensionTemplateSettings.templateParamNamesAndValues = parseTemplateParamsOption(localTemplateParams)
        }

        val localTemplatePath = templatePath
        if (localTemplatePath != null) {
            extensionTemplateSettings.templatePath = localTemplatePath
        }
    }

    @TaskAction
    fun execute() {
        mergeFileSettingsOptionsIntoExtension()
        mergeTemplateSettingsOptionsIntoExtension()
        generator.generate(liquigenExtension, project.projectDir)
    }
}
