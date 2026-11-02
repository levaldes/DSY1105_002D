package com.example.loginapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.loginapp.viewmodel.CreatePasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePasswordScreen(
    viewModel: CreatePasswordViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Contraseña") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Asigna una contraseña a tu cuenta",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Para poder ingresar directamente con tu correo y esta contraseña en el futuro, ingresa una clave de al menos 6 caracteres.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Contraseña
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        localError = null
                    },
                    label = { Text("Nueva Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    isError = uiState.errores.passwordError != null || localError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo Confirmar Contraseña
                OutlinedTextField(
                    value = confirmPasswordInput,
                    onValueChange = {
                        confirmPasswordInput = it
                        localError = null
                    },
                    label = { Text("Confirmar Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    isError = localError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                // Mostrar errores de validación o autenticación
                val errorMessage = localError
                    ?: uiState.errores.passwordError
                    ?: uiState.errores.authError

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Guardar
                Button(
                    onClick = {
                        if (passwordInput != confirmPasswordInput) {
                            localError = "Las contraseñas no coinciden"
                        } else if (passwordInput.length < 6) {
                            localError = "La contraseña debe tener al menos 6 caracteres"
                        } else {
                            viewModel.saveGoogleUserPassword(passwordInput)
                        }
                    },
                    enabled = !uiState.isLoading && passwordInput.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Guardar y Continuar")
                    }
                }
            }
        }
    }
}