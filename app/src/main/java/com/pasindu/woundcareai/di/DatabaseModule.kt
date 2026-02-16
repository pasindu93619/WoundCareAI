package com.pasindu.woundcareai.di

import android.content.Context
import androidx.room.Room
import com.pasindu.woundcareai.data.local.dao.PatientDao
import com.pasindu.woundcareai.data.local.database.WoundCareDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WoundCareDatabase {
        return Room.databaseBuilder(
            context,
            WoundCareDatabase::class.java,
            "woundcare_db"
        )
            .fallbackToDestructiveMigration() // Useful for dev/testing if schema changes
            .build()
    }

    @Provides
    @Singleton
    fun providePatientDao(db: WoundCareDatabase): PatientDao {
        return db.patientDao()
    }
}