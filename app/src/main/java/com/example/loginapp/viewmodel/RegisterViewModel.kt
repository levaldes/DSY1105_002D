package com.example.loginapp.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginapp.data.local.AppDatabase
import com.example.loginapp.data.local.UserEntity
import com.example.loginapp.model.*
import com.example.loginapp.navigation.NavigationEvent
import com.example.loginapp.navigation.Screen
import com.example.loginapp.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _uiState.update {
            it.copy(confirmPassword = newConfirmPassword, errores = it.errores.copy(confirmPasswordError = null, authError = null))
        }
    }

    private fun validarFormulario(): Boolean {
        var isValid = true
        var emailErr: String? = null
        var passErr: String? = null
        var confirmPassErr: String? = null

        val state = _uiState.value

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailErr = "Formato de correo inválido"
            isValid = false
        }

        if (state.password.length < 6) {
            passErr = "La contraseña debe tener al menos 6 caracteres"
            isValid = false
        }

        if (state.password != state.confirmPassword) {
            confirmPassErr = "Las contraseñas no coinciden"
            isValid = false
        }

        _uiState.update {
            it.copy(errores = RegisterErrores(emailError = emailErr, passwordError = passErr, confirmPasswordError = confirmPassErr))
        }
        return isValid
    }

    fun registerWithEmail(context: Context) {
        if (!validarFormulario()) return

        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = RegisterErrores()) }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val firebaseUser = result.user

                    // Enviar correo de verificación por Firebase
                    firebaseUser?.sendEmailVerification()?.addOnCompleteListener {
                        viewModelScope.launch(Dispatchers.IO) {
                            // Guardar en la base de datos local de Room
                            val db = AppDatabase.getDatabase(context)
                            db.userDao().insertUser(
                                UserEntity(
                                    email = email,
                                    passwordHash = password,
                                    provider = "email",
                                    isVerified = false
                                )
                            )

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isSuccessMessageVisible = true,
                                    successMessage = "Registro exitoso. Te hemos enviado un correo de verificación. Confírmalo antes de iniciar sesión."
                                )
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errores = RegisterErrores(authError = exception.message ?: "Error al registrar el usuario")
                        )
                    }
                }
        }
    }

    // --- REGISTRO CON GOOGLE ---
    fun registerWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = RegisterErrores()) }
            val result = authRepository.loginWithGoogle(context = context, webClientId = webClientId)

            result.onSuccess { user ->
                val email = user.email ?: "google_user@domain.com"

                viewModelScope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(context)
                    val existingUser = db.userDao().getUserByEmail(email)

                    // Se inserta con passwordHash vacío si no existe previamente
                    if (existingUser == null) {
                        db.userDao().insertUser(
                            UserEntity(
                                email = email,
                                passwordHash = "",
                                provider = "google",
                                isVerified = true
                            )
                        )
                    }
                }
            }
            handleAuthResult(result)
        }
    }

    fun registerWithApple(activity: Activity, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errores = RegisterErrores()) }

            val provider = OAuthProvider.newBuilder("apple.com")
            provider.scopes = listOf("email", "name")

            auth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener { authResult ->
                    val email = authResult.user?.email
                        ?: "apple_user_${UUID.randomUUID().toString().take(5)}@apple.com"

                    viewModelScope.launch(Dispatchers.IO) {
                        val db = AppDatabase.getDatabase(context)
                        val existingUser = db.userDao().getUserByEmail(email)

                        if (existingUser == null) {
                            db.userDao().insertUser(
                                UserEntity(
                                    email = email,
                                    passwordHash = "",
                                    provider = "apple",
                                    isVerified = true
                                )
                            )
                        }
                    }

                    _uiState.update { it.copy(isLoading = false) }
                    navigateToLogin()
                }
                .addOnFailureListener { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errores = RegisterErrores(authError = exception.message ?: "Error al autenticar con Apple")
                        )
                    }
                }
        }
    }

    private suspend fun handleAuthResult(result: Result<User>) {
        result.onSuccess { user ->
            _uiState.update { it.copy(isLoading = false, currentUser = user) }

            // Dirigir al usuario a la creación de contraseña
            _navigationEvent.emit(NavigationEvent.NavigateTo(Screen.CreatePassword.route))
        }.onFailure { exception ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errores = RegisterErrores(authError = exception.message ?: "Error al registrarse")
                )
            }
        }
    }

    fun navigateToLogin() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateTo(Screen.Login.route))
        }
    }
}