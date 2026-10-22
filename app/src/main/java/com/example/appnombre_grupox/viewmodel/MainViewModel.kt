package com.example.appnombre_grupox.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appnombre_grupox.model.User
import com.example.appnombre_grupox.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        loadUser()
    }

    private fun loadUser() {
        _user.value = repository.getUserData()
    }
}