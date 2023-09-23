package com._16minutes.liquigen.assets

import com._16minutes.liquigen.util.SYSTEM_TEMP_DIRECTORY_PATHNAME
import java.io.File
import java.nio.file.Paths
import java.util.*

class LiquigenAssetProvider(assetGroupId: String) {
    companion object {
        const val CHANGE_LOG_OUTPUT_DIRECTORY_PREFIX = "liquigenTestOutputDir-"
    }

    val outputDirectory: File =
        Paths
            .get(SYSTEM_TEMP_DIRECTORY_PATHNAME, "$CHANGE_LOG_OUTPUT_DIRECTORY_PREFIX$assetGroupId")
            .toFile()

    constructor(): this(UUID.randomUUID().toString())
}
