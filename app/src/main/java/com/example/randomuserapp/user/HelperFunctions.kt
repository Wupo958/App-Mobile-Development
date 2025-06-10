package com.example.randomuserapp.user

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(isoDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = parser.parse(isoDate)
        formatter.format(date!!)
    } catch (e: Exception) {
        isoDate // Fallback
    }
}