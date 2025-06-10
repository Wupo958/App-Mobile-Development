package com.example.randomuserapp.user

import androidx.room.Entity
import androidx.room.PrimaryKey

// DTO + Entity combined
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val dob: String,
    val phone: String,
    val photoUrl: String
)