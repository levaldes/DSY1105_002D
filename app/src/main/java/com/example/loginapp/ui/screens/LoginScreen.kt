package com.example.loginapp.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
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
import com.example.loginapp.ui.components.EmailTextField
import com.example.loginapp.ui.components.GradientButton
import com.example.loginapp.ui.components.PasswordTextField
import com.example.loginapp.ui.components.SocialAuthButtons
import com.example.loginapp.ui.utils.WindowSizeClass
import com.example.loginapp.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    windowSizeClass: WindowSizeClass
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    AuthShell(
        windowSizeClass = windowSizeClass,
        eyebrow = "BIENVENIDO DE NUEVO",
        heroTitle = "Protege lo que más importa.",
        heroSubtitle = "Gestiona el acceso, supervisa a tu equipo y mantén tu operación segura desde un solo lugar.",
        formTitle = "Iniciar sesión",
        formSubtitle = "Ingresa tus credenciales para continuar.",
        features = listOf(
            AuthFeature(
                icon = Icons.Default.Shield,
                title = "Acceso seguro",
                description = "Autenticación con Firebase y roles definidos."
            ),
            AuthFeature(
                icon = Icons.Default.NotificationsActive,
                title = "Alertas en tiempo real",
                description = "Recibe avisos críticos al instante."
            ),
            AuthFeature(
                icon = Icons.Default.Groups,
                title = "Equipo conectado",
                description = "Administra usuarios y permisos fácilmente."
            )
        )
    ) {
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
            error = uiState.errores.passwordError,
            imeAction = ImeAction.Done
        )

        uiState.errores.authError?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            AuthErrorBanner(message)
        }

        Spacer(modifier = Modifier.height(20.dp))
        GradientButton(
            text = "Iniciar sesión",
            onClick = viewModel::login,
            enabled = uiState.isLoginEnabled,
            loading = uiState.isLoading
        )

        Spacer(modifier = Modifier.height(20.dp))
        AuthDivider()
        Spacer(modifier = Modifier.height(16.dp))
        SocialAuthButtons(
            enabled = !uiState.isLoading,
            onGoogleClick = { viewModel.loginWithGoogle(context) },
            onAppleClick = { activity?.let { viewModel.loginWithApple(it) } }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes cuenta?",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            TextButton(onClick = viewModel::onNavigateToRegister) {
                Text(
                    text = "Regístrate aquí",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
