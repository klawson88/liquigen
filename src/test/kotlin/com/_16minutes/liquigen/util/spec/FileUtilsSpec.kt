package com._16minutes.liquigen.util.spec

import com._16minutes.liquigen.generators.timestamp.TimestampGenerator
import com._16minutes.liquigen.settings.LiquigenExtension
import com._16minutes.liquigen.util.procureFullFileName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.nio.file.Path

class FileUtilsSpec: DescribeSpec({

    describe("procureFullFileName") {
        it("""returns the file name specified in the argument
            | settings if settings contains no timestampGenerator""".trimMargin()) {
            val outputDirectoryPath = Path.of("tmp").toString()
            val baseFileName = "foo"
            val fileNameDelimiter = "_"
            val fileSettings =
                LiquigenExtension.FileSettings(outputDirectoryPath, baseFileName, fileNameDelimiter, null)

            val actualResult = procureFullFileName(fileSettings)

            actualResult.shouldBe(baseFileName)
        }

        it("""returns a String that begins with the type of timestamp produced by 
            | the timestampGenerator in the argument settings if present, followed
            | by the delimiter and file name specified in the settings""".trimMargin()) {
            val outputDirectoryPath = Path.of("tmp").toString()
            val baseFileName = "foo"
            val fileNameDelimiter = "_"
            val timestampGenerator = mockk<TimestampGenerator>()
            val fileSettings =
                LiquigenExtension.FileSettings(outputDirectoryPath, baseFileName, fileNameDelimiter, timestampGenerator)
            val expectedTimestamp = "0"
            every {timestampGenerator.generate()} returns expectedTimestamp
            val expectedResult = "$expectedTimestamp$fileNameDelimiter$baseFileName"

            val actualResult = procureFullFileName(fileSettings)

            verify {timestampGenerator.generate()}
            confirmVerified(timestampGenerator)
            actualResult.shouldBe(expectedResult)
        }
    }
})
