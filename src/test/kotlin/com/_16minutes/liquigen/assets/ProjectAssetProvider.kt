package com._16minutes.liquigen.assets

import com._16minutes.liquigen.util.SYSTEM_TEMP_DIRECTORY_PATHNAME

import java.nio.file.Paths
import java.util.*

class ProjectAssetProvider(assetGroupId: String) {
    companion object {
        private const val PROJECT_DIRECTORY_PREFIX = "liquigenIntegrationTestExecutionProject-"
        const val PROJECT_NAME = "integration-test"
        private const val SETTINGS_FILE_NAME = "settings.gradle.kts"
        private const val BUILD_FILE_NAME = "build.gradle.kts"
        private val INITIAL_SETTINGS_FILE_TEXT = """
            rootProject.name = "$PROJECT_NAME"
        """.trimIndent()
        private val INITIAL_BUILD_FILE_TEXT = """
            import com._16minutes.liquigen.settings.LiquigenExtension
            
            plugins {
                id("com._16minutes.liquigen")
            }
        """.trimIndent()

        /**
         *
         */
        fun getInitializedProvider():ProjectAssetProvider {
            val provider = ProjectAssetProvider()
            provider.projectDirectory.mkdir()
            provider.settingsFile.writeText(INITIAL_SETTINGS_FILE_TEXT)
            provider.buildFile.writeText(INITIAL_BUILD_FILE_TEXT)

            return provider
        }
    }

    val projectDirectory =
        Paths
            .get(SYSTEM_TEMP_DIRECTORY_PATHNAME, "${PROJECT_DIRECTORY_PREFIX}$assetGroupId")
            .toFile()
    val settingsFile = Paths.get(projectDirectory.canonicalPath, SETTINGS_FILE_NAME).toFile()
    val buildFile = Paths.get(projectDirectory.canonicalPath, BUILD_FILE_NAME).toFile()

    constructor(): this(UUID.randomUUID().toString())
}
