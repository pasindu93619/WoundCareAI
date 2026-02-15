package com.pasindu.woundcareai.data.repository

import com.pasindu.woundcareai.data.local.dao.PatientDao
import com.pasindu.woundcareai.data.local.entity.Patient
import com.pasindu.woundcareai.domain.repository.PatientRepository
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(
    private val patientDao: PatientDao
) : PatientRepository {
    override suspend fun createPatient(patient: Patient) {
        patientDao.insertPatient(patient)
    }

    override suspend fun searchPatients(query: String): List<Patient> {
        return patientDao.searchPatients(query)
    }

    override suspend fun getPatientById(patientId: String): Patient? {
        return patientDao.getPatientById(patientId)
    }
}