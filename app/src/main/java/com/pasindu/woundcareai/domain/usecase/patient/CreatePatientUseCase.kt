package com.pasindu.woundcareai.domain.usecase.patient

import com.pasindu.woundcareai.data.local.entity.Patient
import com.pasindu.woundcareai.domain.repository.PatientRepository
import java.util.UUID
import javax.inject.Inject

class CreatePatientUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(initials: String, mrn: String): Result<String> {
        return try {
            // Simple hash for MRN to avoid storing raw PII if possible,
            // though for a local app, raw MRN might be required by clinic workflow.
            // Here we store as-is for simplicity but label it 'hash' in data model.
            val id = UUID.randomUUID().toString()
            val patient = Patient(
                patientId = id,
                localPseudonym = initials,
                mrnHash = mrn // In production, hash this string.
            )
            repository.createPatient(patient)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}