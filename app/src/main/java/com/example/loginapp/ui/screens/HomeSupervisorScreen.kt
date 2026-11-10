package com.example.loginapp.ui.screens

import androidx.compose.runtime.Composable
import com.example.loginapp.model.UserRole
import com.example.loginapp.viewmodel.LoginViewModel

@Composable
fun HomeSupervisorScreen(viewModel: LoginViewModel) {
    RoleDashboardScreen(
        role = UserRole.SUPERVISOR,
        viewModel = viewModel
    )
}
