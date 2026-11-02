package com.example.loginapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginapp.data.local.UserDao
import com.example.loginapp.data.local.UserEntity
import com.example.loginapp.model.LoginErrores
import com.example.loginapp.model.LoginUiState
import com.example.loginapp.model.UserRole
import com.example.loginapp.navigation.NavigationEvent
import com.example.loginapp.navigation.Screen
import com.example.loginapp.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userDao: UserDao? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    fun saveGoogleUserPassword(password: String) {
        if (password.length < 6) {
            _uiState.update {
                it.copy(errores = LoginErrores(passwordError = "La contraseña debe tener al menos 6 caracteres"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = LoginErrores()) }

            val result = authRepository.linkPasswordToGoogleUser(password)

            result.onSuccess {
                val firebaseUser = authRepository.getCurrentUser()
                val email = firebaseUser?.email?.lowercase()?.trim() ?: ""

                withContext(Dispatchers.IO) {
                    try {
                        userDao?.insertUser(
                            UserEntity(
                                email = email,
                                passwordHash = password,
                                provider = "google",
                                isVerified = true
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                _uiState.update { it.copy(isLoading = false) }
                // Tras vincular la contraseña, redirigir al Login para iniciar sesión de forma segura
                _navigationEvent.emit(NavigationEvent.NavigateTo(Screen.Login.route))
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errores = LoginErrores(authError = error.message ?: "Error al guardar la contraseña")
                    )
                }
            }
        }
    }
}