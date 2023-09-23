package com._16minutes.liquigen

import com._16minutes.liquigen.generators.TemplateBasedGenerator
import com._16minutes.liquigen.settings.LiquigenExtension
import com._16minutes.liquigen.tasks.GenerateChangeLogFromTemplate
import org.gradle.api.Plugin
import org.gradle.api.Project

class Liquigen : Plugin<Project> {
    enum class TaskName(val value: String) {
        GENERATE_CHANGELOG_FROM_TEMPLATE("liquigenGenerateChangeLogFromTemplate")
    }

    companion object {
        const val PLUGIN_NAME_CAMEL_CASE = "liquigen"
        const val PLUGIN_NAME_PASCAL_CASE = "Liquigen"
    }

    private fun addExtension(project: Project): LiquigenExtension {
        return project.extensions.create(PLUGIN_NAME_CAMEL_CASE, LiquigenExtension::class.java)
    }

    private fun addTasks(project: Project, changeLogGeneratorExtension: LiquigenExtension) {
        val taskContainer = project.tasks

        taskContainer.create(
            TaskName.GENERATE_CHANGELOG_FROM_TEMPLATE.value,
            GenerateChangeLogFromTemplate::class.java,
            changeLogGeneratorExtension,
            TemplateBasedGenerator()
        )
    }
    override fun apply(project: Project) {
        val changeLogGeneratorExtension = addExtension(project)

        project.afterEvaluate {
            changeLogGeneratorExtension.mergeDefaultsInToSettings()
            addTasks(project, changeLogGeneratorExtension)
        }
    }
}
