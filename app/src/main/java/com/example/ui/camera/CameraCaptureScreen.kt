package com.example.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraCaptureScreen(
    onPhotoCaptured: (Uri, String?) -> Unit,
    onCancel: () -> Unit
) {
    val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    if (permissionState.allPermissionsGranted) {
        CameraPreviewContent(onPhotoCaptured = onPhotoCaptured, onCancel = onCancel)
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Camera and Location permissions are required.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                Text("Grant Permissions")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun CameraPreviewContent(
    onPhotoCaptured: (Uri, String?) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    imageCapture = ImageCapture.Builder().build()
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch(exc: Exception) {
                        Log.e("CameraCapture", "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                
                previewView
            }
        )

        // Close Button
        IconButton(
            onClick = onCancel,
            modifier = Modifier
                .padding(16.dp)
                .statusBarsPadding()
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }

        // Capture Button
        FloatingActionButton(
            onClick = {
                val currentCapture = imageCapture ?: return@FloatingActionButton
                val photoFile = File(
                    context.cacheDir,
                    SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
                )
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                
                currentCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            val savedUri = Uri.fromFile(photoFile)
                            coroutineScope.launch {
                                var locationStr: String? = null
                                try {
                                    val location: Location? = fusedLocationClient.lastLocation.await()
                                    if (location != null) {
                                        locationStr = "${location.latitude}, ${location.longitude}"
                                    }
                                } catch (e: Exception) {
                                    Log.e("CameraCapture", "Failed to get location", e)
                                }
                                onPhotoCaptured(savedUri, locationStr)
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraCapture", "Photo capture failed: ${exception.message}", exception)
                        }
                    }
                )
            },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .navigationBarsPadding()
                .align(Alignment.BottomCenter)
                .size(72.dp),
            shape = CircleShape,
            containerColor = Color.White
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = "Capture Photo", modifier = Modifier.size(36.dp), tint = Color.Black)
        }
    }
}
