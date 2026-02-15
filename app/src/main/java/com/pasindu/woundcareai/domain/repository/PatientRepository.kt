package com.pasindu.woundcareai.domain.repository

import com.pasindu.woundcareai.data.local.entity.Patient
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    suspend fun createPatient(patient: Patient)
    suspend fun searchPatients(query: String): List<Patient>
    suspend fun getPatientById(patientId: String): Patient?
}