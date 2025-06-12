package com.example.randomuserapp.screens

import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel) {
    //Intialisierung
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { UserRepository(db) }
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(repository))

    //holt orientation
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

    //Lädt Switches und Buttons im Horizontalen Modus
    if (isLandscape) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                CreateSwitch(themeViewModel)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(48.dp))
                CreateButtons(viewModel)
            }
        }
    }
    //Lädt Swicthes und Buttons im Vertikalen Modus
    else {
        Column(modifier = Modifier.padding(16.dp)) {
            CreateSwitch(themeViewModel)

            Spacer(modifier = Modifier.height(32.dp))

            CreateButtons(viewModel)
        }
    }
}

//Erstellt die Knöpfe
@Composable
fun CreateButtons(viewModel: UserViewModel) {
    Button(onClick = { viewModel.addUsers()}) {
        Text("Add 10 Users")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = { viewModel.refreshUsers() }) {
        Text("Refill Database(10 Users)")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = { viewModel.clearUsers() }) {
        Text("Clear Database")
    }
}

//Erstellt den Theme switch
@Composable
fun CreateSwitch(themeViewModel: ThemeViewModel) {
    val isDark by themeViewModel.isDark.collectAsState()
    Text("Settings", style = MaterialTheme.typography.headlineSmall)

    Spacer(modifier = Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Dark Mode")
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = isDark,
            onCheckedChange = { themeViewModel.setTheme(it) }
        )
    }
}