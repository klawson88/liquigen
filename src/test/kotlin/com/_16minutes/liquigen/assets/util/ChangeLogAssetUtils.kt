package com._16minutes.liquigen.assets.util

import java.io.File

const val CHANGE_LOG_OUTPUT_DIRECTORY_PREFIX = "liquigenTestOutputDir-"

fun getChangeLogFromTestOutputDirectory(outputDirectory: File): File {
    return outputDirectory.listFiles()[0]
}

