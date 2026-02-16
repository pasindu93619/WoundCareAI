package com.pasindu.woundcareai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "consents",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["patientId"])        // ✅ FIX warning
    ]
)
data class Consent(
    @PrimaryKey val consentId: String = UUID.randomUUID().toString(),
    val patientId: String,

    val signedAtMillis: Long = System.currentTimeMillis(),
    val scope: String = "",
    val expiresAtMillis: Long? = null
)
