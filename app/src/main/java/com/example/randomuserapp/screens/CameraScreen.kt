package com.example.randomuserapp.screens

import android.graphics.Rect
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.randomuserapp.data.AppDatabase
import com.example.randomuserapp.data.UserRepository
import com.example.randomuserapp.ui.theme.ThemeViewModel
import com.example.randomuserapp.user.User
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
@Composable
fun CameraScreen(navController: NavController, themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    var barcodeBox by remember { mutableStateOf<Rect?>(null) }
    var userOverlay by remember { mutableStateOf<User?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        userOverlay?.let { user ->
            barcodeBox?.let { box ->
                Box(
                    modifier = Modifier
                        .absoluteOffset(x = box.left.coerceAtLeast(0).dp, y = box.top.coerceAtLeast(0).dp)
                        .width(box.width().dp)
                        .graphicsLayer(
                            rotationZ = -5f,
                            shadowElevation = 8f,
                            shape = MaterialTheme.shapes.medium,
                            clip = true
                        )
                        .background(Color(0xDD000000))
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("detail/${user.id}")
                        }
                ) {
                    Column {
                        Text("${user.firstName} ${user.lastName}", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                        Text(user.phone, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    LaunchedEffect(previewView) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val barcodeScanner = BarcodeScanning.getClient()
        val analyzer = ImageAnalysis.Builder().build().apply {
            setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    barcodeScanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isEmpty()) {
                                userOverlay = null
                                barcodeBox = null
                            }

                            for (barcode in barcodes) {
                                val rawValue = barcode.rawValue ?: continue
                                val box = barcode.boundingBox ?: continue

                                barcodeBox = box

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
                                                userOverlay = it
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("Scanner", "QR Fehler: ${e.message}")
                                }

                                break
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Scanner", "Scan fehlgeschlagen", it)
                            userOverlay = null
                            barcodeBox = null
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
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
