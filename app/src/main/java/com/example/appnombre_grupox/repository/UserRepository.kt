package com.example.appnombre_grupox.repository

import com.example.appnombre_grupox.model.User

class UserRepository {
    fun getUserData(): User {
        return User(name = "Estudiante Duoc UC", role = "Desarrollador Android")
    }
}