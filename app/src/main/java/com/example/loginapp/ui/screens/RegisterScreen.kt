package com.example.loginapp.ui.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.utils.WindowSizeClass
import com.example.loginapp.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (windowSizeClass == WindowSizeClass.COMPACT) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RegisterHeaderSection()
                        Spacer(modifier = Modifier.height(20.dp))
                        RegisterForm(
                            viewModel = viewModel,
                            context = context,
                            activity = activity
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .width(720.dp)
                        .padding(24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RegisterHeaderSection()
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        Column(modifier = Modifier.weight(1.3f)) {
                            RegisterForm(
                                viewModel = viewModel,
                                context = context,
                                activity = activity
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RegisterHeaderSection() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Logo Modo Guardián",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Crear Cuenta",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = "Únete a Modo Guardián",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun RegisterForm(
    viewModel: RegisterViewModel,
    context: Context,
    activity: Activity?
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Botón de Apple
        OutlinedButton(
            onClick = {
                activity?.let { currentActivity ->
                    viewModel.registerWithApple(currentActivity, context)
                }
            },
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Continuar con Apple", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de Google
        OutlinedButton(
            onClick = {
                viewModel.registerWithGoogle(context)
            },
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Continuar con Google", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            Text(
                text = " o ",
                color = MaterialTheme.colorScheme.outline,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Correo
        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            isError = uiState.errores.emailError != null,
            supportingText = {
                uiState.errores.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Campo: Contraseña
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            isError = uiState.errores.passwordError != null,
            supportingText = {
                uiState.errores.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Campo: Repetir Contraseña
        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = { Text("Repetir contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            isError = uiState.errores.confirmPasswordError != null,
            supportingText = {
                uiState.errores.confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        // Error general
        uiState.errores.authError?.let { errorMsg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Mensaje de verificación enviada
        if (uiState.isSuccessMessageVisible) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.successMessage ?: "",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Registrarse
        Button(
            onClick = { viewModel.registerWithEmail(context) },
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text("Registrarse", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { viewModel.navigateToLogin() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión", fontSize = 13.sp)
        }
    }
}