package com.example.randomuserapp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.example.randomuserapp.screens.SettingsScreen
import com.example.randomuserapp.ui.theme.ThemeViewModel
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addUsersButtonIsDisplayedAndClickable() {
        val fakeThemeViewModel = ThemeViewModel()

        composeTestRule.setContent {
            SettingsScreen(themeViewModel = fakeThemeViewModel)
        }

        composeTestRule.onNodeWithText("Add 10 Users")
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun darkModeSwitchIsDisplayed() {
        val fakeThemeViewModel = ThemeViewModel()

        composeTestRule.setContent {
            SettingsScreen(themeViewModel = fakeThemeViewModel)
        }

        composeTestRule.onNodeWithText("Dark Mode")
            .assertExists()
            .assertIsDisplayed()
    }
}