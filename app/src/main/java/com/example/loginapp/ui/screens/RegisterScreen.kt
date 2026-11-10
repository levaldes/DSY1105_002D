package com.example.loginapp.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.components.AuthDivider
import com.example.loginapp.ui.components.AuthErrorBanner
import com.example.loginapp.ui.components.AuthFeature
import com.example.loginapp.ui.components.AuthShell
import com.example.loginapp.ui.components.AuthSuccessBanner
import com.example.loginapp.ui.components.EmailTextField
import com.example.loginapp.ui.components.GradientButton
import com.example.loginapp.ui.components.PasswordStrength
import com.example.loginapp.ui.components.PasswordTextField
import com.example.loginapp.ui.components.SocialAuthButtons
import com.example.loginapp.ui.utils.WindowSizeClass
import com.example.loginapp.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    windowSizeClass: WindowSizeClass
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    AuthShell(
        windowSizeClass = windowSizeClass,
        eyebrow = "NUEVO USUARIO",
        heroTitle = "Únete a Modo Guardián.",
        heroSubtitle = "Crea tu cuenta y empieza a proteger tu entorno con una experiencia simple y segura.",
        formTitle = "Crear cuenta",
        formSubtitle = "Completa tus datos para comenzar.",
        features = listOf(
            AuthFeature(
                icon = Icons.Default.PersonAdd,
                title = "Registro rápido",
                description = "Crea tu cuenta en menos de un minuto."
            ),
            AuthFeature(
                icon = Icons.Default.VerifiedUser,
                title = "Cuenta verificada",
                description = "Protegemos tu identidad con verificación por correo."
            ),
            AuthFeature(
                icon = Icons.Default.Devices,
                title = "Acceso multiplataforma",
                description = "Disponible en todos tus dispositivos."
            )
        )
    ) {
        SocialAuthButtons(
            enabled = !uiState.isLoading,
            onGoogleClick = { viewModel.registerWithGoogle(context) },
            onAppleClick = {
                activity?.let { viewModel.registerWithApple(it, context) }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        AuthDivider()
        Spacer(modifier = Modifier.height(16.dp))

        EmailTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            error = uiState.errores.emailError
        )
        Spacer(modifier = Modifier.height(10.dp))
        PasswordTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Contraseña",
            error = uiState.errores.passwordError
        )

        if (uiState.password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            PasswordStrength(password = uiState.password)
        }

        Spacer(modifier = Modifier.height(10.dp))
        PasswordTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "Repetir contraseña",
            error = uiState.errores.confirmPasswordError,
            imeAction = ImeAction.Done
        )

        uiState.errores.authError?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            AuthErrorBanner(message)
        }

        if (uiState.isSuccessMessageVisible) {
            Spacer(modifier = Modifier.height(12.dp))
            AuthSuccessBanner(
                message = uiState.successMessage ?: "Registro exitoso. Revisa tu correo."
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        GradientButton(
            text = "Crear cuenta",
            onClick = { viewModel.registerWithEmail(context) },
            enabled = !uiState.isLoading,
            loading = uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes cuenta?",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            TextButton(onClick = viewModel::navigateToLogin) {
                Text(
                    text = "Inicia sesión",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
