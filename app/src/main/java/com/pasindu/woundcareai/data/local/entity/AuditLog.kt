package com.pasindu.woundcareai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey val auditId: String,
    val visitId: String?,
    val timestamp: Date,
    val action: String,
    val actorId: String
)