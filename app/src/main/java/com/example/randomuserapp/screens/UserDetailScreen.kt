package com.example.randomuserapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.randomuserapp.data.AppDatabase
import com.example.randomuserapp.data.UserRepository
import com.example.randomuserapp.user.User
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.randomuserapp.ui.theme.ThemeViewModel
import com.example.randomuserapp.user.formatDate
import com.example.randomuserapp.user.generateQrCodeBitmap
import org.json.JSONObject

@Composable
fun UserDetailScreen(userId: Int, navController: NavController, themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { UserRepository(db) }
    var user by remember { mutableStateOf<User?>(null) }

    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(userId) {
        user = repository.getUserById(userId)
    }

    user?.let {
        Column(modifier = Modifier.padding(16.dp)) {
            Row{
                Button(onClick = { navController.navigate("overview") }) {
                    Text("Return")
                }

                Spacer(Modifier.width(32.dp))

                Button(
                    onClick = { navController.navigate("edit/${user!!.id}") },
                    modifier = Modifier
                ) {
                    Text("Edit")
                }
            }


            Spacer(Modifier.height(32.dp))

            if (isLandscape) {
                Row(modifier = Modifier.fillMaxWidth()) {

                    AsyncImage(
                        model = it.photoUrl,
                        contentDescription = "User photo",
                        modifier = Modifier
                            .size(128.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )

                    Spacer(Modifier.width(32.dp))

                    val qrContent = JSONObject().apply {
                        put("firstName", it.firstName)
                        put("lastName", it.lastName)
                        put("dob", it.dob)
                        put("phone", it.phone)
                        put("photoUrl", it.photoUrl)
                    }.toString()

                    val qrBitmap = remember(qrContent) {
                        generateQrCodeBitmap(qrContent)
                    }

                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(128.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )


                    Spacer(Modifier.width(32.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("${it.firstName} ${it.lastName}", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(16.dp))
                        Text("Date of birth: " + formatDate(it.dob))
                        Spacer(Modifier.height(16.dp))
                        Text("Phone: ${it.phone}")
                    }
                }
            } else {
                Column() {

                    Row {
                        AsyncImage(
                            model = it.photoUrl,
                            contentDescription = "User photo",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Spacer(Modifier.width(32.dp))

                        val qrContent = JSONObject().apply {
                            put("firstName", it.firstName)
                            put("lastName", it.lastName)
                            put("dob", it.dob)
                            put("phone", it.phone)
                            put("photoUrl", it.photoUrl)
                        }.toString()

                        val qrBitmap = remember(qrContent) {
                            generateQrCodeBitmap(qrContent)
                        }

                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    Text("${it.firstName} ${it.lastName}", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    Text("Date of birth: " + formatDate(it.dob))
                    Spacer(Modifier.height(16.dp))
                    Text("Phone: ${it.phone}")
                }
            }
        }
    } ?: Text("Loading...")
}

