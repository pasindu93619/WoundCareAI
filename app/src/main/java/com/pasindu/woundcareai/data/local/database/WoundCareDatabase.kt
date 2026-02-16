package com.pasindu.woundcareai.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.pasindu.woundcareai.data.local.dao.PatientDao
import com.pasindu.woundcareai.data.local.entity.*
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Database(
    entities = [
        Patient::class,
        Wound::class,
        Visit::class,
        ImageAsset::class,
        Measurement::class,
        DressingEvent::class,
        Consent::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WoundCareDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
}