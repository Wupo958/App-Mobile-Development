package com.example.randomuserapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.randomuserapp.data.AppDatabase
import com.example.randomuserapp.data.UserRepository
import com.example.randomuserapp.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun UserEditScreen(userId: Int?, navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { UserRepository(db) }

    var user by remember { mutableStateOf(User(0, "", "", "", "", "")) }

    LaunchedEffect(userId) {
        if (userId != null) {
            user = repository.getUserById(userId)
        }
    }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(value = user.firstName, onValueChange = { user = user.copy(firstName = it) }, label = { Text("Name") })
        OutlinedTextField(value = user.lastName, onValueChange = { user = user.copy(lastName = it) }, label = { Text("Lastname") })
        OutlinedTextField(value = user.phone, onValueChange = { user = user.copy(phone = it) }, label = { Text("Phone") })
        OutlinedTextField(value = user.dob, onValueChange = { user = user.copy(dob = it) }, label = { Text("Date of Brith") })
        OutlinedTextField(value = user.photoUrl, onValueChange = { user = user.copy(photoUrl = it) }, label = { Text("Photo-URL") })

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                if (userId == null) {
                    repository.insert(user)
                } else {
                    repository.updateUser(user.copy(id = userId))
                }
                withContext(Dispatchers.Main) {
                    navController.popBackStack()
                }
            }
        }) {
            Text("Save")
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Cancel")
        }
    }
}