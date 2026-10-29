package com.example.loginapp.repository

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.loginapp.model.User
import com.example.loginapp.model.UserRole
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth = Firebase.auth

    // Método de Login conectado a Firebase Auth
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            // 1. Autenticar en Firebase
            val authResult = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Usuario no encontrado"))

            // 2. Refrescar el estado del usuario desde el servidor de Firebase
            firebaseUser.reload().await()

            // 3. Validar si el correo electrónico fue verificado
            if (!firebaseUser.isEmailVerified) {
                return Result.failure(Exception("Debes verificar tu correo antes de iniciar sesión."))
            }

            // 4. Mapear el usuario a la entidad local de la app
            val user = User(
                email = firebaseUser.email ?: email,
                role = UserRole.OPERADOR, // Rol predeterminado
                name = firebaseUser.displayName ?: "Usuario"
            )

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("Credenciales incorrectas o usuario no registrado."))
        }
    }

    // Registro con Email y envío automático de correo de verificación
    suspend fun registerWithEmail(name: String, email: String, password: String): Result<User> {
        return try {
            val cleanEmail = email.trim().lowercase()

            // 1. Crear el usuario en Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(cleanEmail, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("No se pudo crear el usuario"))

            // 2. Asignar el nombre de usuario en Firebase Profile
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name.trim())
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // 3. Enviar correo de verificación de cuenta
            firebaseUser.sendEmailVerification().await()

            val newUser = User(
                email = cleanEmail,
                role = UserRole.OPERADOR,
                name = name.trim()
            )

            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Error al registrar el usuario."))
        }
    }

    // Login con Google a través de CredentialManager
    suspend fun loginWithGoogle(context: Context, webClientId: String): Result<User> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult.user

            val user = User(
                email = firebaseUser?.email ?: "",
                role = UserRole.OPERADOR,
                name = firebaseUser?.displayName ?: "Usuario Google"
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login con GitHub (OAuth)
    suspend fun loginWithGithub(activity: Activity): Result<User> {
        return try {
            val provider = OAuthProvider.newBuilder("github.com")
            provider.scopes = listOf("user:email")

            val pendingResultTask = firebaseAuth.pendingAuthResult
            val authResult = if (pendingResultTask != null) {
                pendingResultTask.await()
            } else {
                firebaseAuth.startActivityForSignInWithProvider(activity, provider.build()).await()
            }

            val firebaseUser = authResult.user
            val user = User(
                email = firebaseUser?.email ?: "github_user@app.com",
                role = UserRole.OPERADOR,
                name = firebaseUser?.displayName ?: "Usuario GitHub"
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}