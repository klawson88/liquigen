package com._16minutes.liquigen.generators.timestamp

import java.text.SimpleDateFormat
import java.util.*

open abstract class TimestampGenerator {
    class EpochTimestamp: TimestampGenerator() {
        override fun generate(): String {
            return "${System.currentTimeMillis()}"
        }
    }

    class DateTimeTimestamp(val format: String): TimestampGenerator() {
        override fun generate(): String {
            return SimpleDateFormat(format).format(Date())
        }
    }

    abstract fun generate(): String
}
