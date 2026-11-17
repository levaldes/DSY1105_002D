package com.example.loginapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class CameraFeed(
    val id: String,
    val name: String,
    val code: String,
    val resolution: String,
    val signal: String,
    val isRecording: Boolean
)

@Composable
fun CamarasTab(
    modifier: Modifier = Modifier,
    accent: Color
) {
    val cameras = remember {
        listOf(
            CameraFeed("CAM-01", "Entrada Principal", "GUARDIAN CAM-01", "1080p • 30FPS", "Excelente", true),
            CameraFeed("CAM-02", "Estacionamiento Norte", "GUARDIAN CAM-02", "2K • 25FPS", "Buena", true),
            CameraFeed("CAM-03", "Pasillo Bodega B", "GUARDIAN CAM-03", "1080p • 30FPS", "Estable", false)
        )
    }

    var selectedCameraIndex by rememberSaveable { mutableIntStateOf(0) }
    var isRecording by rememberSaveable { mutableStateOf(true) }
    var isMicMuted by rememberSaveable { mutableStateOf(true) }
    var isLightOn by rememberSaveable { mutableStateOf(false) }

    val currentCamera = cameras[selectedCameraIndex]

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Encabezado + Selector rápido de cámaras
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Monitoreo en Vivo",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Circuito cerrado de vigilancia",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Badge EN VIVO
                    Surface(
                        color = Color(0xFFDC2626).copy(alpha = 0.14f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDC2626))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EN VIVO",
                                color = Color(0xFFDC2626),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Chips para alternar entre cámaras
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cameras.forEachIndexed { index, cam ->
                        FilterChip(
                            selected = selectedCameraIndex == index,
                            onClick = { selectedCameraIndex = index },
                            label = { Text("${cam.id} · ${cam.name}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = accent.copy(alpha = 0.15f),
                                selectedLabelColor = accent
                            )
                        )
                    }
                }
            }
        }

        // Visor nocturno
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1713))
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF1F382C),
                                        Color(0xFF0B130F)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        // Efecto visual de escaneo y puertapasillo
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val step = 10.dp.toPx()
                            for (y in 0..(size.height / step).toInt()) {
                                drawLine(
                                    color = Color(0xFF34D399).copy(alpha = 0.05f),
                                    start = Offset(0f, y * step),
                                    end = Offset(size.width, y * step),
                                    strokeWidth = 1f
                                )
                            }

                            // Marco central puerta acceso
                            val doorW = size.width * 0.34f
                            val doorH = size.height * 0.62f
                            val left = (size.width - doorW) / 2f
                            val top = (size.height - doorH) / 2f

                            drawRect(
                                color = Color(0xFF34D399).copy(alpha = 0.12f),
                                topLeft = Offset(left, top),
                                size = androidx.compose.ui.geometry.Size(doorW, doorH)
                            )

                            // esquinas de enfoque de visor táctico
                            val corner = 20.dp.toPx()
                            val stroke = 2.dp.toPx()
                            val bracketColor = Color(0xFF6EE7B7).copy(alpha = 0.55f)

                            // Superior izquierda
                            drawLine(bracketColor, Offset(0f, 0f), Offset(corner, 0f), stroke)
                            drawLine(bracketColor, Offset(0f, 0f), Offset(0f, corner), stroke)
                            // Superior derecha
                            drawLine(bracketColor, Offset(size.width - corner, 0f), Offset(size.width, 0f), stroke)
                            drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width, corner), stroke)
                            // Inferior izquierda
                            drawLine(bracketColor, Offset(0f, size.height - corner), Offset(0f, size.height), stroke)
                            drawLine(bracketColor, Offset(0f, size.height), Offset(corner, size.height), stroke)
                            // Inferior derecha
                            drawLine(bracketColor, Offset(size.width - corner, size.height), Offset(size.width, size.height), stroke)
                            drawLine(bracketColor, Offset(size.width, size.height - corner), Offset(size.width, size.height), stroke)
                        }

                        // data superior izquierda
                        Column(modifier = Modifier.align(Alignment.TopStart)) {
                            Text(
                                text = "17-11-2026  12:28:40",
                                color = Color(0xFF6EE7B7),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = currentCamera.code,
                                color = Color(0xFF6EE7B7).copy(alpha = 0.75f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Etiqueta IR AUTO superior derecha
                        Surface(
                            modifier = Modifier.align(Alignment.TopEnd),
                            color = Color.Black.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "IR • AUTO",
                                color = Color(0xFFA7F3D0),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // indicador inferior de codec y estado
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isRecording) Color(0xFFEF4444) else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isRecording) "REC  00:14:37" else "PAUSADO",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = "H.265 • ${currentCamera.resolution}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // botonera de controles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CameraControlButton(
                    icon = Icons.Default.PhotoCamera,
                    label = "CAPTURA",
                    isActive = false,
                    activeColor = accent,
                    onClick = { }
                )
                CameraControlButton(
                    icon = if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                    label = if (isRecording) "PARAR" else "GRABAR",
                    isActive = isRecording,
                    activeColor = Color(0xFFEF4444),
                    onClick = { isRecording = !isRecording }
                )
                CameraControlButton(
                    icon = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = "AUDIO",
                    isActive = !isMicMuted,
                    activeColor = accent,
                    onClick = { isMicMuted = !isMicMuted }
                )
                CameraControlButton(
                    icon = if (isLightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                    label = "LUZ",
                    isActive = isLightOn,
                    activeColor = Color(0xFFF59E0B),
                    onClick = { isLightOn = !isLightOn }
                )
                CameraControlButton(
                    icon = Icons.Default.Fullscreen,
                    label = "EXPANDIR",
                    isActive = false,
                    activeColor = accent,
                    onClick = { }
                )
            }
        }

        // tarjeta de diagnóstico del cel
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ESTADO DEL DISPOSITIVO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DeviceMetricBox(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Sensors,
                            iconColor = Color(0xFF10B981),
                            title = "ESTADO",
                            value = "En línea"
                        )
                        DeviceMetricBox(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Wifi,
                            iconColor = Color(0xFF3B82F6),
                            title = "SEÑAL",
                            value = currentCamera.signal
                        )
                        DeviceMetricBox(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.CloudDone,
                            iconColor = accent,
                            title = "GRABACIÓN",
                            value = "Nube + Local"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraControlButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) activeColor
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DeviceMetricBox(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}