package com._16minutes.liquigen.generators

import com._16minutes.liquigen.generators.timestamp.TimestampGenerator
import com._16minutes.liquigen.settings.LiquigenExtension
import com._16minutes.liquigen.util.SYSTEM_TEMP_DIRECTORY_PATHNAME
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createTempFile

class TemplateBasedGeneratorSpec: DescribeSpec({
    describe("companion object") {
        describe("procureFileNameWithExtension") {
            fun createLiquigenExtensionMockForTest(baseFileName: String, templatePath: String): LiquigenExtension {
                var fileSettings = mockk<LiquigenExtension.FileSettings>()
                every {fileSettings.baseFileName} returns baseFileName
                every {fileSettings.timestampGenerator} returns null
                var templateSettings = mockk<LiquigenExtension.TemplateSettings>()
                every {templateSettings.templatePath} returns templatePath
                var liquigenExtension = mockk<LiquigenExtension>()
                every {liquigenExtension.fileSettings} returns fileSettings
                every {liquigenExtension.templateSettings} returns templateSettings

                return liquigenExtension
            }

            fun performVerificationsForTest(liquigenExtensionMock: LiquigenExtension) {
                val fileSettingsMock = liquigenExtensionMock.fileSettings
                verify {fileSettingsMock.baseFileName}
                verify {fileSettingsMock.timestampGenerator}

                val templateSettingsMock = liquigenExtensionMock.templateSettings
                verify {templateSettingsMock.templatePath}

                verify {liquigenExtensionMock.fileSettings}
                verify {liquigenExtensionMock.templateSettings}

                confirmVerified(
                    fileSettingsMock,
                    templateSettingsMock,
                    liquigenExtensionMock
                )
            }

            it("""returns the file name specified in the argument extension
                | if the template specified in the argument has no extension""".trimMargin()) {
                val baseFileName = "foo"
                val templatePath = Path.of("tmp", "foo_template").toString()
                val liquigenExtension = createLiquigenExtensionMockForTest(baseFileName, templatePath)

                val actualResult = TemplateBasedGenerator.procureFileNameWithExtension(liquigenExtension)

                performVerificationsForTest(liquigenExtension)
                actualResult.shouldBe(baseFileName)
            }

            it("""returns the file name specified in the argument extension followed by the
                | file extension of the template specified in it, if the latter is present""".trimMargin()) {
                val baseFileName = "foo"
                val templatePath = Path.of("tmp", "foo_template.yaml").toString()
                val liquigenExtension = createLiquigenExtensionMockForTest(baseFileName, templatePath)
                val expectedResult = "$baseFileName.yaml"

                val actualResult = TemplateBasedGenerator.procureFileNameWithExtension(liquigenExtension)

                performVerificationsForTest(liquigenExtension)
                actualResult.shouldBe(expectedResult)
            }
        }
    }

    describe("generate") {
        fun createFileSettingsMockForTest(
            outputDirectoryPath: String,
            baseFileName: String,
            fileNameDelimiter: String,
            timestamp: String
        ): LiquigenExtension.FileSettings {
            var fileSettings = mockk<LiquigenExtension.FileSettings>()
            val timestampGenerator = mockk<TimestampGenerator>()
            
            every{timestampGenerator.generate()} returns timestamp
            every {fileSettings.outputDirectoryPath} returns outputDirectoryPath
            every {fileSettings.baseFileName} returns baseFileName
            every {fileSettings.fileNameDelimiter} returns fileNameDelimiter
            every {fileSettings.timestampGenerator} returns timestampGenerator
            
            return fileSettings
        }

        fun performFileSettingsMockVerifications(fileSettings: LiquigenExtension.FileSettings) {
            verify {fileSettings.outputDirectoryPath}
            verify {fileSettings.fileNameDelimiter}
            verify {fileSettings.baseFileName}
            verify {fileSettings.timestampGenerator}
            confirmVerified(fileSettings)
            
            val timestampGenerator = fileSettings.timestampGenerator!!
            verify {timestampGenerator.generate()}
            confirmVerified(timestampGenerator)
        }
        
        fun createTemplateSettingsMockForTest(
            paramInterpolationToken: String,
            templatePath: String,
            tokenValuesByParam: Map<String, String>
        ): LiquigenExtension.TemplateSettings {
            var templateSettings = mockk<LiquigenExtension.TemplateSettings>()
            every {templateSettings.templateParamInterpolationToken} returns paramInterpolationToken
            every {templateSettings.templatePath} returns templatePath
            every {templateSettings.templateParamNamesAndValues} returns tokenValuesByParam
            
            return templateSettings
        }

        fun performTemplateSettingsMockVerification(templateSettings: LiquigenExtension.TemplateSettings) {
            verify {templateSettings.templateParamInterpolationToken}
            verify {templateSettings.templatePath}
            verify {templateSettings.templateParamNamesAndValues}
            confirmVerified(templateSettings)
        }
        
        fun createLiquigenExtensionMockForTest(
            fileSettings: LiquigenExtension.FileSettings,
            templateSettings: LiquigenExtension.TemplateSettings
        ): LiquigenExtension {
            var liquigenExtension = mockk<LiquigenExtension>()
            every {liquigenExtension.fileSettings} returns fileSettings
            every {liquigenExtension.templateSettings} returns templateSettings
            
            return liquigenExtension
        }
        
        fun performLiquigenExtensionMockVerification(liquigenExtension: LiquigenExtension) {
            verify {liquigenExtension.fileSettings}
            verify {liquigenExtension.templateSettings}
            confirmVerified(liquigenExtension)
        }
        
        fun createTempTemplateFile(fileExtension: String, contents: String): File {
            val file = createTempFile("", fileExtension).toFile()
            file.writeText(contents)

            return file
        }

        it("""returns a file with a path comprised of the output directory, base component, type of 
            | generated timestamp, and extension specified by the argument extension""".trimMargin()) {
            val outputDirectoryPath = Path.of(SYSTEM_TEMP_DIRECTORY_PATHNAME).toString()
            val baseFileName = "foo"
            val fileNameDelimiter = LiquigenExtension.FileSettings.Defaults.FILE_NAME_DELIMITER
            val timestamp = "0"
            val fileSettings = 
                createFileSettingsMockForTest(outputDirectoryPath, baseFileName, fileNameDelimiter, timestamp)
            val templatePath = createTempTemplateFile(".yaml", "").path
            val templateSettings = createTemplateSettingsMockForTest(
                LiquigenExtension.TemplateSettings.Defaults.PARAM_INTERPOLATION_TOKEN,
                templatePath,
                HashMap()
            )
            val liquigenExtension = createLiquigenExtensionMockForTest(fileSettings, templateSettings)
            val expectedFileName =
                arrayOf(timestamp, "$baseFileName.yaml")
                    .joinToString(LiquigenExtension.FileSettings.Defaults.FILE_NAME_DELIMITER)
            val expectedFilePath = Path.of(outputDirectoryPath, expectedFileName).toString()

            val file = TemplateBasedGenerator().generate(liquigenExtension)

            performFileSettingsMockVerifications(fileSettings)
            performTemplateSettingsMockVerification(templateSettings)
            performLiquigenExtensionMockVerification(liquigenExtension)
            file.path.shouldBe(expectedFilePath)
        }

        it("""returns a file with the contents of the template file specified
            | in the argument extension, if the template contains no parameter
            |  interpolation tokens as defined by the argument extension""".trimMargin()) {
            val fileSettings = createFileSettingsMockForTest(
                outputDirectoryPath = Path.of(SYSTEM_TEMP_DIRECTORY_PATHNAME).toString(),
                baseFileName = "foo",
                fileNameDelimiter = "_",
                timestamp = "0"
            )
            val templateFile = createTempTemplateFile(".yaml", """
                databaseChangeLog:
                    - changeSet:
                        id: foo
                        author: bar
                        dbms: baz
                        changes:
                            - sql:
                                sql:
                        rollback:
                            - sql:
                                sql:
            """.trimIndent())
            val templateSettings = createTemplateSettingsMockForTest(
                LiquigenExtension.TemplateSettings.Defaults.PARAM_INTERPOLATION_TOKEN,
                templateFile.path,
                HashMap()
            )
            val liquigenExtension = createLiquigenExtensionMockForTest(fileSettings, templateSettings)

            val file = TemplateBasedGenerator().generate(liquigenExtension)

            performFileSettingsMockVerifications(fileSettings)
            performTemplateSettingsMockVerification(templateSettings)
            performLiquigenExtensionMockVerification(liquigenExtension)
            file.readText().shouldBe(templateFile.readText())
        }

        it("""returns a file with the contents of the template file specified in the argument extension,
            | with instances of the parameter interpolation token specified in the extension replaced
            | with the respective associated values also specified in it""".trimMargin()) {
            val fileSettings = createFileSettingsMockForTest(
                outputDirectoryPath = Path.of(SYSTEM_TEMP_DIRECTORY_PATHNAME).toString(),
                baseFileName = "foo",
                fileNameDelimiter = "_",
                timestamp = "0"
            )
            val template = """
                databaseChangeLog:
                    - changeSet:
                        id: (id)
                        author: (author)
                        dbms: (dbms)
                        changes:
                            - sql:
                                sql:
                        rollback:
                            - sql:
                                sql:
            """.trimIndent()
            val templateFile = createTempTemplateFile(".yaml", template)
            val tokenValuesByParam = mapOf ("id" to "foo", "author" to "bar", "dbms" to "baz")
            val templateSettings = createTemplateSettingsMockForTest(
                "(${LiquigenExtension.TemplateSettings.PARAM_PLACEHOLDER})",
                templateFile.path,
                tokenValuesByParam
            )
            val liquigenExtension = createLiquigenExtensionMockForTest(fileSettings, templateSettings)
            val expectedFileContents = """
                databaseChangeLog:
                    - changeSet:
                        id: ${tokenValuesByParam["id"]}
                        author: ${tokenValuesByParam["author"]}
                        dbms: ${tokenValuesByParam["dbms"]}
                        changes:
                            - sql:
                                sql:
                        rollback:
                            - sql:
                                sql:
            """.trimIndent()

            val file = TemplateBasedGenerator().generate(liquigenExtension)

            performFileSettingsMockVerifications(fileSettings)
            performTemplateSettingsMockVerification(templateSettings)
            performLiquigenExtensionMockVerification(liquigenExtension)
            file.readText().shouldBe(expectedFileContents)
        }
    }
})
