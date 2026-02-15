package com.pasindu.woundcareai.domain.usecase.patient

import com.pasindu.woundcareai.data.local.entity.Patient
import com.pasindu.woundcareai.domain.repository.PatientRepository
import javax.inject.Inject

class SearchPatientsUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(query: String): List<Patient> {
        return repository.searchPatients(query)
    }
}