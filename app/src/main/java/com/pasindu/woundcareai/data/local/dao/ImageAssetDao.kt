package com.pasindu.woundcareai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pasindu.woundcareai.data.local.entity.ImageAsset

@Dao
interface ImageAssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageAsset(imageAsset: ImageAsset)

    @Query("SELECT * FROM image_assets WHERE visitId = :visitId")
    suspend fun getImagesForVisit(visitId: String): List<ImageAsset>

    @Query("SELECT * FROM image_assets WHERE id = :id")
    suspend fun getImageById(id: String): ImageAsset?
}