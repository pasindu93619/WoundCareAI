package com.pasindu.woundcareai.feature.guidance

import java.nio.ByteBuffer
import javax.inject.Inject

class LightingEvaluator @Inject constructor() {

    // Threshold for acceptable brightness (approximate lux or pixel intensity)
    // 0-255 scale for Y plane. < 100 is often too dark for clinical wounds.
    private val MIN_LUMA = 80.0

    /**
     * returns Pair(isAcceptable, averageLuma)
     */
    fun evaluate(buffer: ByteBuffer, width: Int, height: Int): Pair<Boolean, Double> {
        // Simple luminosity calculation from the Y plane (grayscale)
        // We assume NV21 or YUV_420_888, where the first plane is Y.

        // Optimization: Don't scan every pixel. Scan a grid or center crop.
        // For guidance, speed is key. We sample every 16th pixel.

        var sum = 0L
        val pixelCount = buffer.remaining()
        val step = 16
        var sampledCount = 0

        // Reset position just in case
        buffer.rewind()

        // Loop through buffer with step
        for (i in 0 until pixelCount step step) {
            val pixel = buffer.get(i).toInt() and 0xFF
            sum += pixel
            sampledCount++
        }

        val avgLuma = if (sampledCount > 0) sum.toDouble() / sampledCount else 0.0
        val isAcceptable = avgLuma >= MIN_LUMA

        return Pair(isAcceptable, avgLuma)
    }
}