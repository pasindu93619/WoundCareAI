package com.pasindu.woundcareai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a single clinical visit/encounter where wounds are imaged.
 * Kept simple for now to satisfy Foreign Key constraints.
 */
@Entity(
    tableName = "visits",
    indices = [Index(value = ["patientId"]), Index(value = ["woundId"])],
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
        // Note: Wound entity connection omitted to avoid circular dependency loop in this step
        // if Wound entity isn't fully ready.
    ]
)
data class Visit(
    @PrimaryKey val visitId: String,
    val patientId: String,
    val woundId: String,
    val date: Long,
    val notes: String? = null
)