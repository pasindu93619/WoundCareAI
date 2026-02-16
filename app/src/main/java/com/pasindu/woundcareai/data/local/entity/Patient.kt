package com.pasindu.woundcareai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val patientId: String,
    val localPseudonym: String, // Initials or local ID
    val mrnHash: String, // Hashed Medical Record Number
    val dobOptional: Date? = null,
    val createdAt: Date = Date()
)