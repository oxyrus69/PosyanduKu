package com.posyanduku.app.data.db

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter fun toDate(v: Long?): LocalDate? = v?.let { LocalDate.ofEpochDay(it) }
    @TypeConverter fun fromDate(d: LocalDate?): Long? = d?.toEpochDay()
}
