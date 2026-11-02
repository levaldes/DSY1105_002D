package com.example.loginapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginapp.data.local.UserDao
import com.example.loginapp.model.*
import com.example.loginapp.navigation.NavigationEvent
import com.example.loginapp.navigation.Screen
import com.example.loginapp.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userDao: UserDao? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    // Web Client ID de Google Cloud para autenticación Firebase
    private val webClientId = "682645433329-bo08am7r6grjvimr3041hbck13llt8rt.apps.googleusercontent.com"

    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(email = newEmail, errores = it.errores.copy(emailError = null, authError = null))
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(password = newPassword, errores = it.errores.copy(passwordError = null, authError = null))
        }
    }

    private fun validarFormulario(): Boolean {
        var isValid = true
        var emailErr: String? = null
        var passErr: String? = null

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
            emailErr = "Formato de correo inválido"
            isValid = false
        }

        if (_uiState.value.password.length < 6) {
            passErr = "La contraseña debe tener al menos 6 caracteres"
            isValid = false
        }

        _uiState.update { it.copy(errores = LoginErrores(emailError = emailErr, passwordError = passErr)) }
        return isValid
    }

    // --- LOGIN CORREO / CONTRASEÑA ---
    fun login() {
        if (!validarFormulario()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = LoginErrores()) }

            val result = authRepository.login(_uiState.value.email, _uiState.value.password)

            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, currentUser = user) }
                navegarSegunRol(user.role)
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errores = LoginErrores(authError = exception.message ?: "Error al iniciar sesión")
                    )
                }
            }
        }
    }

    // --- LOGIN CON GOOGLE + VERIFICACIÓN DE CONTRASEÑA LOCAL ---
    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = LoginErrores()) }
            val result = authRepository.loginWithGoogle(context = context, webClientId = webClientId)

            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, currentUser = user) }

                val cleanEmail = user.email.lowercase().trim()

                val localUser = withContext(Dispatchers.IO) {
                    userDao?.getUserByEmail(cleanEmail)
                }

                if (localUser == null || localUser.passwordHash.isEmpty()) {
                    _navigationEvent.emit(NavigationEvent.NavigateTo(Screen.CreatePassword.route))
                } else {
                    navegarSegunRol(user.role)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errores = LoginErrores(authError = exception.message ?: "Error con Google")
                    )
                }
            }
        }
    }

    // --- LOGIN CON APPLE ---
    fun loginWithApple(activity: android.app.Activity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = LoginErrores()) }
            val result = authRepository.loginWithApple(activity)

            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, currentUser = user) }
                navegarSegunRol(user.role)
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errores = LoginErrores(authError = exception.message ?: "Error con Apple")
                    )
                }
            }
        }
    }

    // --- NAVEGACIÓN Y AUXILIARES ---
    private suspend fun navegarSegunRol(role: UserRole) {
        val route = when (role) {
            UserRole.ADMIN -> Screen.HomeAdmin.route
            UserRole.SUPERVISOR -> Screen.HomeSupervisor.route
            UserRole.OPERADOR -> Screen.HomeOperator.route
        }
        _navigationEvent.emit(NavigationEvent.NavigateTo(route))
    }

    fun onNavigateToRegister() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateTo(Screen.Register.route))
        }
    }

    fun logout() {
        _uiState.update { LoginUiState() }
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateTo(Screen.Login.route))
        }
    }
}