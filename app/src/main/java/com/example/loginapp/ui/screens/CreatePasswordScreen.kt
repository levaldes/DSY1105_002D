package com.example.loginapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.components.AuthCard
import com.example.loginapp.ui.components.AuthErrorBanner
import com.example.loginapp.ui.components.BrandHeader
import com.example.loginapp.ui.components.GradientButton
import com.example.loginapp.ui.components.GuardianBackground
import com.example.loginapp.ui.components.PasswordTextField
import com.example.loginapp.ui.components.PasswordStrength
import com.example.loginapp.viewmodel.CreatePasswordViewModel

@Composable
fun CreatePasswordScreen(
    viewModel: CreatePasswordViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordInput by rememberSaveable { mutableStateOf("") }
    var confirmPasswordInput by rememberSaveable { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    GuardianBackground {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrandHeader(compact = true)
                Spacer(modifier = Modifier.height(24.dp))

                AuthCard(modifier = Modifier.widthIn(max = 480.dp)) {
                    Text(
                        text = "CONFIGURA TU ACCESO",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Crea tu contraseña",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 26.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Asigna una contraseña segura para ingresar con tu correo y esta clave en el futuro.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    PasswordTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            localError = null
                        },
                        label = "Nueva contraseña",
                        error = uiState.errores.passwordError
                    )

                    if (passwordInput.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordStrength(password = passwordInput)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    PasswordTextField(
                        value = confirmPasswordInput,
                        onValueChange = {
                            confirmPasswordInput = it
                            localError = null
                        },
                        label = "Confirmar contraseña",
                        error = localError,
                        imeAction = ImeAction.Done
                    )

                    val errorMessage = localError
                        ?: uiState.errores.passwordError
                        ?: uiState.errores.authError

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AuthErrorBanner(message = errorMessage)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    GradientButton(
                        text = "Guardar y continuar",
                        onClick = {
                            when {
                                passwordInput != confirmPasswordInput -> {
                                    localError = "Las contraseñas no coinciden"
                                }

                                passwordInput.length < 6 -> {
                                    localError = "La contraseña debe tener al menos 6 caracteres"
                                }

                                else -> {
                                    viewModel.saveGoogleUserPassword(passwordInput)
                                }
                            }
                        },
                        enabled = passwordInput.isNotEmpty() && !uiState.isLoading,
                        loading = uiState.isLoading
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Tu contraseña se vinculará de forma segura a tu cuenta de Google.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
