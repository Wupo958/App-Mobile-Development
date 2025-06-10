package com.example.randomuserapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.randomuserapp.data.AppDatabase
import com.example.randomuserapp.data.UserRepository
import com.example.randomuserapp.screens.CameraScreen
import com.example.randomuserapp.screens.SettingsScreen
import com.example.randomuserapp.screens.UserDetailScreen
import com.example.randomuserapp.screens.UserOverviewScreen
import com.example.randomuserapp.ui.theme.RandomUserAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(db)

        lifecycleScope.launch {
            if (!repository.hasUsers()) {
                repository.refreshUsers()
            }
        }

        setContent {
            RandomUserAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "overview",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("overview") { UserOverviewScreen(navController) }
            composable("camera") { CameraScreen() }
            composable("settings") { SettingsScreen() }
            composable("detail/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                if (userId != null) {
                    UserDetailScreen(userId, navController)
                } else {
                    Text("Fehler: Keine ID übergeben")
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val items = listOf(
            NavItem("Overview", "overview"),
            NavItem("Camera", "camera"),
            NavItem("Settings", "settings"),
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = {Icon(Icons.Default.Home, contentDescription = null)},
                label = { Text(item.title) },
                selected = navController.currentDestination?.route == item.route,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}

data class NavItem(val title: String, val route: String)

