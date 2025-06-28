package com.imfibit.activitytracker.core.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val NavType.Companion.LocalDateType: NavType<LocalDate>
    get() = object : NavType<LocalDate>(false) {
        override fun get(bundle: Bundle, key: String): LocalDate? {
            return bundle.getString(key)?.let { LocalDate.parse(it) }
        }

        override fun parseValue(value: String): LocalDate {
            return LocalDate.parse(value)
        }

        override fun put(bundle: Bundle, key: String, value: LocalDate) {
            bundle.putString(key, value.format(DateTimeFormatter.ISO_LOCAL_DATE))
        }

        override val name: String
            get() = "java.time.LocalDate"
    }

val NavType.Companion.LocalTimeType: NavType<LocalTime?>
    get() = object : NavType<LocalTime?>(true) {
        override fun get(bundle: Bundle, key: String): LocalTime? {
            return bundle.getString(key)?.let { LocalTime.parse(it) }
        }

        override fun parseValue(value: String): LocalTime? {
            return LocalTime.parse(value)
        }

        override fun put(bundle: Bundle, key: String, value: LocalTime?) {
            bundle.putString(
                key,
                value?.format(DateTimeFormatter.ISO_LOCAL_TIME)
            )
        }

        override val name: String
            get() = "java.time.LocalTime"
    }


val NavType.Companion.LocalDateTimeType: NavType<LocalDateTime?>
    get() = object : NavType<LocalDateTime?>(true) {
        override fun get(bundle: Bundle, key: String): LocalDateTime? {
            return bundle.getString(key)?.let { LocalDateTime.parse(it) }
        }

        override fun parseValue(value: String): LocalDateTime? {
            return LocalDateTime.parse(value)
        }

        override fun put(bundle: Bundle, key: String, value: LocalDateTime?) {
            // Ensure you use a consistent format, ISO_LOCAL_DATE_TIME is a good default
            bundle.putString(key, value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        }

        override val name: String
            get() = "java.time.LocalDateTime"
    }

object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("java.time.LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}

// Similarly for LocalTime:
object LocalTimeSerializer : KSerializer<LocalTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("java.time.LocalTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString(), formatter)
    }
}

// And for LocalDateTime:
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("java.time.LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}