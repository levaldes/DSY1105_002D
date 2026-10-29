package com.example.loginapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val passwordHash: String,
    val provider: String, // "email", "google" o "apple"
    val isVerified: Boolean = false
)