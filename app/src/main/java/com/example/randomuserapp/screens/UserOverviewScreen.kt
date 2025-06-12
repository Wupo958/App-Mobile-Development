package com.example.randomuserapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.randomuserapp.data.AppDatabase
import com.example.randomuserapp.data.UserRepository
import com.example.randomuserapp.user.SortOption
import com.example.randomuserapp.user.UserViewModel
import com.example.randomuserapp.user.UserViewModelFactory
import com.example.randomuserapp.user.formatDate

@Composable
fun UserOverviewScreen(navController: NavController) {
    //Intialisierung
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(UserRepository(db))
    )
    val users by viewModel.users.observeAsState(emptyList())

    //Sortierungs option laden
    LaunchedEffect(Unit) {
        viewModel.loadSortOption(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row{
            //Create user Knopf
            Button(
                onClick = { navController.navigate("create") },
                modifier = Modifier
                    .padding(bottom = 16.dp)
            ) {
                Text("Create user")
            }

            //Sort By Knopf
            SortDropdown(viewModel)
        }

        LazyColumn {
            //Karte pro User
            items(users) { user ->
                //Karte eines users
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("detail/${user.id}")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profil Bild
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = "User image",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            //Namen Text
                            Text(
                                text = "${user.firstName} ${user.lastName}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            //Geburstag Text
                            Text(
                                text = formatDate(user.dob),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

//Zum Sortieren der Overview
@Composable
fun SortDropdown(viewModel: UserViewModel) {
    val context = LocalContext.current
    val currentOption by viewModel.sortOption.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Box {
        //Knopf zum ausfahren
        Button(
            onClick = { expanded = true },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("Sort by: ${currentOption.displayName}")
        }

        //Anklickbare Optionen
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName) },
                    onClick = {
                        viewModel.setSortOption(option, context)
                        expanded = false
                    }
                )
            }
        }
    }
}


