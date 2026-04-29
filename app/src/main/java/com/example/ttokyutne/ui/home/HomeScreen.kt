package com.example.ttokyutne.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
private val Mint = Color(0xFFE2F2EC)
private val Blue = Color(0xFFE7EEFA)
private val BlueInk = Color(0xFF315B96)
private val Warm = Color(0xFFFFECE3)
private val WarmInk = Color(0xFFB45731)

@Composable
fun HomeScreen(
    uiState: HomeUiState = HomeUiState(),
    notificationPermissionGranted: Boolean = true,
    onRecordTestEvent: () -> Unit = {},
    onStartScreenMonitor: () -> Unit = {},
    onRequestNotificationPermission: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
            AppHeader()
            HeroSection(lastIntervalSeconds = uiState.lastIntervalSeconds)
            if (!notificationPermissionGranted) {
                NotificationPermissionNotice(
                    onRequestNotificationPermission = onRequestNotificationPermission
                )
            }
            TodayMetrics(
                todayScreenOnCount = uiState.todayScreenOnCount,
                lastIntervalSeconds = uiState.lastIntervalSeconds
            )
            ReassurancePanel()
            ActionButtons()
            ScreenMonitorButton(onStartScreenMonitor = onStartScreenMonitor)
            DeveloperTestButton(
                isSaving = uiState.isSavingTestEvent,
                onRecordTestEvent = onRecordTestEvent
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun NotificationPermissionNotice(
    onRequestNotificationPermission: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFFFF6E5),
        border = BorderStroke(1.dp, Color(0xFFF5D59D))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "알림 권한이 필요해요",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = "화면을 다시 켠 순간에 또켰네 알림을 보려면 알림 권한을 허용해 주세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
            Button(
                onClick = onRequestNotificationPermission,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarmInk,
                    contentColor = Color.White
                )
            ) {
                Text(text = "알림 권한 허용", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AppHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "또켰네",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = "화면 재확인 습관 자각 도구",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = Mint,
            border = BorderStroke(1.dp, Color(0xFFC8E5DC))
        ) {
            Text(
                text = "v0.1",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Forest
            )
        }
    }
}

@Composable
private fun HeroSection(lastIntervalSeconds: Long?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = ForestDark
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = Color(0xFF194E48)
            ) {
                Text(
                    text = "알림 불안 내려놓기",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFDDF5ED)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "계속 확인하지 않아도 괜찮아요",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "연락을 놓칠까 불안해서 핸드폰을 껐다 켜는 순간을 조용히 기록해요.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFC8DCD7)
                )
            }

            ScreenCheckPreview(lastIntervalSeconds = lastIntervalSeconds)
        }
    }
}

@Composable
private fun ScreenCheckPreview(lastIntervalSeconds: Long?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF123F3A),
        border = BorderStroke(1.dp, Color(0xFF2C625C))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE2F2EC)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "잠깐",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Forest
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "마지막 확인 이후",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF9FC6BD)
                )
                Text(
                    text = formatIntervalSeconds(lastIntervalSeconds),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TodayMetrics(
    todayScreenOnCount: Int,
    lastIntervalSeconds: Long?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricTile(
            title = "오늘 화면 켠 횟수",
            value = formatCountText(todayScreenOnCount),
            caption = if (todayScreenOnCount == 0) "첫 확인 대기" else "오늘 기록 반영됨",
            containerColor = Color.White,
            accentColor = Forest,
            modifier = Modifier.weight(1f)
        )
        MetricTile(
            title = "마지막 재확인 간격",
            value = formatIntervalSeconds(lastIntervalSeconds),
            caption = if (lastIntervalSeconds == null) "간격 계산 전" else "직전 확인과의 간격",
            containerColor = Blue,
            accentColor = BlueInk,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricTile(
    title: String,
    value: String,
    caption: String,
    containerColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(132.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Ink
                )
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
        }
    }
}

@Composable
private fun ReassurancePanel() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Warm,
        border = BorderStroke(1.dp, Color(0xFFFFD6C3))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "오늘의 한 문장",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = WarmInk
            )
            Text(
                text = "연락은 조금 늦게 봐도 괜찮을 수 있어요.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Ink
            )
            Text(
                text = "또 켠 순간을 탓하지 않고, 그 마음을 알아차리는 것부터 시작해요.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

@Composable
private fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = {},
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Forest,
                contentColor = Color.White
            )
        ) {
            Text(text = "오늘 분석", fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            border = BorderStroke(1.dp, Color(0xFFB8C9C4)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Forest
            )
        ) {
            Text(text = "설정", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ScreenMonitorButton(
    onStartScreenMonitor: () -> Unit
) {
    Button(
        onClick = onStartScreenMonitor,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ForestDark,
            contentColor = Color.White
        )
    ) {
        Text(text = "화면 감지 시작", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DeveloperTestButton(
    isSaving: Boolean,
    onRecordTestEvent: () -> Unit
) {
    Button(
        onClick = onRecordTestEvent,
        enabled = !isSaving,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Ink,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF98A2B3),
            disabledContentColor = Color.White
        )
    ) {
        Text(
            text = if (isSaving) "저장 중" else "개발용 테스트 이벤트 기록",
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatCountText(count: Int): String {
    return if (count == 0) "기록 전" else "${count}회"
}

private fun formatIntervalSeconds(intervalSeconds: Long?): String {
    return when {
        intervalSeconds == null -> "첫 기록이에요"
        intervalSeconds < 60 -> "${intervalSeconds}초"
        else -> {
            val minutes = intervalSeconds / 60
            val seconds = intervalSeconds % 60
            if (seconds == 0L) "${minutes}분" else "${minutes}분 ${seconds}초"
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TtoKyutNeTheme {
        HomeScreen()
    }
}
