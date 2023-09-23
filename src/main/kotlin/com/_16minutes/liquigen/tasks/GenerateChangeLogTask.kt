package com._16minutes.liquigen.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.options.Option

abstract class GenerateChangeLogTask: DefaultTask() {
    @Internal
    @Option(description = "The path of the directory to output the changelog in to")
    var outputDirectoryPath: String? = null

    @Internal
    @Option(description = "The baseFileName of the change log")
    var baseFileName: String? = null

    @Internal
    @Option(description = "The delimiter of the name of the changelog")
    var fileNameDelimiter: String? = null

    @Internal
    @Option(description = "The format of the timestamp generated for the changelog")
    var timestampFormat: String? = null
}
