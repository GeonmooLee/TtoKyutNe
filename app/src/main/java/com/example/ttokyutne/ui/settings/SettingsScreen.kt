package com.example.ttokyutne.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import com.example.ttokyutne.settings.RecheckAlertMode
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
    onRecheckAlertModeChange: (RecheckAlertMode) -> Unit,
    onMinIntervalSecondsChange: (Long) -> Unit,
    onDeleteAllData: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenRecheckAlertChannelSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showVibrationGuideDialog by remember { mutableStateOf(false) }
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

            AppExplanationCard()

            RecheckAlertModeCard(
                selectedMode = settings.recheckAlertMode,
                onModeChange = onRecheckAlertModeChange
            )

            IntervalSelectorCard(
                selectedSeconds = settings.minIntervalSeconds,
                onMinIntervalSecondsChange = onMinIntervalSecondsChange
            )

            NotificationPermissionCard(
                notificationPermissionGranted = notificationPermissionGranted,
                onOpenNotificationSettings = onOpenNotificationSettings
            )

            VibrationGuideCard(onGuideClick = { showVibrationGuideDialog = true })

            DataManagementCard(onDeleteClick = { showDeleteDialog = true })

            Spacer(modifier = Modifier.height(4.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "전체 데이터를 삭제할까요?") },
            text = {
                Text(text = "화면 켜짐 기록과 문구 표시 기록이 모두 삭제됩니다. 설정값은 유지되고, 이 작업은 되돌릴 수 없어요.")
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

    if (showVibrationGuideDialog) {
        AlertDialog(
            onDismissRequest = { showVibrationGuideDialog = false },
            title = { Text(text = "알림 진동만 끄는 방법") },
            text = {
                Text(
                    text = "Android 설정에서 또켰네 알림 채널의 진동을 직접 꺼주세요.\n\n" +
                        "1. 시스템 설정 화면이 열리면 '또켰네 알림'을 확인해요.\n" +
                        "2. '진동' 또는 '소리 및 진동' 항목을 꺼요.\n" +
                        "3. 알림 자체는 켜둬야 화면을 켰을 때 문구가 보여요."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showVibrationGuideDialog = false
                        onOpenRecheckAlertChannelSettings()
                    }
                ) {
                    Text(text = "채널 설정 열기", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showVibrationGuideDialog = false }) {
                    Text(text = "닫기")
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
private fun AppExplanationCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "앱 설명",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = "또켰네는 로그인 없이, 서버 없이, 기록을 기기 안에만 저장합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

@Composable
private fun NotificationPermissionCard(
    notificationPermissionGranted: Boolean,
    onOpenNotificationSettings: () -> Unit
) {
    val warning = !notificationPermissionGranted

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = if (warning) Warm else Color.White,
        border = BorderStroke(1.dp, if (warning) Color(0xFFFFD6C3) else Line)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "알림 권한",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = if (warning) {
                    "Android 13 이상에서 알림 권한이 꺼져 있으면 재확인 알림이 표시되지 않아요."
                } else {
                    "알림 권한이 허용되어 있으면 선택한 방식과 조건에 맞춰 알림을 받을 수 있어요."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
            Button(
                onClick = onOpenNotificationSettings,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (warning) WarmInk else ForestDark,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (warning) "알림 권한 설정 열기" else "알림 설정 열기",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RecheckAlertModeCard(
    selectedMode: RecheckAlertMode,
    onModeChange: (RecheckAlertMode) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = "재확인 알림 방식",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = "화면 켜짐 기록은 어떤 모드에서도 계속 저장됩니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }
            AlertModeOptionRow(
                title = "감성 문구 포함",
                description = "짧은 공감 문구와 재확인 간격을 함께 보여줘요.",
                selected = selectedMode == RecheckAlertMode.WithPhrase,
                onSelected = { onModeChange(RecheckAlertMode.WithPhrase) }
            )
            AlertModeOptionRow(
                title = "감성 문구 없이 알림",
                description = "재확인 간격과 오늘 화면 켠 횟수만 보여줘요.",
                selected = selectedMode == RecheckAlertMode.Simple,
                onSelected = { onModeChange(RecheckAlertMode.Simple) }
            )
            AlertModeOptionRow(
                title = "알림 끄기",
                description = "기록은 저장하지만 재확인 알림은 보내지 않아요.",
                selected = selectedMode == RecheckAlertMode.Off,
                onSelected = { onModeChange(RecheckAlertMode.Off) }
            )
        }
    }
}

@Composable
private fun AlertModeOptionRow(
    title: String,
    description: String,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Color(0xFFE2F2EC) else Color.White,
        border = BorderStroke(1.dp, if (selected) Color(0xFFC8E5DC) else Line)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }
            if (selected) {
                Text(
                    text = "선택됨",
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Forest
                )
            }
        }
    }
}

@Composable
private fun VibrationGuideCard(onGuideClick: () -> Unit) {
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
                    text = "알림 진동 끄기",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = "Android 알림 채널에서 진동만 끄는 방법을 안내해요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }
            Button(
                onClick = onGuideClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Forest,
                    contentColor = Color.White
                )
            ) {
                Text(text = "끄는 방법", fontWeight = FontWeight.Bold)
            }
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
                    text = "알림 조건",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = "몇 분 이내에 다시 켰을 때 알림을 받을까요?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Ink
                )
                Text(
                    text = "예를 들어 10분을 선택하면, 직전 확인 후 10분 이내에 다시 켰을 때만 재확인 알림이 표시됩니다.",
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
private fun DataManagementCard(onDeleteClick: () -> Unit) {
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
                text = "데이터 관리",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = "저장된 화면 켜짐 기록과 문구 표시 기록을 삭제할 수 있어요.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
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
            onRecheckAlertModeChange = {},
            onMinIntervalSecondsChange = {},
            onDeleteAllData = {},
            onOpenNotificationSettings = {},
            onOpenRecheckAlertChannelSettings = {}
        )
    }
}
