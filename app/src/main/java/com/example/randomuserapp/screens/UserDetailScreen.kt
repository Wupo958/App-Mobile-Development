package com.example.randomuserapp.screens

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.randomuserapp.user.formatDate
import com.example.randomuserapp.user.generateQrCodeBitmap
import org.json.JSONObject

@Composable
fun UserDetailScreen(userId: Int, navController: NavController) {
    //Intialisierung
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { UserRepository(db) }
    var user by remember { mutableStateOf<User?>(null) }

    //holt orientation
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

    //holt den angeklickten User
    LaunchedEffect(userId) {
        user = repository.getUserById(userId)
    }

    user?.let {
        Column(modifier = Modifier.padding(16.dp)) {
            Row{
                //Return Knopf
                Button(onClick = { navController.navigate("overview") }) {
                    Text("Return")
                }

                Spacer(Modifier.width(32.dp))

                //Edit Knopf
                Button(
                    onClick = { navController.navigate("edit/${user!!.id}") },
                    modifier = Modifier
                ) {
                    Text("Edit")
                }
            }

            Spacer(Modifier.height(32.dp))

            //Lädt Bilder und Text im Horizontalen Modus
            if (isLandscape) {
                Row(modifier = Modifier.fillMaxWidth()) {

                    LoadImages(it)

                    Spacer(Modifier.width(32.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        LoadTextDetailScreen(it)
                    }
                }
            }
            //Lädt Bilder und Text im Vertikalen Modus
            else {
                Column {

                    Row {
                        LoadImages(it)
                    }

                    Spacer(Modifier.height(32.dp))

                    LoadTextDetailScreen(it)
                }
            }
        }
    } ?: Text("Loading...")
}

@Composable
fun createQRCode(user: User): Bitmap {
    //base url entfernen für weniger komplexen QR Code
    val baseUrl = "https://randomuser.me/api/portraits/"
    val shortPhotoPath = user.photoUrl.removePrefix(baseUrl)

    val qrContent = JSONObject().apply {
        put("firstName", user.firstName)
        put("lastName", user.lastName)
        put("dob", user.dob)
        put("phone", user.phone)
        put("photoUrl", shortPhotoPath)
        //Log.d("user.photoUrl", user.sho)
    }.toString()

    //Create qr Code bitmap from string using QrCodeUtil.kt function
    val qrBitmap = remember(qrContent) {
        generateQrCodeBitmap(qrContent)
    }

    return qrBitmap
}

//Lädt QR Code und Profil Bild
@Composable
fun LoadImages(user: User) {
    var isQrFullscreen by remember { mutableStateOf(false) }
    AsyncImage(
        model = user.photoUrl,
        contentDescription = "User photo",
        modifier = Modifier
            .size(128.dp)
            .clip(RoundedCornerShape(10.dp))
    )

    Spacer(Modifier.width(32.dp))

    Image(
        bitmap = createQRCode(user).asImageBitmap(),
        contentDescription = "QR Code",

        modifier = if (isQrFullscreen)
            Modifier
                .size(256.dp)
                .clickable { isQrFullscreen = !isQrFullscreen }
        else
            Modifier.size(128.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { isQrFullscreen = !isQrFullscreen }
    )
}

//Lädt Namen, Geburtstag und telefonnummer
@Composable
fun LoadTextDetailScreen(user: User) {
    Text("${user.firstName} ${user.lastName}", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(16.dp))
    Text("Date of birth: " + formatDate(user.dob))
    Spacer(Modifier.height(16.dp))
    Text("Phone: ${user.phone}")
}

