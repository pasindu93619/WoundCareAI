package com.pasindu.woundcareai.domain.repository

import com.pasindu.woundcareai.feature.guidance.GuidanceUiState
import java.io.File

interface CaptureRepository {
    suspend fun saveCapturedImage(
        visitId: String,
        tempFile: File,
        guidanceState: GuidanceUiState
    ): Result<String> // Returns the ID of the saved ImageAsset
}