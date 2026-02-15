package com.pasindu.woundcareai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pasindu.woundcareai.data.local.entity.Patient

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    @Query("SELECT * FROM patients WHERE localPseudonym LIKE '%' || :query || '%' OR mrnHash LIKE '%' || :query || '%'")
    suspend fun searchPatients(query: String): List<Patient>

    @Query("SELECT * FROM patients WHERE patientId = :id")
    suspend fun getPatientById(id: String): Patient?
}