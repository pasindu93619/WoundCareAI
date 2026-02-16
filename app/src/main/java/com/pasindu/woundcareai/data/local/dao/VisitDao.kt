package com.pasindu.woundcareai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pasindu.woundcareai.data.local.entity.Visit

@Dao
interface VisitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: Visit)

    @Query("SELECT * FROM visits WHERE visitId = :visitId")
    suspend fun getVisitById(visitId: String): Visit?
}