package com.pasindu.woundcareai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "dressing_events",
    foreignKeys = [
        ForeignKey(
            entity = Visit::class,
            parentColumns = ["visitId"],
            childColumns = ["visitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["visitId"])          // ✅ removes warning
    ]
)
data class DressingEvent(
    @PrimaryKey val dressingEventId: String = UUID.randomUUID().toString(),
    val visitId: String,

    val dressingType: String = "",
    val adjuncts: String? = null,

    val timestampMillis: Long = System.currentTimeMillis()
)
