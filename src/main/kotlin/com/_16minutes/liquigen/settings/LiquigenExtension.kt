package com._16minutes.liquigen.settings

import com._16minutes.liquigen.generators.timestamp.TimestampGenerator
import java.io.File

open class LiquigenExtension(
    val fileSettings: FileSettings,
    val templateSettings: TemplateSettings
) {
    class FileSettings() {
        object Defaults {
            const val OUTPUT_DIRECTORY_PATH = "./"
            const val BASE_FILE_NAME = "changelog"
            const val FILE_NAME_DELIMITER = "_"
            val TIMESTAMP_GENERATOR = TimestampGenerator.EpochTimestamp()
        }

        lateinit var outputDirectoryPath: String
        lateinit var baseFileName: String
        lateinit var fileNameDelimiter: String
        var timestampGenerator: TimestampGenerator? = null

        constructor(
            outputDir: String,
            baseFileName: String,
            fileNameDelimiter: String,
            timestampGenerator: TimestampGenerator? = null
        ) : this() {
            this.outputDirectoryPath = outputDir
            this.baseFileName = baseFileName
            this.fileNameDelimiter = fileNameDelimiter
            this.timestampGenerator = timestampGenerator
        }

        fun mergeInDefaults() {
            if (!::outputDirectoryPath.isInitialized) {
                outputDirectoryPath = Defaults.OUTPUT_DIRECTORY_PATH
            }

            if (!::baseFileName.isInitialized) {
                baseFileName = Defaults.BASE_FILE_NAME
            }

            if (!::fileNameDelimiter.isInitialized) {
                fileNameDelimiter = Defaults.FILE_NAME_DELIMITER
            }

            if (timestampGenerator == null) {
                timestampGenerator = Defaults.TIMESTAMP_GENERATOR
            }
        }

        override fun toString(): String {
            val strBuilder = StringBuilder()

            if (::outputDirectoryPath.isInitialized) {
                strBuilder.appendLine("""outputDirectoryPath = "$outputDirectoryPath"""")
            }

            if (::baseFileName.isInitialized) {
                strBuilder.appendLine("""baseFileName = "$baseFileName"""")
            }

            if (::fileNameDelimiter.isInitialized) {
                strBuilder.appendLine("""fileNameDelimiter = "$fileNameDelimiter"""")
            }

            val localTimestampGenerator = timestampGenerator
            val timestampGeneratorConstructorCallStr = when (localTimestampGenerator) {
                is TimestampGenerator.EpochTimestamp -> "()"
                is TimestampGenerator.DateTimeTimestamp -> """("${localTimestampGenerator.format}")"""
                else -> ""
            }

            if (localTimestampGenerator != null) {
                val timestampGeneratorQualifiedName = localTimestampGenerator::class.qualifiedName
                strBuilder.appendLine("timestampGenerator = ${timestampGeneratorQualifiedName}${timestampGeneratorConstructorCallStr}")
            }

            return strBuilder.toString()
        }
    }

    class TemplateSettings() {

        companion object {
            const val PARAM_PLACEHOLDER = "param"
        }
        object Defaults {
            const val PARAM_INTERPOLATION_TOKEN = "\${$PARAM_PLACEHOLDER}"
            val TEMPLATE_PARAM_NAMES_AND_VALUES = mapOf(
                "id" to "",
                "author" to "",
                "dbms" to ""
            )
            val TEMPLATE_PATH: String =
                File(this.javaClass.getResource("/templates/yaml/sql_changeset_template.yaml").toURI()).absolutePath
        }

        lateinit var templateParamInterpolationToken: String
        lateinit var templateParamNamesAndValues: Map<String, Any>
        lateinit var templatePath: String

        constructor(
            templateParamInterpolationToken: String,
            templateParamNamesAndValues: Map<String, Any>,
            templatePath: String
        ): this() {
            this.templateParamInterpolationToken = templateParamInterpolationToken
            this.templateParamNamesAndValues = templateParamNamesAndValues
            this.templatePath = templatePath
        }
        fun mergeInDefaults() {
            if (!::templateParamInterpolationToken.isInitialized) {
                templateParamInterpolationToken = Defaults.PARAM_INTERPOLATION_TOKEN
            }

            if (!::templateParamNamesAndValues.isInitialized) {
                templateParamNamesAndValues = Defaults.TEMPLATE_PARAM_NAMES_AND_VALUES
            }

            if (!::templatePath.isInitialized) {
                templatePath = Defaults.TEMPLATE_PATH
            }
        }

        override fun toString(): String {
            val strBuilder = StringBuilder()

            if (::templateParamInterpolationToken.isInitialized) {
                strBuilder.appendLine("""paramInterpolationToken = "$templateParamInterpolationToken"""")
            }

            if (::templateParamNamesAndValues.isInitialized && templateParamNamesAndValues.isNotEmpty()) {
                val templateParamsPropertiesStr =
                    templateParamNamesAndValues
                        .asSequence()
                        .map{""""${it.key}" to "${it.value}""""}
                        .joinToString(", ")

                strBuilder.appendLine("templateParamNamesAndValues = mapOf($templateParamsPropertiesStr)")
            }

            if (::templatePath.isInitialized) {
                strBuilder.appendLine("""templatePath = "$templatePath"""")
            }

            return strBuilder.toString()
        }
    }

    constructor() : this(FileSettings(), TemplateSettings())

    fun fileSettings(configure: FileSettings.() -> Unit) {
        fileSettings.configure()
    }

    fun templateSettings(configure: TemplateSettings.() -> Unit) {
        templateSettings.configure()
    }

    fun mergeDefaultsInToSettings() {
        fileSettings.mergeInDefaults()
        templateSettings.mergeInDefaults()
    }

    override fun toString(): String {
        val settingsStrBuilder = StringBuilder()

        val fileSettingsStr = fileSettings.toString()
        if (fileSettingsStr.isNotEmpty()) {
            settingsStrBuilder.appendLine("""
                fileSettings {
                    $fileSettingsStr
                }
            """.trimMargin())
        }

        val templateSettingsStr = templateSettings.toString()
        if (templateSettingsStr.isNotEmpty()) {
            settingsStrBuilder.appendLine("""
                templateSettings {
                    $templateSettingsStr
                }
            """.trimMargin())
        }

        return """
            liquigen {
                $settingsStrBuilder
            }
        """.trimIndent()
    }
}
