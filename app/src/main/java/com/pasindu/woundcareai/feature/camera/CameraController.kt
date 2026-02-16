package com.pasindu.woundcareai.feature.camera

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasindu.woundcareai.feature.guidance.BlurDetector
import com.pasindu.woundcareai.feature.guidance.GuidanceUiState
import com.pasindu.woundcareai.feature.guidance.LightingEvaluator
import com.pasindu.woundcareai.feature.guidance.OrientationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraController @Inject constructor(
    private val orientationManager: OrientationManager,
    private val lightingEvaluator: LightingEvaluator,
    private val blurDetector: BlurDetector
) : ViewModel() {

    private val _guidanceState = MutableStateFlow(GuidanceUiState())
    val guidanceState: StateFlow<GuidanceUiState> = _guidanceState.asStateFlow()

    init {
        observeOrientation()
    }

    private fun observeOrientation() {
        viewModelScope.launch {
            orientationManager.getOrientationFlow().collectLatest { orientation ->
                _guidanceState.update { currentState ->
                    currentState.copy(
                        isLevel = orientation.isLevel,
                        pitch = orientation.pitch,
                        roll = orientation.roll,
                        guidanceMessage = updateGuidanceMessage(
                            orientation.isLevel,
                            currentState.isLightingAcceptable,
                            currentState.isBlurry
                        )
                    )
                }
            }
        }
    }

    fun analyzeImage(imageProxy: ImageProxy) {
        // Run analysis on the image frame
        // Warning: This runs on the analysis executor thread, be careful with synchronization if needed.

        val buffer = imageProxy.planes[0].buffer
        val width = imageProxy.width
        val height = imageProxy.height

        val (isLightingOk, luma) = lightingEvaluator.evaluate(buffer, width, height)
        val isBlurry = blurDetector.isBlurry(buffer, width, height)

        _guidanceState.update { currentState ->
            currentState.copy(
                isLightingAcceptable = isLightingOk,
                brightnessLux = luma.toFloat(),
                isBlurry = isBlurry,
                guidanceMessage = updateGuidanceMessage(
                    currentState.isLevel,
                    isLightingOk,
                    isBlurry
                )
            )
        }

        imageProxy.close()
    }

    private fun updateGuidanceMessage(
        isLevel: Boolean,
        isLightingOk: Boolean,
        isBlurry: Boolean
    ): String? {
        return when {
            !isLightingOk -> "Too Dark"
            !isLevel -> "Level the phone"
            isBlurry -> "Hold Still"
            else -> "Perfect"
        }
    }
}