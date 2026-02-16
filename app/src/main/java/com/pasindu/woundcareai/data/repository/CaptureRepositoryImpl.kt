package com.pasindu.woundcareai.data.repository

import android.content.Context
import com.pasindu.woundcareai.data.local.dao.ImageAssetDao
import com.pasindu.woundcareai.data.local.dao.VisitDao
import com.pasindu.woundcareai.data.local.entity.ImageAsset
import com.pasindu.woundcareai.data.local.entity.Visit
import com.pasindu.woundcareai.domain.repository.CaptureRepository
import com.pasindu.woundcareai.feature.guidance.GuidanceUiState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class CaptureRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageAssetDao: ImageAssetDao,
    private val visitDao: VisitDao
) : CaptureRepository {

    override suspend fun saveCapturedImage(
        visitId: String,
        tempFile: File,
        guidanceState: GuidanceUiState
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 1. Ensure Visit exists (Auto-create if missing for this generic/temp flow)
            // In a real flow, Visit is created before Camera.
            val existingVisit = visitDao.getVisitById(visitId)
            if (existingVisit == null) {
                // Create a dummy/temp visit for the captured image if it doesn't exist
                // This prevents Foreign Key constraint failures during testing
                val tempVisit = Visit(
                    visitId = visitId,
                    patientId = "temp_patient", // Placeholder
                    woundId = "temp_wound",     // Placeholder
                    date = System.currentTimeMillis()
                )
                visitDao.insertVisit(tempVisit)
            }

            // 2. Move file to internal storage
            val imagesDir = File(context.filesDir, "wound_images")
            if (!imagesDir.exists()) imagesDir.mkdirs()

            val newFileName = "IMG_${UUID.randomUUID()}.jpg"
            val destFile = File(imagesDir, newFileName)

            tempFile.copyTo(destFile, overwrite = true)
            // Optional: Delete temp file? tempFile.delete()

            // 3. Serialize Metadata
            val metricsJson = "{" +
                    "\"pitch\":${guidanceState.pitch}," +
                    "\"roll\":${guidanceState.roll}," +
                    "\"lux\":${guidanceState.brightnessLux}," +
                    "\"blur\":${guidanceState.isBlurry}," +
                    "\"accepted\":${guidanceState.isLevel && guidanceState.isLightingAcceptable}" +
                    "}"

            // 4. Create Entity
            val imageId = UUID.randomUUID().toString()
            val imageAsset = ImageAsset(
                id = imageId,
                visitId = visitId,
                filePath = destFile.absolutePath,
                timestamp = System.currentTimeMillis(),
                width = 0, // TODO: Read from Bitmap if needed
                height = 0,
                guidanceMetricsJson = metricsJson
            )

            // 5. Save to DB
            imageAssetDao.insertImageAsset(imageAsset)

            Result.success(imageId)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}