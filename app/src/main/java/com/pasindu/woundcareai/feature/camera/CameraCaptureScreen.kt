package com.pasindu.woundcareai.feature.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.pasindu.woundcareai.feature.guidance.GuidanceUiState
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs

@Composable
fun CameraCaptureScreen(
    visitId: String,
    viewModel: CameraController = hiltViewModel(),
    onImageCaptured: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val guidanceState by viewModel.guidanceState.collectAsState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        CameraContent(
            visitId = visitId,
            guidanceState = guidanceState,
            onAnalyzeImage = viewModel::analyzeImage,
            onImageCaptured = onImageCaptured
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text("Grant Camera Permission")
            }
        }
    }
}

@Composable
fun CameraContent(
    visitId: String,
    guidanceState: GuidanceUiState,
    onAnalyzeImage: (androidx.camera.core.ImageProxy) -> Unit,
    onImageCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    var isCameraReady by remember { mutableStateOf(false) }

    // Executor for the image analysis to prevent blocking the UI thread
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        val cameraProvider = context.getCameraProvider()
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
            onAnalyzeImage(imageProxy)
        }

        preview.setSurfaceProvider(previewView.surfaceProvider)

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )
            isCameraReady = true
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to start camera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        floatingActionButton = {
            if (isCameraReady) {
                FloatingActionButton(
                    onClick = {
                        takePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            onImageCaptured = onImageCaptured,
                            onError = {
                                Toast.makeText(context, "Capture failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier.padding(bottom = 32.dp),
                    containerColor = if (guidanceState.guidanceMessage == "Perfect")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                ) {
                    Box(modifier = Modifier.size(24.dp)) {
                        Text("SNAP", color = Color.White, fontSize = 10.sp, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize(),
                update = {
                    it.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            )

            if (!isCameraReady) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Guidance Overlay
            GuidanceOverlay(
                state = guidanceState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun GuidanceOverlay(
    state: GuidanceUiState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Center Crosshair / Leveler
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val lineLength = 300f

            // Rotate based on device roll to show "horizon"
            // If device rolls left, line rotates right to stay level with world (conceptually)
            // But here we want to show the deviation.
            // Let's visualize the target: A static horizontal line (target) and a moving line (current).

            // 1. Static Target Line (White, faint)
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(centerX - lineLength, centerY),
                end = Offset(centerX + lineLength, centerY),
                strokeWidth = 4f
            )

            // 2. Active Indicator Line
            // We rotate the canvas context to match the roll.
            rotate(degrees = -state.roll) {
                drawLine(
                    color = if (state.isLevel) Color.Green else Color.Red,
                    start = Offset(centerX - lineLength, centerY),
                    end = Offset(centerX + lineLength, centerY),
                    strokeWidth = 8f
                )
            }
        }

        // Guidance Text / Instructions
        if (state.guidanceMessage != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = state.guidanceMessage ?: "",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black,
                            blurRadius = 4f
                        )
                    )
                )
            }
        }

        // Debug Metrics (Optional, for clinical validation)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(text = "Lux: ${state.brightnessLux.toInt()}", color = Color.White, fontSize = 12.sp)
            Text(text = "Pitch: ${state.pitch.toInt()}°", color = Color.White, fontSize = 12.sp)
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (String) -> Unit,
    onError: (Exception) -> Unit
) {
    val filename = "WOUND_${System.currentTimeMillis()}.jpg"
    val file = java.io.File(context.cacheDir, filename)

    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                onError(exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onImageCaptured(file.absolutePath)
            }
        }
    )
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    cameraProviderFuture.addListener({
        continuation.resume(cameraProviderFuture.get())
    }, ContextCompat.getMainExecutor(this))
}