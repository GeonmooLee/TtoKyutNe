package com.example.ttokyutne.ui.analysis

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ttokyutne.ui.home.RecentScreenOnRecordUiState
import com.example.ttokyutne.ui.home.TodayAnalysisUiState
import com.example.ttokyutne.ui.theme.TtoKyutNeTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val Canvas = Color(0xFFF6F8FA)
private val Ink = Color(0xFF111827)
private val Muted = Color(0xFF667085)
private val Line = Color(0xFFE1E6EA)
private val Forest = Color(0xFF0F6B5F)
private val ForestDark = Color(0xFF0B3733)
private val Blue = Color(0xFFE7EEFA)
private val BlueInk = Color(0xFF315B96)
private val Warm = Color(0xFFFFECE3)
private val WarmInk = Color(0xFFB45731)

@Composable
fun TodayAnalysisScreen(
    analysis: TodayAnalysisUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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

            if (analysis.totalScreenOnCount == 0) {
                EmptyTodayCard()
            } else {
                SummaryGrid(analysis = analysis)
                AverageNotice(analysis = analysis)
                RecentRecords(records = analysis.recentRecords)
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
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
                text = "오늘 분석",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = "오늘 00:00 이후 화면 재확인 흐름을 모아봤어요.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

@Composable
private fun EmptyTodayCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "아직 오늘 기록이 없어요",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = "화면 감지를 시작한 뒤 핸드폰을 다시 켜면 오늘 분석이 채워집니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

@Composable
private fun SummaryGrid(analysis: TodayAnalysisUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "오늘 화면 켠 총 횟수",
                value = "${analysis.totalScreenOnCount}회",
                caption = "오늘 00:00 이후",
                containerColor = Color.White,
                accentColor = Forest,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "10분 이내 재확인",
                value = "${analysis.recheckWithinTenMinutesCount}회",
                caption = "interval <= 600초",
                containerColor = Warm,
                accentColor = WarmInk,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "평균 재확인 간격",
                value = analysis.averageIntervalSeconds?.let(::formatIntervalSeconds) ?: "계산 전",
                caption = "첫 기록 제외",
                containerColor = Blue,
                accentColor = BlueInk,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "최단 재확인 간격",
                value = analysis.shortestIntervalSeconds?.let(::formatIntervalSeconds) ?: "계산 전",
                caption = "첫 기록 제외",
                containerColor = Color.White,
                accentColor = ForestDark,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    caption: String,
    containerColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(128.dp),
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
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = Ink
            )
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
private fun AverageNotice(analysis: TodayAnalysisUiState) {
    if (analysis.averageIntervalSeconds != null) return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Warm,
        border = BorderStroke(1.dp, Color(0xFFFFD6C3))
    ) {
        Text(
            text = "평균 간격은 아직 계산할 수 없어요",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = WarmInk
        )
    }
}

@Composable
private fun RecentRecords(records: List<RecentScreenOnRecordUiState>) {
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
            Text(
                text = "최근 기록 5개",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )

            records.forEach { record ->
                RecentRecordRow(record = record)
            }
        }
    }
}

@Composable
private fun RecentRecordRow(record: RecentScreenOnRecordUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = formatTime(record.screenOnTime),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Ink
            )
            Text(
                text = "이벤트 #${record.id}",
                style = MaterialTheme.typography.bodySmall,
                color = Muted
            )
        }
        Text(
            text = record.intervalSeconds?.let(::formatIntervalSeconds) ?: "첫 기록",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = record.intervalSeconds?.let { Forest } ?: Muted
        )
    }
}

private fun formatIntervalSeconds(intervalSeconds: Long): String {
    return when {
        intervalSeconds < 60 -> "${intervalSeconds}초"
        else -> {
            val minutes = intervalSeconds / 60
            val seconds = intervalSeconds % 60
            if (seconds == 0L) "${minutes}분" else "${minutes}분 ${seconds}초"
        }
    }
}

private fun formatTime(timeMillis: Long): String {
    return DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(timeMillis))
}

@Preview(showBackground = true)
@Composable
private fun TodayAnalysisScreenPreview() {
    TtoKyutNeTheme {
        TodayAnalysisScreen(
            analysis = TodayAnalysisUiState(
                totalScreenOnCount = 8,
                averageIntervalSeconds = 115,
                shortestIntervalSeconds = 18,
                recheckWithinTenMinutesCount = 6,
                recentRecords = listOf(
                    RecentScreenOnRecordUiState(8, System.currentTimeMillis(), 42),
                    RecentScreenOnRecordUiState(7, System.currentTimeMillis() - 80_000, 75),
                    RecentScreenOnRecordUiState(6, System.currentTimeMillis() - 180_000, null)
                )
            ),
            onBack = {}
        )
    }
}
