package com.pasindu.woundcareai.feature.guidance

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class OrientationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    // Threshold in degrees to consider the device "level" (flat)
    private val LEVEL_THRESHOLD = 5.0

    /**
     * Returns a Flow that emits OrientationData updates based on sensor readings.
     */
    fun getOrientationFlow(): Flow<OrientationData> = callbackFlow {
        val listener = object : SensorEventListener {
            private var gravity: FloatArray? = null
            private var geomagnetic: FloatArray? = null

            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return

                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    gravity = event.values
                }
                if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    geomagnetic = event.values
                }

                if (gravity != null && geomagnetic != null) {
                    val r = FloatArray(9)
                    val i = FloatArray(9)

                    if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)

                        // Convert radians to degrees
                        // orientation[0] = Azimuth (Z)
                        // orientation[1] = Pitch (X)
                        // orientation[2] = Roll (Y)
                        val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
                        val roll = Math.toDegrees(orientation[2].toDouble()).toFloat()

                        // Determine if device is flat (parallel to ground)
                        val isLevel = abs(pitch) < LEVEL_THRESHOLD && abs(roll) < LEVEL_THRESHOLD

                        trySend(
                            OrientationData(
                                pitch = pitch,
                                roll = roll,
                                isLevel = isLevel
                            )
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No-op
            }
        }

        // Register listeners
        if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        } else {
            // Handle devices without sensors if necessary, or emit default
            trySend(OrientationData(0f, 0f, false))
        }

        // Unregister on cancellation
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}

data class OrientationData(
    val pitch: Float,
    val roll: Float,
    val isLevel: Boolean
)