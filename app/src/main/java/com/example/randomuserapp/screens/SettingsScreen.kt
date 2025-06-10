package com.example.randomuserapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.randomuserapp.data.AppDatabase
import com.example.randomuserapp.data.UserRepository
import com.example.randomuserapp.ui.theme.ThemeViewModel
import com.example.randomuserapp.user.UserViewModel
import com.example.randomuserapp.user.UserViewModelFactory
import androidx.compose.runtime.getValue

@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { UserRepository(db) }
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(repository))

    val isDark by themeViewModel.isDark.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Einstellungen", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dark Mode")
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = isDark,
                onCheckedChange = { themeViewModel.setTheme(it) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

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