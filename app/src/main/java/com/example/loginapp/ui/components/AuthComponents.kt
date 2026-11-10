package com.example.loginapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.theme.GuardianCyan
import com.example.loginapp.ui.theme.GuardianIndigo
import com.example.loginapp.ui.utils.WindowSizeClass

data class AuthFeature(
    val icon: ImageVector,
    val title: String,
    val description: String
)

@Composable
fun GuardianBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF081226),
                        Color(0xFF12224A),
                        Color(0xFF1E1B4B)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-110).dp, y = (-130).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        )
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 90.dp)
                .clip(CircleShape)
                .background(GuardianCyan.copy(alpha = 0.10f))
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 50.dp)
                .clip(CircleShape)
                .background(GuardianIndigo.copy(alpha = 0.14f))
        )
        content()
    }
}

@Composable
fun GuardianLogo(
    modifier: Modifier = Modifier,
    size: Dp = 60.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 3))
            .background(
                Brush.linearGradient(
                    colors = listOf(GuardianIndigo, GuardianCyan)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Logo Modo Guardián",
            tint = Color.White,
            modifier = Modifier.size(size * 0.52f)
        )
    }
}

@Composable
fun BrandHeader(compact: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        GuardianLogo(size = if (compact) 52.dp else 62.dp)
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = "Modo Guardián",
                color = Color.White,
                fontSize = if (compact) 20.sp else 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Sistema InsertCode",
                color = Color.White.copy(alpha = 0.62f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun AuthShell(
    windowSizeClass: WindowSizeClass,
    eyebrow: String,
    heroTitle: String,
    heroSubtitle: String,
    formTitle: String,
    formSubtitle: String,
    features: List<AuthFeature>,
    form: @Composable ColumnScope.() -> Unit
) {
    GuardianBackground {
        if (windowSizeClass == WindowSizeClass.COMPACT) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BrandHeader(compact = true)
                    Spacer(modifier = Modifier.height(24.dp))
                    AuthCard(modifier = Modifier.widthIn(max = 480.dp)) {
                        FormHeader(
                            eyebrow = eyebrow,
                            title = formTitle,
                            subtitle = formSubtitle
                        )
                        Spacer(modifier = Modifier.height(22.dp))
                        form()
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    BrandHeader(compact = false)
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = heroTitle,
                        color = Color.White,
                        fontSize = 40.sp,
                        lineHeight = 46.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = heroSubtitle,
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    features.forEach { feature ->
                        FeatureRow(feature)
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
                AuthCard(modifier = Modifier.width(440.dp)) {
                    FormHeader(
                        eyebrow = eyebrow,
                        title = formTitle,
                        subtitle = formSubtitle
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    form()
                }
            }
        }
    }
}

@Composable
fun AuthCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 18.dp
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            content = content
        )
    }
}

@Composable
private fun FormHeader(
    eyebrow: String,
    title: String,
    subtitle: String
) {
    Text(
        text = eyebrow,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = subtitle,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 13.sp,
        lineHeight = 19.sp
    )
}

@Composable
private fun FeatureRow(feature: AuthFeature) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color.White.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = feature.title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = feature.description,
                color = Color.White.copy(alpha = 0.62f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    imeAction: ImeAction = ImeAction.Next,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Correo electrónico") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        isError = error != null,
        supportingText = error?.let { message ->
            { Text(message, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    imeAction: ImeAction = ImeAction.Next,
    modifier: Modifier = Modifier
) {
    var visible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        isError = error != null,
        supportingText = error?.let { message ->
            { Text(message, color = MaterialTheme.colorScheme.error) }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val shape = RoundedCornerShape(16.dp)
    val isEnabled = enabled && !loading

    Button(
        onClick = onClick,
        enabled = isEnabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isEnabled) {
                        Brush.horizontalGradient(listOf(GuardianIndigo, Color(0xFF2563EB)))
                    } else {
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    },
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = text,
                    color = if (isEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AuthDivider(text: String = "o continúa con") {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun SocialAuthButtons(
    enabled: Boolean,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SocialButton(
            label = "Google",
            badgeText = "G",
            badgeColor = Color(0xFF4285F4),
            enabled = enabled,
            onClick = onGoogleClick,
            modifier = Modifier.weight(1f)
        )
        SocialButton(
            label = "Apple",
            badgeText = "A",
            badgeColor = Color(0xFF111827),
            enabled = enabled,
            onClick = onAppleClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SocialButton(
    label: String,
    badgeText: String,
    badgeColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(badgeColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = badgeText,
                color = badgeColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AuthErrorBanner(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun AuthSuccessBanner(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun PasswordStrength(password: String) {
    val strength = when {
        password.length >= 10 &&
            password.any { it.isDigit() } &&
            password.any { !it.isLetterOrDigit() } -> 3

        password.length >= 8 -> 2
        password.length >= 6 -> 1
        else -> 0
    }

    val color = when (strength) {
        3 -> Color(0xFF16A34A)
        2 -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }
    val label = when (strength) {
        3 -> "Fuerte"
        2 -> "Media"
        1 -> "Débil"
        else -> "Muy débil"
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (index < strength) color
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Seguridad: $label",
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
