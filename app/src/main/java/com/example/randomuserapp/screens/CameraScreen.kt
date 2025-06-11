package com.example.randomuserapp.screens

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.randomuserapp.data.AppDatabase
import com.example.randomuserapp.data.UserRepository
import com.example.randomuserapp.ui.theme.ThemeViewModel
import com.example.randomuserapp.user.User
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
@Composable
fun CameraScreen(navController: NavController, themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

    LaunchedEffect(previewView) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val barcodeScanner = BarcodeScanning.getClient()
        val analyzer = ImageAnalysis.Builder().build().apply {
            setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    barcodeScanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                val rawValue = barcode.rawValue ?: return@addOnSuccessListener
                                try {
                                    val json = JSONObject(rawValue)
                                    val user = User(
                                        firstName = json.getString("firstName"),
                                        lastName = json.getString("lastName"),
                                        dob = json.getString("dob"),
                                        phone = json.getString("phone"),
                                        photoUrl = json.getString("photoUrl")
                                    )

                                    val db = AppDatabase.getDatabase(context)
                                    val repo = UserRepository(db)

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val savedUser = repo.insertIfNotExists(user)
                                        savedUser?.let {
                                            withContext(Dispatchers.Main) {
                                                navController.navigate("detail/${it.id}")
                                            }
                                        }
                                    }

                                } catch (e: Exception) {
                                    Log.e("Scanner", "QR-Fehler: ${e.message}")
                                }
                            }
                        }
                        .addOnFailureListener { Log.e("Scanner", "Scan fehlgeschlagen", it) }
                        .addOnCompleteListener { imageProxy.close() }
                } else {
                    imageProxy.close()
                }
            }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            analyzer
        )
    }
}