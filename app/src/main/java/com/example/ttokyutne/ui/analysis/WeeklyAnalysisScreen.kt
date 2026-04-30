package com.example.ttokyutne.ui.analysis

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ttokyutne.ui.home.BusiestHourUiState
import com.example.ttokyutne.ui.home.DailyScreenOnCountUiState
import com.example.ttokyutne.ui.home.HourlyScreenOnCountUiState
import com.example.ttokyutne.ui.home.WeeklyAnalysisUiState
import com.example.ttokyutne.ui.theme.TtoKyutNeTheme
import java.time.DayOfWeek
import java.time.LocalDate

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
fun WeeklyAnalysisScreen(
    analysis: WeeklyAnalysisUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val defaultSelectedDate = analysis.dailyScreenOnCounts.lastOrNull { it.count > 0 }?.date
        ?: analysis.dailyScreenOnCounts.lastOrNull()?.date
    var selectedDate by remember(analysis.dailyScreenOnCounts) {
        mutableStateOf<LocalDate?>(defaultSelectedDate)
    }
    val selectedDailyCount = analysis.dailyScreenOnCounts.firstOrNull { dailyCount ->
        dailyCount.date == selectedDate
    } ?: analysis.dailyScreenOnCounts.lastOrNull()

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
            SummaryCards(analysis = analysis)
            DayPatternCard(
                analysis = analysis,
                selectedDate = selectedDailyCount?.date,
                onSelectDate = { selectedDate = it }
            )
            HourlyPatternCard(dailyCount = selectedDailyCount)
            TimePatternCard(busiestHour = analysis.busiestHour)
            InterpretationCard()
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
                text = "주간 분석",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = "최근 7일 동안 얼마나 자주 폰을 다시 켰는지 살펴봐요.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

@Composable
private fun SummaryCards(analysis: WeeklyAnalysisUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "최근 7일 화면 켠 횟수",
                value = "${analysis.totalScreenOnCount}회",
                caption = "오늘 포함 7일",
                containerColor = Color.White,
                accentColor = Forest,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "1분 이내 재확인",
                value = "${analysis.recheckWithinOneMinuteCount}회",
                caption = "interval <= 60초",
                containerColor = Warm,
                accentColor = WarmInk,
                modifier = Modifier.weight(1f)
            )
        }

        StatCard(
            title = "가장 자주 켠 시간대",
            value = analysis.busiestHour?.let(::formatBusiestHourValue) ?: "계산 전",
            caption = "같은 횟수면 더 이른 시간대",
            containerColor = Blue,
            accentColor = BlueInk,
            modifier = Modifier.fillMaxWidth()
        )
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
private fun DayPatternCard(
    analysis: WeeklyAnalysisUiState,
    selectedDate: LocalDate?,
    onSelectDate: (LocalDate) -> Unit
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
            Text(
                text = "요일별 패턴",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )

            if (analysis.totalScreenOnCount == 0) {
                Text(
                    text = "아직 분석할 기록이 부족해요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            } else {
                WeeklyHistogram(
                    dailyCounts = analysis.dailyScreenOnCounts,
                    selectedDate = selectedDate,
                    onSelectDate = onSelectDate
                )
            }
        }
    }
}

@Composable
private fun WeeklyHistogram(
    dailyCounts: List<DailyScreenOnCountUiState>,
    selectedDate: LocalDate?,
    onSelectDate: (LocalDate) -> Unit
) {
    val maxCount = dailyCounts.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        dailyCounts.forEach { dailyCount ->
            val selected = dailyCount.date == selectedDate
            val fraction = dailyCount.count.toFloat() / maxCount.toFloat()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelectDate(dailyCount.date) }
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (dailyCount.count > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(100.dp))
                                .background(if (selected) BlueInk else Color(0xFF98A2B3))
                        )
                    }
                }
                Text(
                    text = formatDayOfWeek(dailyCount.dayOfWeek),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) BlueInk else Muted,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${dailyCount.date.monthValue}/${dailyCount.date.dayOfMonth}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${dailyCount.count}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) BlueInk else Muted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun HourlyPatternCard(dailyCount: DailyScreenOnCountUiState?) {
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "선택한 날의 시간대",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = dailyCount?.let(::formatDateLabel) ?: "최근 7일 기록 없음",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }

            if (dailyCount == null || dailyCount.count == 0) {
                Text(
                    text = "선택한 날에는 아직 시간대 패턴을 볼 기록이 없어요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            } else {
                HourlyHistogram(hourlyCounts = dailyCount.hourlyCounts)
            }
        }
    }
}

