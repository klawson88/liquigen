package com._16minutes.liquigen.tasks.util.spec

import com._16minutes.liquigen.generators.timestamp.TimestampGenerator
import com._16minutes.liquigen.tasks.util.createTimestampGeneratorForFormatOption
import com._16minutes.liquigen.tasks.util.parseTemplateParamsOption
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeTypeOf

class GenerateChangeLogTaskOptionUtilsSpec: DescribeSpec({
    describe("createTimestampGeneratorForFormatOption") {
        it("returns a TimestampGenerator.EpochTimestamp when timestampFormat is 'epoch'") {
            val timestampGenerator = createTimestampGeneratorForFormatOption("epoch")

            timestampGenerator.shouldNotBeNull()
            timestampGenerator!!.shouldBeTypeOf<TimestampGenerator.EpochTimestamp>()
        }

        it("returns a TimestampGenerator.DateTimeTimestamp when timestampFormat is a date time format string") {
            val timestampGenerator = createTimestampGeneratorForFormatOption("yyyy-MM-dd")

            timestampGenerator.shouldNotBeNull()
            timestampGenerator!!.shouldBeTypeOf<TimestampGenerator.DateTimeTimestamp>()
        }
    }

    describe("parseTemplateParamsOption") {
        it("returns a Map<String, String> consisting of the properties in templateParamsPropertiesStr") {
            val expectedTemplateParamNamesAndValues: Map<String, Any> = mapOf(
                "fooOne" to "barOne",
                "fooTwo" to "barTwo",
            )
            val templateParamsPropertiesStr =
                expectedTemplateParamNamesAndValues
                    .entries
                    .asSequence()
                    .map{"${it.key}=${it.value}"}
                    .joinToString(";")

            val actualTemplateParamNamesAndValues = parseTemplateParamsOption(templateParamsPropertiesStr);

            actualTemplateParamNamesAndValues.shouldBeEqual(expectedTemplateParamNamesAndValues)
        }
    }
})
