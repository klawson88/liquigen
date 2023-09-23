package com._16minutes.liquigen.generators.timestamp

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import java.text.ParsePosition
import java.text.SimpleDateFormat

class TimestampGeneratorSpec: DescribeSpec({
    describe("EpochTimestamp") {
        describe ("generate") {
            it("returns a UNIX timestamp in the form of a string") {
                val cut = TimestampGenerator.EpochTimestamp()

                val result = cut.generate()

                result.toLongOrNull().shouldNotBeNull()
            }
        }
    }

    describe("DateTimeTimestamp") {
        describe ("generate") {
            it("returns a timestamp of the form specified by the format property") {
                val format = "yyyy_mm_dd"
                val cut = TimestampGenerator.DateTimeTimestamp(format)

                val result = cut.generate()

                val simpleDateFormat = SimpleDateFormat(format)
                simpleDateFormat.parse(result,  ParsePosition(0)).shouldNotBeNull()
            }
        }
    }
})
