package com.example.ttokyutne.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ttokyutne.ui.theme.TtoKyutNeTheme

private val Canvas = Color(0xFFF6F8FA)
private val Ink = Color(0xFF111827)
private val Muted = Color(0xFF667085)
private val Line = Color(0xFFE1E6EA)
private val Forest = Color(0xFF0F6B5F)
private val ForestDark = Color(0xFF0B3733)
private val Warm = Color(0xFFFFECE3)
private val WarmInk = Color(0xFFB45731)
private val Danger = Color(0xFFB42318)

private val intervalOptions = listOf(
    60L to "1분",
    300L to "5분",
    600L to "10분",
    1800L to "30분"
)

@Composable
fun SettingsScreen(
    settings: SettingsUiState,
    notificationPermissionGranted: Boolean,
    onBack: () -> Unit,
    onNotificationEnabledChange: (Boolean) -> Unit,
    onMinIntervalSecondsChange: (Long) -> Unit,
    onDeleteAllData: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    BackHandler(onBack = onBack)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Canvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header(onBack = onBack)

            if (!notificationPermissionGranted) {
                NotificationPermissionCard(onOpenNotificationSettings = onOpenNotificationSettings)
            }

            NotificationToggleCard(
                enabled = settings.notificationEnabled,
                onEnabledChange = onNotificationEnabledChange
            )

            IntervalSelectorCard(
                selectedSeconds = settings.minIntervalSeconds,
                onMinIntervalSecondsChange = onMinIntervalSecondsChange
            )

            DataCard(
                onDeleteClick = { showDeleteDialog = true },
                onOpenNotificationSettings = onOpenNotificationSettings
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "전체 데이터를 삭제할까요?") },
            text = {
                Text(text = "screen_on_event와 phrase_history 기록이 모두 삭제됩니다. 이 작업은 되돌릴 수 없어요.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAllData()
                        showDeleteDialog = false
                    }
                ) {
                    Text(text = "삭제", color = Danger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "취소")
                }
            }
        )
    }
}

@Composable
private fun Header(onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Button(
            onClick = onBack,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ForestDark,
                contentColor = Color.White
            )
        ) {
            Text(text = "홈으로", fontWeight = FontWeight.Bold)
        }

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = "설정",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = "알림 기준과 저장된 기록을 관리해요.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

@Composable
private fun NotificationPermissionCard(onOpenNotificationSettings: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Warm,
        border = BorderStroke(1.dp, Color(0xFFFFD6C3))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "알림 권한이 꺼져 있어요",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = "감성 문구 알림을 켜도 Android 알림 권한이 꺼져 있으면 또켰네 알림이 표시되지 않아요.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
            Button(
                onClick = onOpenNotificationSettings,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarmInk,
                    contentColor = Color.White
                )
            ) {
                Text(text = "알림 권한 설정 열기", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NotificationToggleCard(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Line)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "감성 문구 알림",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = "꺼도 화면 켜짐 기록은 계속 저장됩니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange
            )
        }
    }
}

@Composable
private fun IntervalSelectorCard(
    selectedSeconds: Long,
    onMinIntervalSecondsChange: (Long) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = "최소 알림 간격",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = "선택한 시간 이내에 다시 켰을 때만 알림을 표시해요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                intervalOptions.forEach { (seconds, label) ->
                    val selected = selectedSeconds == seconds
                    val colors = if (selected) {
                        ButtonDefaults.buttonColors(
                            containerColor = Forest,
                            contentColor = Color.White
                        )
                    } else {
                        ButtonDefaults.outlinedButtonColors(contentColor = Forest)
                    }

                    if (selected) {
                        Button(
                            onClick = { onMinIntervalSecondsChange(seconds) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = colors,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(text = label, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onMinIntervalSecondsChange(seconds) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = colors,
                            border = BorderStroke(1.dp, Color(0xFFB8C9C4)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(text = label, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DataCard(
    onDeleteClick: () -> Unit,
    onOpenNotificationSettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "데이터와 권한",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Button(
                onClick = onOpenNotificationSettings,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestDark,
                    contentColor = Color.White
                )
            ) {
                Text(text = "알림 권한 설정 열기", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFF0B8B2)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger)
            ) {
                Text(text = "전체 데이터 삭제", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    TtoKyutNeTheme {
        SettingsScreen(
            settings = SettingsUiState(),
            notificationPermissionGranted = false,
            onBack = {},
            onNotificationEnabledChange = {},
            onMinIntervalSecondsChange = {},
            onDeleteAllData = {},
            onOpenNotificationSettings = {}
        )
    }
}
