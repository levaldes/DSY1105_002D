package com.example.loginapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginapp.model.*
import com.example.loginapp.navigation.NavigationEvent
import com.example.loginapp.navigation.Screen
import com.example.loginapp.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

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
// Validaciones
    fun login() {
        if (!validarFormulario()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = LoginErrores()) }

            val result = authRepository.login(_uiState.value.email, _uiState.value.password)

            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, currentUser = user) }

                val route = when (user.role) {
                    UserRole.ADMIN -> Screen.HomeAdmin.route
                    UserRole.SUPERVISOR -> Screen.HomeSupervisor.route
                    UserRole.OPERADOR -> Screen.HomeOperator.route
                }
                _navigationEvent.emit(NavigationEvent.NavigateTo(route))
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errores = LoginErrores(authError = exception.message ?: "Error desconocido")
                    )
                }
            }
        }
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