package com.example.loginapp.model

// Errores específicos del formulario de registro
data class RegisterErrores(
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val authError: String? = null
)

// Estado general de la UI para la pantalla de registro
data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccessMessageVisible: Boolean = false,
    val successMessage: String? = null,
    val errores: RegisterErrores = RegisterErrores(),
    val currentUser: User? = null
)