package com.pasindu.woundcareai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_assets",
    indices = [Index(value = ["visitId"])],
    /* Note: We assume the Visit entity exists based on the project summary.
       If Visit is not yet created, remove the foreignKeys block temporarily.
    */
    foreignKeys = [
        ForeignKey(
            entity = Visit::class,
            parentColumns = ["visitId"],
            childColumns = ["visitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ImageAsset(
    @PrimaryKey val id: String,
    val visitId: String,
    val filePath: String,
    val timestamp: Long,
    val width: Int,
    val height: Int,
    val guidanceMetricsJson: String, // JSON: { "pitch": 0.5, "roll": 1.2, "lux": 100, "blur": false }
    val isOriginal: Boolean = true // True for raw capture, False for processed/annotated versions
)