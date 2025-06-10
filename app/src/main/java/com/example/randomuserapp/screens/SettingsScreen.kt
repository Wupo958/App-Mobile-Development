package com.example.randomuserapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.randomuserapp.data.AppDatabase
import com.example.randomuserapp.data.UserRepository
import com.example.randomuserapp.user.UserViewModel
import com.example.randomuserapp.user.UserViewModelFactory



@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { UserRepository(db) }
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(repository))

    Column(modifier = Modifier.padding(16.dp)) {

        Button(onClick = { viewModel.addUsers() }) {
            Text("Add 10 More Users")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.refreshUsers() }) {
            Text("Refill Database with 10 Users")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.clearUsers() }) {
            Text("Clear Database")
        }
    }
}