package com.example.loginapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.model.UserRole
import com.example.loginapp.ui.components.GuardianLogo
import com.example.loginapp.ui.utils.WindowSizeClass
import com.example.loginapp.ui.utils.rememberWindowSizeClass
import com.example.loginapp.viewmodel.LoginViewModel
import androidx.compose.material.icons.filled.Videocam

private enum class DashboardTab(val label: String, val icon: ImageVector) {
    INICIO("Inicio", Icons.Default.Home),
    CAMARAS("Cámaras", Icons.Default.Videocam),
    ALERTAS("Alertas", Icons.Default.Notifications),
    PERFIL("Perfil", Icons.Default.Person)
}

private enum class AlertSeverity(val label: String, val color: Color) {
    ALTA("Alta", Color(0xFFDC2626)),
    MEDIA("Media", Color(0xFFD97706)),
    BAJA("Baja", Color(0xFF0D9488))
}

private data class StatItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

private data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

private data class AlertItem(
    val title: String,
    val description: String,
    val severity: AlertSeverity,
    val time: String,
    val location: String
)

private data class RoleConfig(
    val title: String,
    val shortTitle: String,
    val greeting: String,
    val accent: Color,
    val stats: List<StatItem>,
    val quickActions: List<QuickAction>,
    val alerts: List<AlertItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDashboardScreen(
    role: UserRole,
    viewModel: LoginViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val windowSizeClass = rememberWindowSizeClass()
    var selectedTab by rememberSaveable { mutableStateOf(DashboardTab.INICIO) }
    val config = remember(role) { roleConfig(role) }

    val fallback = fallbackUser(role)
    val displayName = uiState.currentUser?.name?.takeIf { it.isNotBlank() } ?: fallback.first
    val email = uiState.currentUser?.email?.takeIf { it.isNotBlank() } ?: fallback.second

    if (windowSizeClass == WindowSizeClass.COMPACT) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                DashboardTopBar(
                    config = config,
                    onAlertsClick = { selectedTab = DashboardTab.ALERTAS }
                )
            },
            bottomBar = {
                DashboardBottomBar(
                    selectedTab = selectedTab,
                    accent = config.accent,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { padding ->
            DashboardContent(
                modifier = Modifier.padding(padding),
                tab = selectedTab,
                config = config,
                displayName = displayName,
                email = email,
                onLogout = viewModel::logout
            )
        }
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface,
                header = {
                    Box(modifier = Modifier.padding(vertical = 20.dp)) {
                        GuardianLogo(size = 44.dp)
                    }
                }
            ) {
                Spacer(modifier = Modifier.weight(1f))
                DashboardTab.entries.forEach { tab ->
                    NavigationRailItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = config.accent,
                            selectedTextColor = config.accent,
                            indicatorColor = config.accent.copy(alpha = 0.12f)
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    DashboardTopBar(
                        config = config,
                        onAlertsClick = { selectedTab = DashboardTab.ALERTAS }
                    )
                }
            ) { padding ->
                DashboardContent(
                    modifier = Modifier.padding(padding),
                    tab = selectedTab,
                    config = config,
                    displayName = displayName,
                    email = email,
                    onLogout = viewModel::logout
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    config: RoleConfig,
    onAlertsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GuardianLogo(size = 36.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Modo Guardián",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = config.shortTitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            Surface(
                color = config.accent.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(config.accent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = config.shortTitle,
                        color = config.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onAlertsClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Ver alertas"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun DashboardBottomBar(
    selectedTab: DashboardTab,
    accent: Color,
    onTabSelected: (DashboardTab) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        DashboardTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = accent,
                    selectedTextColor = accent,
                    indicatorColor = accent.copy(alpha = 0.12f)
                )
            )
        }
    }
}

@Composable
private fun DashboardContent(
    modifier: Modifier,
    tab: DashboardTab,
    config: RoleConfig,
    displayName: String,
    email: String,
    onLogout: () -> Unit
) {
    when (tab) {
        DashboardTab.INICIO -> InicioTab(
            modifier = modifier,
            config = config,
            displayName = displayName,
            email = email
        )

        DashboardTab.CAMARAS -> CamarasTab(
            modifier = modifier,
            accent = config.accent
        )

        DashboardTab.ALERTAS -> AlertasTab(
            modifier = modifier,
            config = config
        )

        DashboardTab.PERFIL -> PerfilTab(
            modifier = modifier,
            config = config,
            displayName = displayName,
            email = email,
            onLogout = onLogout
        )
    }
}

@Composable
private fun InicioTab(
    modifier: Modifier,
    config: RoleConfig,
    displayName: String,
    email: String
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            HeroCard(
                config = config,
                displayName = displayName,
                email = email
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                config.stats.forEach { stat ->
                    StatCard(
                        stat = stat,
                        accent = config.accent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            SectionHeader(
                title = "Acciones rápidas",
                subtitle = "Todo lo que necesitas, a un toque"
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                config.quickActions.chunked(2).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEach { action ->
                            QuickActionCard(
                                action = action,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Actividad reciente",
                subtitle = "Últimos movimientos de tu cuenta"
            )
        }

        items(config.alerts.take(3)) { alert ->
            AlertCard(alert = alert)
        }
    }
}

@Composable
private fun HeroCard(
    config: RoleConfig,
    displayName: String,
    email: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(config.accent, config.accent.copy(alpha = 0.72f))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = config.greeting,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayName,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.82f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = email,
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = config.shortTitle,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    stat: StatItem,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = stat.color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stat.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stat.label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(92.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(action.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = action.color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = action.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AlertasTab(
    modifier: Modifier,
    config: RoleConfig
) {
    var filter by rememberSaveable { mutableStateOf<AlertSeverity?>(null) }
    val filteredAlerts = config.alerts.filter { alert ->
        filter == null || alert.severity == filter
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Centro de alertas",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Revisa y prioriza los eventos de tu operación.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == null,
                    onClick = { filter = null },
                    label = { Text("Todas") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = config.accent.copy(alpha = 0.12f),
                        selectedLabelColor = config.accent
                    )
                )
                AlertSeverity.entries.forEach { severity ->
                    FilterChip(
                        selected = filter == severity,
                        onClick = { filter = severity },
                        label = { Text(severity.label) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(severity.color)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = severity.color.copy(alpha = 0.12f),
                            selectedLabelColor = severity.color,
                            selectedLeadingIconColor = severity.color
                        )
                    )
                }
            }
        }

        if (filteredAlerts.isEmpty()) {
            item {
                EmptyAlerts()
            }
        } else {
            items(filteredAlerts) { alert ->
                AlertCard(alert = alert)
            }
        }
    }
}

@Composable
private fun EmptyAlerts() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No hay alertas en esta categoría",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Todo está bajo control.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AlertCard(alert: AlertItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(alert.severity.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = alert.severity.color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = alert.severity.label,
                        color = alert.severity.color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = alert.time,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = alert.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = alert.location,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PerfilTab(
    modifier: Modifier,
    config: RoleConfig,
    displayName: String,
    email: String,
    onLogout: () -> Unit
) {
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }
    var weeklySummaryEnabled by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(config.accent, config.accent.copy(alpha = 0.7f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials(displayName),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = email,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = config.accent.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = config.shortTitle,
                            color = config.accent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Información de la cuenta")
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column {
                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Correo",
                        value = email
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    ProfileInfoRow(
                        icon = Icons.Default.Badge,
                        label = "Rol",
                        value = config.shortTitle
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    ProfileInfoRow(
                        icon = Icons.Default.CheckCircle,
                        label = "Estado",
                        value = "Cuenta verificada"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    ProfileInfoRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Miembro desde",
                        value = "Noviembre 2026"
                    )
                }
            }
        }

        item {
            SectionHeader(title = "Preferencias")
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column {
                    PreferenceRow(
                        title = "Notificaciones push",
                        subtitle = "Recibe alertas críticas al instante",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    PreferenceRow(
                        title = "Resumen semanal",
                        subtitle = "Recibe un correo con el resumen de actividad",
                        checked = weeklySummaryEnabled,
                        onCheckedChange = { weeklySummaryEnabled = it }
                    )
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar sesión",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PreferenceRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun initials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "MG"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "${parts[0].first()}${parts[1].first()}".uppercase()
    }
}

private fun fallbackUser(role: UserRole): Pair<String, String> = when (role) {
    UserRole.ADMIN -> "Administrador" to "admin@guardian.test"
    UserRole.SUPERVISOR -> "Supervisor" to "supervisor@guardian.test"
    UserRole.OPERADOR -> "Operador" to "operador@guardian.test"
}

private fun roleConfig(role: UserRole): RoleConfig = when (role) {
    UserRole.ADMIN -> RoleConfig(
        title = "Administración",
        shortTitle = "Administrador",
        greeting = "Bienvenido de nuevo",
        accent = Color(0xFF4F46E5),
        stats = listOf(
            StatItem("Usuarios activos", "128", Icons.Default.Groups, Color(0xFF4F46E5)),
            StatItem("Alertas hoy", "12", Icons.Default.NotificationsActive, Color(0xFFDC2626)),
            StatItem("Reportes", "34", Icons.Default.Assessment, Color(0xFF0D9488))
        ),
        quickActions = listOf(
            QuickAction("Gestionar usuarios", Icons.Default.ManageAccounts, Color(0xFF4F46E5)),
            QuickAction("Ver reportes", Icons.Default.Assessment, Color(0xFF0D9488)),
            QuickAction("Configurar alertas", Icons.Default.Settings, Color(0xFFD97706)),
            QuickAction("Auditoría", Icons.Default.Security, Color(0xFF7C3AED))
        ),
        alerts = listOf(
            AlertItem(
                title = "Acceso no autorizado",
                description = "Se detectó un intento de acceso desde un dispositivo desconocido.",
                severity = AlertSeverity.ALTA,
                time = "Hace 8 min",
                location = "Sede central"
            ),
            AlertItem(
                title = "Nuevo usuario registrado",
                description = "María López se unió al equipo como operadora.",
                severity = AlertSeverity.MEDIA,
                time = "Hace 32 min",
                location = "Plataforma"
            ),
            AlertItem(
                title = "Respaldo completado",
                description = "La copia de seguridad diaria se completó correctamente.",
                severity = AlertSeverity.BAJA,
                time = "Hace 1 h",
                location = "Servidor principal"
            )
        )
    )

    UserRole.SUPERVISOR -> RoleConfig(
        title = "Supervisión",
        shortTitle = "Supervisor",
        greeting = "Buen turno",
        accent = Color(0xFF0D9488),
        stats = listOf(
            StatItem("Operadores", "24", Icons.Default.Groups, Color(0xFF0D9488)),
            StatItem("Alertas activas", "8", Icons.Default.NotificationsActive, Color(0xFFD97706)),
            StatItem("Turnos cubiertos", "96%", Icons.Default.EventAvailable, Color(0xFF4F46E5))
        ),
        quickActions = listOf(
            QuickAction("Monitorear turnos", Icons.Default.Visibility, Color(0xFF0D9488)),
            QuickAction("Asignar tareas", Icons.AutoMirrored.Filled.Assignment, Color(0xFF4F46E5)),
            QuickAction("Revisar alertas", Icons.Default.NotificationsActive, Color(0xFFD97706)),
            QuickAction("Reporte diario", Icons.Default.Description, Color(0xFF7C3AED))
        ),
        alerts = listOf(
            AlertItem(
                title = "Retraso en turno",
                description = "El operador del turno noche ingresó 15 minutos tarde.",
                severity = AlertSeverity.MEDIA,
                time = "Hace 12 min",
                location = "Acceso principal"
            ),
            AlertItem(
                title = "Operador sin conexión",
                description = "No se detecta actividad del operador en la ronda asignada.",
                severity = AlertSeverity.ALTA,
                time = "Hace 25 min",
                location = "Zona norte"
            ),
            AlertItem(
                title = "Checklist completado",
                description = "La ronda de supervisión finalizó sin novedades.",
                severity = AlertSeverity.BAJA,
                time = "Hace 50 min",
                location = "Zona sur"
            )
        )
    )

    UserRole.OPERADOR -> RoleConfig(
        title = "Operación",
        shortTitle = "Operador",
        greeting = "Hola de nuevo",
        accent = Color(0xFFD97706),
        stats = listOf(
            StatItem("Alertas asignadas", "5", Icons.Default.NotificationsActive, Color(0xFFD97706)),
            StatItem("Tareas pendientes", "3", Icons.Default.Checklist, Color(0xFF4F46E5)),
            StatItem("Estado del turno", "Activo", Icons.Default.Work, Color(0xFF0D9488))
        ),
        quickActions = listOf(
            QuickAction("Reportar alerta", Icons.Default.AddAlert, Color(0xFFDC2626)),
            QuickAction("Mis tareas", Icons.Default.Checklist, Color(0xFF4F46E5)),
            QuickAction("Historial", Icons.Default.History, Color(0xFF0D9488)),
            QuickAction("Contactar supervisor", Icons.Default.SupportAgent, Color(0xFFD97706))
        ),
        alerts = listOf(
            AlertItem(
                title = "Puerta norte abierta",
                description = "Se detectó la puerta norte abierta fuera del horario permitido.",
                severity = AlertSeverity.ALTA,
                time = "Hace 5 min",
                location = "Zona norte"
            ),
            AlertItem(
                title = "Ronda de vigilancia",
                description = "Recuerda completar la ronda de la zona sur.",
                severity = AlertSeverity.MEDIA,
                time = "Hace 20 min",
                location = "Zona sur"
            ),
            AlertItem(
                title = "Equipo revisado",
                description = "El checklist de equipos se completó correctamente.",
                severity = AlertSeverity.BAJA,
                time = "Hace 1 h",
                location = "Bodega"
            )
        )
    )
}