@Composable
private fun HourlyHistogram(hourlyCounts: List<HourlyScreenOnCountUiState>) {
    val maxCount = hourlyCounts.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            hourlyCounts.forEach { hourlyCount ->
                val fraction = hourlyCount.count.toFloat() / maxCount.toFloat()
                val isPeak = hourlyCount.count == maxCount && hourlyCount.count > 0

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (hourlyCount.count > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isPeak) BlueInk else Forest)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("00", "06", "12", "18", "23").forEach { hourLabel ->
                Text(
                    text = hourLabel,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Muted
                )
            }
        }
    }
}

@Composable
private fun TimePatternCard(busiestHour: BusiestHourUiState?) {
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
                text = "시간대 분석",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = busiestHour?.let(::formatBusiestHourSentence)
                    ?: "아직 시간대 패턴을 판단할 기록이 부족해요.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = busiestHour?.let { Forest } ?: Muted
            )
            Text(
                text = busiestHour?.let { "최근 7일 동안 ${it.count}번 기록됐어요." }
                    ?: "화면 감지를 시작하면 이 영역이 채워집니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

@Composable
private fun InterpretationCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Warm,
        border = BorderStroke(1.dp, Color(0xFFFFD6C3))
    ) {
        Text(
            text = "많이 켠 시간대는 꼭 나쁜 시간이 아니라, 마음이 자주 흔들린 시간일 수도 있어요.",
            modifier = Modifier.padding(18.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = WarmInk
        )
    }
}

private fun formatDateLabel(dailyCount: DailyScreenOnCountUiState): String {
    return "${dailyCount.date.monthValue}/${dailyCount.date.dayOfMonth} ${formatDayOfWeek(dailyCount.dayOfWeek)}"
}

private fun formatDayOfWeek(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "월"
        DayOfWeek.TUESDAY -> "화"
        DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY -> "목"
        DayOfWeek.FRIDAY -> "금"
        DayOfWeek.SATURDAY -> "토"
        DayOfWeek.SUNDAY -> "일"
    }
}

private fun formatBusiestHourValue(busiestHour: BusiestHourUiState): String {
    return "${formatHourLabel(busiestHour.hour)} ${busiestHour.count}회"
}

private fun formatBusiestHourSentence(busiestHour: BusiestHourUiState): String {
    return "${formatHourLabel(busiestHour.hour)}에 가장 자주 켰어요."
}

private fun formatHourLabel(hour: Int): String {
    val period = when (hour) {
        in 0..5 -> "새벽"
        in 6..11 -> "오전"
        in 12..17 -> "오후"
        else -> "밤"
    }
    val hourText = when (val hourInTwelve = hour % 12) {
        0 -> 12
        else -> hourInTwelve
    }
    return "$period ${hourText}시대"
}

@Preview(showBackground = true)
@Composable
private fun WeeklyAnalysisScreenPreview() {
    val today = LocalDate.now()
    val dailyCounts = (6 downTo 0).map { daysAgo ->
        val date = today.minusDays(daysAgo.toLong())
        val count = listOf(4, 8, 3, 0, 11, 6, 9)[6 - daysAgo]
        DailyScreenOnCountUiState(
            date = date,
            dayOfWeek = date.dayOfWeek,
            count = count,
            hourlyCounts = (0..23).map { hour ->
                HourlyScreenOnCountUiState(
                    hour = hour,
                    count = when {
                        count == 0 -> 0
                        hour in 21..23 -> 2
                        hour in 7..9 -> 1
                        else -> 0
                    }
                )
            }
        )
    }

    TtoKyutNeTheme {
        WeeklyAnalysisScreen(
            analysis = WeeklyAnalysisUiState(
                totalScreenOnCount = dailyCounts.sumOf { it.count },
                dailyScreenOnCounts = dailyCounts,
                busiestHour = BusiestHourUiState(hour = 22, count = 7),
                averageIntervalSeconds = 140,
                recheckWithinOneMinuteCount = 5,
                recheckWithinTenMinutesCount = 18
            ),
            onBack = {}
        )
    }
}
