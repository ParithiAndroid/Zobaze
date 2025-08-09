package com.parithidb.zobazeassignment.util

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString() // "YYYY-MM-DD"
    }
}
