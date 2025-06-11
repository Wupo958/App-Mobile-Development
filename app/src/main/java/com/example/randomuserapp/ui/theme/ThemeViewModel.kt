package com.example.randomuserapp.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {
    private val _isDark = MutableStateFlow(false)
    val isDark: StateFlow<Boolean> = _isDark

    fun setTheme(dark: Boolean) {
        _isDark.value = dark
    }
}