package com.taskflow.data.local

import androidx.room.TypeConverter
import java.time.LocalDateTime

class RoomConverters {

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let(LocalDateTime::parse)
}
