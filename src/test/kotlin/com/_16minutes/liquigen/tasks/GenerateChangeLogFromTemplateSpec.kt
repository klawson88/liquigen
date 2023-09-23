package com._16minutes.liquigen.tasks

import com._16minutes.liquigen.Liquigen
import com._16minutes.liquigen.assets.LiquigenAssetProvider
import com._16minutes.liquigen.assets.ProjectAssetProvider
import com._16minutes.liquigen.generators.timestamp.TimestampGenerator
import com._16minutes.liquigen.settings.LiquigenExtension
import com._16minutes.liquigen.settings.util.mergeDefaultsAndTaskOptionsIntoSettings
import com._16minutes.liquigen.tasks.util.validateChangeLogName
import com._16minutes.liquigen.tasks.util.validateTemplateBasedChangeLogContent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File

class GenerateChangeLogFromTemplateSpec: DescribeSpec({
    describe("execute") {
        fun validateGenerateChangeLogFromTemplateTaskRun(
            liquigenExtension: LiquigenExtension,
            taskOptionsByName: Map<String, String>
        ) {
            val outputDirectoryPath =
                taskOptionsByName["outputDirectoryPath"]
                ?: liquigenExtension.fileSettings.outputDirectoryPath
            val outputDirectory = File(outputDirectoryPath)
            outputDirectory.mkdir().shouldBeTrue()
            val projectAssetProvider = ProjectAssetProvider.getInitializedProvider()
            projectAssetProvider.buildFile.appendText(liquigenExtension.toString())
            val taskArguments = mutableListOf(Liquigen.TaskName.GENERATE_CHANGELOG_FROM_TEMPLATE.value)
            taskArguments.addAll(taskOptionsByName.asSequence().map { "--${it.key}=${it.value}" })

            val result =
                GradleRunner
                    .create()
                    .withProjectDir(projectAssetProvider.projectDirectory)
                    .withArguments(taskArguments)
                    .withPluginClasspath()
                    .forwardOutput()
                    .build()

            val taskName = Liquigen.TaskName.GENERATE_CHANGELOG_FROM_TEMPLATE
            result
                .task(":${taskName.value}")!!
                .outcome
                .shouldBe(TaskOutcome.SUCCESS)
            val changeLogFile = outputDirectory.listFiles()[0]
            changeLogFile.exists().shouldBeTrue()
            // Merge default values and task options in to the extension settings in order to ensure it mimics
            // the state of that it was used to create for the task run, at the time the task was run
            mergeDefaultsAndTaskOptionsIntoSettings(liquigenExtension, taskOptionsByName)
            validateChangeLogName(changeLogFile, liquigenExtension, taskName)
            validateTemplateBasedChangeLogContent(changeLogFile, liquigenExtension.templateSettings)
        }

        it("""generates a changelog as implicitly specified by the default plugin extension settings""".trimMargin()) {
            val liquigenAssetProvider = LiquigenAssetProvider()
            val liquigenExtension = LiquigenExtension()
            liquigenExtension.fileSettings.outputDirectoryPath = liquigenAssetProvider.outputDirectory.absolutePath

            validateGenerateChangeLogFromTemplateTaskRun(liquigenExtension, emptyMap())
        }

        it("generates a changelog as explicitly specified through the plugin extension") {
            val liquigenAssetProvider = LiquigenAssetProvider()
            val liquigenExtension = LiquigenExtension()
            liquigenExtension.fileSettings.outputDirectoryPath = liquigenAssetProvider.outputDirectory.absolutePath
            liquigenExtension.fileSettings.fileNameDelimiter = "-"
            liquigenExtension.fileSettings.baseFileName = "test-changelog"
            liquigenExtension.fileSettings.timestampGenerator = TimestampGenerator.DateTimeTimestamp("yyyy-MM-dd")
            liquigenExtension.templateSettings.templateParamNamesAndValues = mapOf(
                "id" to "foo",
                "author" to "bar",
                "dbms" to "baz"
            )

            liquigenExtension.fileSettings.outputDirectoryPath
                .shouldNotBe(LiquigenExtension.FileSettings.Defaults.OUTPUT_DIRECTORY_PATH)
            liquigenExtension.fileSettings.fileNameDelimiter
                .shouldNotBe(LiquigenExtension.FileSettings.Defaults.FILE_NAME_DELIMITER)
            liquigenExtension.fileSettings.baseFileName
                .shouldNotBe(LiquigenExtension.FileSettings.Defaults.BASE_FILE_NAME)
            liquigenExtension.fileSettings.timestampGenerator
                .shouldNotBe(LiquigenExtension.FileSettings.Defaults.TIMESTAMP_GENERATOR)
            liquigenExtension.templateSettings.templateParamNamesAndValues
                .shouldNotBe(LiquigenExtension.TemplateSettings.Defaults.TEMPLATE_PARAM_NAMES_AND_VALUES)

            validateGenerateChangeLogFromTemplateTaskRun(liquigenExtension, emptyMap())
        }

        it ("generates a changelog as specified through command-line options, if present") {
            val liquigenAssetProvider = LiquigenAssetProvider()
            val templateParamNamesAndValues = mapOf(
                "id" to "foo",
                "author" to "bar",
                "dbms" to "baz"
            )
            val taskOptionsByName = mapOf(
                "outputDirectoryPath" to liquigenAssetProvider.outputDirectory.absolutePath,
                "baseFileName" to "test-changelog",
                "fileNameDelimiter" to "-",
                "timestampFormat" to "yyyy-MM-dd",
                "templateParams" to
                        templateParamNamesAndValues
                            .entries
                            .asSequence()
                            .map{"${it.key}=${it.value}"}
                            .joinToString(";")
            )

            taskOptionsByName["outputDirectory"]
                .shouldNotBe(LiquigenExtension.FileSettings.Defaults.OUTPUT_DIRECTORY_PATH)
            taskOptionsByName["baseFileName"]
                .shouldNotBe(LiquigenExtension.FileSettings.Defaults.BASE_FILE_NAME)
            taskOptionsByName["fileNameDelimiter"]
                .shouldNotBe(LiquigenExtension.FileSettings.Defaults.FILE_NAME_DELIMITER)
            templateParamNamesAndValues.shouldNotBe(LiquigenExtension.TemplateSettings.Defaults.TEMPLATE_PARAM_NAMES_AND_VALUES)

            validateGenerateChangeLogFromTemplateTaskRun(LiquigenExtension(), taskOptionsByName)
        }
    }
})
