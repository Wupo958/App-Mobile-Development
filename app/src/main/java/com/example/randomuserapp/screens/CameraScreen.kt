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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.randomuserapp.data.AppDatabase
import com.example.randomuserapp.data.UserRepository
import com.example.randomuserapp.user.User
import com.example.randomuserapp.user.formatDate
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    var barcodeBox by remember { mutableStateOf<Rect?>(null) }
    var userOverlay by remember { mutableStateOf<User?>(null) }
    var lastDetectedTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(200)
            val now = System.currentTimeMillis()
            if (now - lastDetectedTime > 1000) {
                barcodeBox = null
                userOverlay = null
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        userOverlay?.let { user ->
            barcodeBox?.let { box ->
                Box(
                    modifier = Modifier
                        .absoluteOffset(
                            x = box.left.coerceAtLeast(0).dp,
                            y = box.top.coerceAtLeast(0).dp
                        )
                        .width(box.width().dp)
                        .height(box.height().dp)
                        .background(Color(0xBB000000))
                        .padding(4.dp)
                        .clickable {
                            navController.navigate("detail/${user.id}")
                        }
                ) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                        Text(
                            text = "${user.firstName} ${user.lastName}",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = formatDate(user.dob),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                            if (barcodes.isEmpty()) return@addOnSuccessListener

                            val barcode = barcodes.firstOrNull { it.rawValue != null && it.boundingBox != null }
                            barcode?.let {
                                val rawValue = it.rawValue ?: return@let
                                val box = it.boundingBox ?: return@let

                                try {
                                    val baseUrl = "https://randomuser.me/api/portraits/"

                                    val json = JSONObject(rawValue)
                                    val user = User(
                                        firstName = json.getString("firstName"),
                                        lastName = json.getString("lastName"),
                                        dob = json.getString("dob"),
                                        phone = json.getString("phone"),
                                        photoUrl = baseUrl + json.getString("photoUrl")
                                    )

                                    val db = AppDatabase.getDatabase(context)
                                    val repo = UserRepository(db)

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val savedUser = repo.insertIfNotExists(user)
                                        savedUser?.let {
                                            withContext(Dispatchers.Main) {
                                                barcodeBox = box
                                                userOverlay = it
                                                lastDetectedTime = System.currentTimeMillis()
                                            }
                                        }
                                    }

                                } catch (e: Exception) {
                                    Log.e("Scanner", "QR Fehler: ${e.message}")
                                }
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Scanner", "Scan fehlgeschlagen", it)
                        }
                        .addOnCompleteListener { imageProxy.close() }
                } else {
                    imageProxy.close()
                }
            }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analyzer)
    }
}
