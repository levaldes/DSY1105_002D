package com.example.loginapp.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.utils.WindowSizeClass
import com.example.loginapp.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    windowSizeClass: WindowSizeClass
) {
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
                        HeaderSection()
                        Spacer(modifier = Modifier.height(24.dp))
                        LoginForm(viewModel = viewModel)
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .width(680.dp)
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
                            HeaderSection()
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        Column(modifier = Modifier.weight(1.2f)) {
                            LoginForm(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
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
        text = "Modo Guardián",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = "Sistema InsertCode",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun LoginForm(viewModel: LoginViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        uiState.errores.authError?.let { errorMsg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón único para Iniciar Sesión con Correo y Contraseña
        Button(
            onClick = { viewModel.login() },
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { viewModel.onNavigateToRegister() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿No tienes cuenta? Regístrate aquí", fontSize = 13.sp)
        }
    }
}