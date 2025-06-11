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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.randomuserapp.ui.theme.ThemeViewModel
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import com.example.randomuserapp.screens.UserEditScreen

@androidx.camera.core.ExperimentalGetImage
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
        }

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(db)

        lifecycleScope.launch {
            if (!repository.hasUsers()) {
                repository.refreshUsers()
            }
        }

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDark by themeViewModel.isDark.collectAsState()

            RandomUserAppTheme(useDarkTheme = isDark) {
                AppNavigation(themeViewModel)
            }
        }
    }
}

@androidx.camera.core.ExperimentalGetImage
@Composable
fun AppNavigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "overview",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("overview") { UserOverviewScreen(navController, themeViewModel) }
            composable("camera") { CameraScreen(navController, themeViewModel) }
            composable("settings") { SettingsScreen(themeViewModel) }
            composable("detail/{id}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                userId?.let {
                    UserDetailScreen(userId, navController, themeViewModel)
                }
            }
            composable("create") {
                UserEditScreen(null, navController)
            }
            composable("edit/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                id?.let {
                    UserEditScreen(it, navController)
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

