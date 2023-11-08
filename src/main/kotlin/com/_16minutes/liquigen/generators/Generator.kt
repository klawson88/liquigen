package com._16minutes.liquigen.generators

import com._16minutes.liquigen.settings.LiquigenExtension
import java.io.File

interface Generator {
    fun generate(extension: LiquigenExtension, relativePathParentDirectory: File?): File
}
