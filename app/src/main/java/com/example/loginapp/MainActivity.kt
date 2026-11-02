package com.example.loginapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loginapp.data.local.AppDatabase
import com.example.loginapp.data.local.UserDao
import com.example.loginapp.navigation.NavigationEvent
import com.example.loginapp.navigation.Screen
import com.example.loginapp.repository.AuthRepository
import com.example.loginapp.ui.screens.*
import com.example.loginapp.ui.utils.rememberWindowSizeClass
import com.example.loginapp.viewmodel.CreatePasswordViewModel
import com.example.loginapp.viewmodel.LoginViewModel
import com.example.loginapp.viewmodel.RegisterViewModel

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        LoginViewModelFactory(AuthRepository(), database.userDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(loginViewModel = loginViewModel)
                }
            }
        }
    }
}

// --- PASO 2: FACTORY PARA PASAR USERDAO A CREATEPASSWORDVIEWMODEL ---
class CreatePasswordViewModelFactory(
    private val authRepository: AuthRepository,
    private val userDao: UserDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatePasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreatePasswordViewModel(authRepository, userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val userDao: UserDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository, userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun AppNavigation(loginViewModel: LoginViewModel) {
    val navController = rememberNavController()
    val windowSizeClass = rememberWindowSizeClass()

    LaunchedEffect(Unit) {
        loginViewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route) {
                        if (event.route == Screen.Login.route) {
                            popUpTo(0)
                        }
                    }
                }
                is NavigationEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(viewModel = loginViewModel, windowSizeClass = windowSizeClass)
        }

        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel()

            LaunchedEffect(Unit) {
                registerViewModel.navigationEvent.collect { event ->
                    when (event) {
                        is NavigationEvent.NavigateTo -> {
                            navController.navigate(event.route) {
                                if (event.route != Screen.Login.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        }
                        is NavigationEvent.NavigateBack -> navController.popBackStack()
                    }
                }
            }

            RegisterScreen(viewModel = registerViewModel, windowSizeClass = windowSizeClass)
        }

        // --- PASO 3: CONFIGURACIÓN DE CREATEPASSWORDSCREEN CON SU VIEWMODEL Y FACTORY ---
        composable(Screen.CreatePassword.route) {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val createPasswordViewModel: CreatePasswordViewModel = viewModel(
                factory = CreatePasswordViewModelFactory(AuthRepository(), database.userDao())
            )

            LaunchedEffect(Unit) {
                createPasswordViewModel.navigationEvent.collect { event ->
                    when (event) {
                        is NavigationEvent.NavigateTo -> {
                            navController.navigate(event.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        is NavigationEvent.NavigateBack -> navController.popBackStack()
                    }
                }
            }

            CreatePasswordScreen(viewModel = createPasswordViewModel)
        }

        composable(Screen.HomeAdmin.route) {
            HomeAdminScreen(viewModel = loginViewModel)
        }
        composable(Screen.HomeSupervisor.route) {
            HomeSupervisorScreen(viewModel = loginViewModel)
        }
        composable(Screen.HomeOperator.route) {
            HomeOperadorScreen(viewModel = loginViewModel)
        }
    }
}