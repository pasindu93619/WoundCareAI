package com.pasindu.woundcareai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "measurements",
    foreignKeys = [
        ForeignKey(
            entity = ImageAsset::class,
            parentColumns = ["id"],          // ✅ FIX: ImageAsset uses "id"
            childColumns = ["imageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["imageId"])          // ✅ FIX: avoids Room warning + improves performance
    ]
)
data class Measurement(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),

    // FK -> ImageAsset.id
    val imageId: String,

    val areaCm2: Float? = null,
    val areaCiLow: Float? = null,
    val areaCiHigh: Float? = null,

    val modelVersion: String? = null,
    val notes: String? = null,

    val timestampMillis: Long = System.currentTimeMillis()
)
