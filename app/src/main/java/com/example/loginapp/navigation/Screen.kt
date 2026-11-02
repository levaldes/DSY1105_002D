package com.example.loginapp.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object CreatePassword : Screen("create_password") // Ruta añadida para flujo Google + Password
    data object HomeAdmin : Screen("home_admin")
    data object HomeSupervisor : Screen("home_supervisor")
    data object HomeOperator : Screen("home_operador")
}

sealed class NavigationEvent {
    data class NavigateTo(val route: String) : NavigationEvent()
    data object NavigateBack : NavigationEvent()
}