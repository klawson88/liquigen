package com._16minutes.liquigen.settings

import com._16minutes.liquigen.generators.timestamp.TimestampGenerator
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.nio.file.Path

class LiquigenExtensionSpec : DescribeSpec({

    describe("Nested classes") {
        describe("FileSettings") {
            describe("mergeInDefaults") {
                it("sets uninitialized properties to default values") {
                    val cut = LiquigenExtension.FileSettings()

                    cut.mergeInDefaults()

                    cut.outputDirectoryPath.shouldBe(LiquigenExtension.FileSettings.Defaults.OUTPUT_DIRECTORY_PATH)
                    cut.baseFileName.shouldBe(LiquigenExtension.FileSettings.Defaults.BASE_FILE_NAME)
                    cut.fileNameDelimiter.shouldBe(LiquigenExtension.FileSettings.Defaults.FILE_NAME_DELIMITER)
                }

                it("preserves initialized property values") {
                    val outputDirectoryPath = Path.of("tmp").toString()
                    val baseFileName = "foo"
                    val fileNameDelimiter = "-"
                    val timestampGenerator = TimestampGenerator.EpochTimestamp()
                    val cut = LiquigenExtension.FileSettings(
                        outputDirectoryPath,
                        baseFileName,
                        fileNameDelimiter,
                        timestampGenerator
                    )

                    cut.mergeInDefaults()

                    cut.outputDirectoryPath.shouldBe(outputDirectoryPath)
                    cut.baseFileName.shouldBe(baseFileName)
                    cut.fileNameDelimiter.shouldBe(fileNameDelimiter)
                    cut.timestampGenerator.shouldBe(timestampGenerator)
                }
            }

            describe("TimestampGenerator")
        }

        describe("TemplateSettings") {
            describe("mergeInDefaults") {
                it("sets uninitialized properties to default values") {
                    val cut = LiquigenExtension.TemplateSettings()

                    cut.mergeInDefaults()

                    cut
                        .templateParamInterpolationToken
                        .shouldBe(LiquigenExtension.TemplateSettings.Defaults.PARAM_INTERPOLATION_TOKEN)
                    cut.templateParamNamesAndValues.shouldBe(LiquigenExtension.TemplateSettings.Defaults.TEMPLATE_PARAM_NAMES_AND_VALUES)
                    cut.templatePath.shouldBe(LiquigenExtension.TemplateSettings.Defaults.TEMPLATE_PATH)
                }

                it("preserves initialized property values") {
                    val paramInterpolationToken = "[param]"
                    val tokenValuesByParam = mapOf("foo" to "1")
                    val templatePath = "/foo/bar"

                    val cut =
                        LiquigenExtension.TemplateSettings(paramInterpolationToken, tokenValuesByParam, templatePath)

                    cut.templateParamInterpolationToken.shouldBe(paramInterpolationToken)
                    cut.templateParamNamesAndValues.shouldBe(tokenValuesByParam)
                    cut.templatePath.shouldBe(templatePath)
                }
            }
        }


    }

    describe("mergeInDefaults") {
        it("""calls the "mergeInDefaults" methods of its settings members""".trimMargin()) {
            val fileSettings = mockk<LiquigenExtension.FileSettings>()
            val templateSettings = mockk<LiquigenExtension.TemplateSettings>()
            every { fileSettings.mergeInDefaults() } just Runs
            every { templateSettings.mergeInDefaults() } just Runs
            val cut = LiquigenExtension(fileSettings, templateSettings)

            cut.mergeDefaultsInToSettings()

            verify { fileSettings.mergeInDefaults() }
            verify { fileSettings.mergeInDefaults() }
        }

    }
})
