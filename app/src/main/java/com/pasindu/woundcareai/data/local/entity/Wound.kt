package com.pasindu.woundcareai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "wounds",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["patientId"])        // ✅ removes warning
    ]
)
data class Wound(
    @PrimaryKey val woundId: String = UUID.randomUUID().toString(),
    val patientId: String,

    val locationCode: String = "",
    val aetiology: String = "",
    val onsetDateMillis: Long? = null,

    val createdAtMillis: Long = System.currentTimeMillis()
)
