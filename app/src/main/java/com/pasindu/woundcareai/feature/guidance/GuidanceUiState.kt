package com.pasindu.woundcareai.feature.guidance

/**
 * Represents the state of the real-time capture guidance overlays.
 */
data class GuidanceUiState(
    val isLevel: Boolean = false, // True if device is roughly parallel to the ground
    val pitch: Float = 0f,        // Device tilt (up/down)
    val roll: Float = 0f,         // Device tilt (left/right)
    val isLightingAcceptable: Boolean = true,
    val brightnessLux: Float = 0f,
    val isBlurry: Boolean = false,
    val guidanceMessage: String? = null // User-facing instruction (e.g., "Hold still", "More light needed")
)