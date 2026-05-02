package com.example.ttokyutne.ui.analysis

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ttokyutne.R
import com.example.ttokyutne.ui.home.BusiestHourUiState
import com.example.ttokyutne.ui.home.DailyScreenOnCountUiState
import com.example.ttokyutne.ui.home.HourlyScreenOnCountUiState
import com.example.ttokyutne.ui.home.WeeklyAnalysisUiState
import com.example.ttokyutne.ui.theme.TtoKyutNeTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

private object AnalysisColors {
    val AppBg = Color(0xFFFBF7F3)
    val SurfacePrimary = Color(0xF4FFFFFF).copy(alpha = 0.42f)
    val SurfaceRaised = Color(0xFCFFFFFF).copy(alpha = 0.76f)
    val SurfaceSecondary = Color(0xFFFFF8F4)
    val BorderSoft = Color(0xA8F1E7DF)
    val AccentCoral = Color(0xFFFF8F70)
    val AccentCoralDeep = Color(0xFFFF7358)
    val AccentCoralSoft = Color(0xFFFFD9CB)
    val AccentLavender = Color(0xFFA99AE7)
    val AccentLavenderDeep = Color(0xFF7364C7)
    val AccentLavenderSoft = Color(0xFFECE7FF)
    val AccentBeige = Color(0xFFE8D8C6)
    val AccentBeigeSoft = Color(0xFFF3E8DA)
    val TextPrimary = Color(0xFF3F312A)
    val TextSecondary = Color(0xFF6D625D)
    val TextTertiary = Color(0xFF9B928C)
    val IconInactive = Color(0xFF8D837D)
    val Divider = Color(0xFFEDE3DA)
    val GridLine = Color(0xFFE8DDD3)
    val White = Color(0xFFFFFFFF)
}

private enum class AnalysisTab {
    Main,
    Analysis,
    Settings
}

@Composable
fun WeeklyAnalysisScreen(
    analysis: WeeklyAnalysisUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenTodayAnalysis: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onPreviousWeekClick: () -> Unit = {},
    onNextWeekClick: () -> Unit = {}
) {
    BackHandler(onBack = onBack)

    val visibleWeek = remember(analysis.dailyScreenOnCounts) {
        buildVisibleWeek(analysis.dailyScreenOnCounts)
    }
    val selectedDate = remember(visibleWeek) { defaultSelectedDate(visibleWeek) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AnalysisColors.AppBg,
        bottomBar = {
            AnalysisBottomBar(
                selectedTab = AnalysisTab.Analysis,
                onMainClick = onBack,
                onAnalysisClick = {},
                onSettingsClick = onOpenSettings
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AnalysisColors.AppBg)
                .padding(innerPadding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 18.dp)
        ) {
            item {
                AnalysisHeader()
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                AnalysisSegmentedControl(
                    selectedWeekly = true,
                    onDailyClick = onOpenTodayAnalysis
                )
            }
            item {
                Spacer(modifier = Modifier.height(18.dp))
                WeeklyRangeSelector(
                    rangeText = formatWeekRange(visibleWeek),
                    onPreviousClick = onPreviousWeekClick,
                    onNextClick = onNextWeekClick
                )
            }
            item {
                Spacer(modifier = Modifier.height(18.dp))
                WeeklyBarChartCard(
                    weekDays = visibleWeek,
                    selectedDate = selectedDate,
                    onSelectedDateChange = {
                        onOpenTodayAnalysis()
                    },
                    onDetailClick = onOpenTodayAnalysis
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                WeeklySummaryGrid(
                    analysis = analysis,
                    weekDays = visibleWeek
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                WeeklyInsightRows(weekDays = visibleWeek)
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun analysisFontFamily(): FontFamily {
    return MaterialTheme.typography.bodyLarge.fontFamily ?: FontFamily.SansSerif
}

@Composable
private fun AnalysisHeader(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(top = 26.dp)
    ) {
        val compact = maxWidth <= 360.dp
        Column(
            verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 9.dp)
        ) {
            Text(
                text = "분석",
                color = AnalysisColors.TextPrimary,
                fontSize = if (compact) 44.sp else 50.sp,
                lineHeight = if (compact) 49.sp else 55.sp,
                fontFamily = analysisFontFamily(),
                fontWeight = FontWeight.Black,
                letterSpacing = 0.sp
            )
            Text(
                text = "오늘과 이번 주의 확인 패턴을 차분히 살펴보세요.",
                color = AnalysisColors.TextSecondary,
                fontSize = if (compact) 13.2.sp else 15.sp,
                lineHeight = if (compact) 19.sp else 22.sp,
                fontFamily = analysisFontFamily(),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
                maxLines = 2,
                overflow = TextOverflow.Clip
            )
        }
    }
}

@Composable
private fun AnalysisSegmentedControl(
    selectedWeekly: Boolean,
    onDailyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        color = AnalysisColors.SurfaceRaised,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, AnalysisColors.BorderSoft)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            SegmentOption(
                label = "일간",
                selected = !selectedWeekly,
                onClick = onDailyClick,
                modifier = Modifier.weight(1f)
            )
            SegmentOption(
                label = "주간",
                selected = selectedWeekly,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SegmentOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(15.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(15.dp),
        color = if (selected) AnalysisColors.AccentCoralSoft.copy(alpha = 0.28f) else Color.Transparent,
        border = if (selected) {
            BorderStroke(1.dp, AnalysisColors.AccentCoralSoft)
        } else {
            null
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (selected) AnalysisColors.TextPrimary else AnalysisColors.TextSecondary,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontFamily = analysisFontFamily(),
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                letterSpacing = 0.sp
            )
        }
    }
}

@Composable
private fun WeeklyRangeSelector(
    rangeText: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundIconButton(
            iconRes = R.drawable.ic_chevron_right_rounded,
            contentDescription = "이전 주",
            onClick = onPreviousClick,
            mirrorIcon = true
        )
        Text(
            text = rangeText,
            color = AnalysisColors.TextPrimary,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontFamily = analysisFontFamily(),
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(min = 180.dp)
                .padding(horizontal = 18.dp)
        )
        RoundIconButton(
            iconRes = R.drawable.ic_chevron_right_rounded,
            contentDescription = "다음 주",
            onClick = onNextClick
        )
    }
}

@Composable
private fun RoundIconButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    mirrorIcon: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = CircleShape,
        color = AnalysisColors.SurfaceRaised,
        border = BorderStroke(1.dp, AnalysisColors.BorderSoft),
        shadowElevation = 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (mirrorIcon) {
                Text(
                    text = "‹",
                    color = AnalysisColors.IconInactive,
                    fontSize = 34.sp,
                    lineHeight = 34.sp,
                    fontFamily = analysisFontFamily(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-1).dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = contentDescription,
                    tint = AnalysisColors.IconInactive,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun WeeklyBarChartCard(
    weekDays: List<DailyScreenOnCountUiState>,
    selectedDate: LocalDate?,
    onSelectedDateChange: (LocalDate) -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDay = weekDays.firstOrNull { it.date == selectedDate }
        ?: weekDays.firstOrNull { it.dayOfWeek == DayOfWeek.TUESDAY }
        ?: weekDays.firstOrNull()
    val selectedDayName = selectedDay?.dayOfWeek?.let(::formatLongDayOfWeek) ?: "화요일"

    SoftAnalysisCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        radius = 28.dp,
        containerColor = AnalysisColors.SurfaceRaised
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "화면 켠 횟수 (회)",
                    color = AnalysisColors.TextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontFamily = analysisFontFamily(),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            WeeklyBarChart(
                weekDays = weekDays,
                selectedDate = selectedDay?.date,
                onSelectedDateChange = onSelectedDateChange
            )

            ChartHintPill()
        }
    }
}

@Composable
private fun DetailChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(100.dp),
        color = AnalysisColors.AccentCoralSoft.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, AnalysisColors.AccentCoralSoft.copy(alpha = 0.72f))
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = text,
                color = AnalysisColors.AccentCoralDeep,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontFamily = analysisFontFamily(),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right_rounded),
                contentDescription = null,
                tint = AnalysisColors.AccentCoralDeep,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun WeeklyBarChart(
    weekDays: List<DailyScreenOnCountUiState>,
    selectedDate: LocalDate?,
    onSelectedDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(if (weekDays.size <= 7) 232.dp else 240.dp)
    ) {
        val compact = maxWidth <= 320.dp
        val labelWidth = if (compact) 30.dp else 36.dp
        val topSpace = if (compact) 28.dp else 34.dp
        val plotHeight = if (compact) 156.dp else 164.dp
        val dayLabelHeight = 34.dp
        val axisMax = 80f
        val labels = listOf(80, 60, 40, 20, 0)
        val gridEffect = remember {
            PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            val labelWidthPx = labelWidth.toPx()
            val topPx = topSpace.toPx()
            val plotHeightPx = plotHeight.toPx()
            labels.forEach { label ->
                val y = topPx + plotHeightPx * (1f - label / axisMax)
                drawLine(
                    color = if (label == 0) AnalysisColors.Divider else AnalysisColors.GridLine,
                    start = Offset(labelWidthPx, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = if (label == 0) null else gridEffect
                )
            }
        }

        labels.forEach { label ->
            val yOffset = topSpace + plotHeight * (1f - label / axisMax) - 9.dp
            Text(
                text = label.toString(),
                color = AnalysisColors.TextSecondary.copy(alpha = 0.88f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontFamily = analysisFontFamily(),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = yOffset)
                    .width(labelWidth)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(topSpace + plotHeight + dayLabelHeight)
                .padding(start = labelWidth, top = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            weekDays.forEach { day ->
                val selected = day.date == selectedDate
                WeeklyBarItem(
                    day = day,
                    selected = selected,
                    axisMax = axisMax,
                    topSpace = topSpace,
                    plotHeight = plotHeight,
                    dayLabelHeight = dayLabelHeight,
                    onClick = { onSelectedDateChange(day.date) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WeeklyBarItem(
    day: DailyScreenOnCountUiState,
    selected: Boolean,
    axisMax: Float,
    topSpace: Dp,
    plotHeight: Dp,
    dayLabelHeight: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val fraction = (day.count.toFloat() / axisMax).coerceIn(0f, 1f)
    val barHeight = if (day.count == 0) 0.dp else (plotHeight * fraction).coerceAtLeast(10.dp)
    val barWidth = 22.dp
    val numberColor = if (selected) AnalysisColors.AccentCoralDeep else AnalysisColors.AccentLavenderDeep
    val barBrush = if (selected) {
        Brush.verticalGradient(
            colors = listOf(
                AnalysisColors.AccentCoralSoft,
                AnalysisColors.AccentCoralDeep
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                AnalysisColors.AccentLavenderSoft,
                AnalysisColors.AccentLavender.copy(alpha = 0.68f)
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(topSpace),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = day.count.toString(),
                color = numberColor,
                fontSize = if (selected) 24.sp else 16.sp,
                lineHeight = if (selected) 27.sp else 20.sp,
                fontFamily = analysisFontFamily(),
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                letterSpacing = 0.sp
            )
        }
        Box(
            modifier = Modifier
                .height(plotHeight)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (day.count > 0) {
                Box(
                    modifier = Modifier
                        .width(barWidth)
                        .height(barHeight)
                        .clip(RoundedCornerShape(10.dp))
                        .background(barBrush)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dayLabelHeight),
            contentAlignment = Alignment.Center
        ) {
            DayLabelPill(
                dayLabel = formatDayOfWeek(day.dayOfWeek),
                selected = selected
            )
        }
    }
}

@Composable
private fun DayLabelPill(
    dayLabel: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(38.dp)
            .height(30.dp),
        shape = RoundedCornerShape(100.dp),
        color = if (selected) AnalysisColors.AccentCoralSoft.copy(alpha = 0.38f) else Color.Transparent,
        border = if (selected) {
            BorderStroke(1.dp, AnalysisColors.AccentCoralSoft.copy(alpha = 0.78f))
        } else {
            null
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = dayLabel,
                color = AnalysisColors.TextSecondary,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                fontFamily = analysisFontFamily(),
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                letterSpacing = 0.sp
            )
        }
    }
}

@Composable
private fun ChartHintPill(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .widthIn(max = 330.dp),
        shape = RoundedCornerShape(24.dp),
        color = AnalysisColors.AccentBeigeSoft.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_leaf_rounded),
                contentDescription = null,
                tint = AnalysisColors.AccentCoral,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "요일 막대를 누르면 일간 분석으로 이동해요.",
                color = AnalysisColors.TextPrimary,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontFamily = analysisFontFamily(),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}

@Composable
private fun WeeklySummaryGrid(
    analysis: WeeklyAnalysisUiState,
    weekDays: List<DailyScreenOnCountUiState>,
    modifier: Modifier = Modifier
) {
    val totalCount = analysis.totalScreenOnCount.takeIf { it > 0 } ?: weekDays.sumOf { it.count }
    val averageRecheckCount = if (weekDays.isEmpty()) {
        0
    } else {
        (analysis.recheckWithinTenMinutesCount.toFloat() / weekDays.size.toFloat()).roundToInt()
    }
    val busiestDay = weekDays
        .filter { it.count > 0 }
        .maxByOrNull { it.count }
        ?.dayOfWeek
        ?.let(::formatLongDayOfWeek)
        ?: "계산 전"
    val averageInterval = analysis.averageIntervalSeconds?.let(::formatIntervalSeconds) ?: "계산 전"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeeklyMetricCard(
                label = "이번 주 총 화면 켠 횟수",
                value = "${totalCount}회",
                iconRes = R.drawable.ic_phone_android_rounded,
                iconBackground = AnalysisColors.AccentLavenderSoft,
                iconTint = AnalysisColors.AccentLavenderDeep,
                modifier = Modifier.weight(1f)
            )
            WeeklyMetricCard(
                label = "하루 평균 재확인 횟수",
                value = "${averageRecheckCount}회",
                iconRes = R.drawable.ic_repeat_rounded,
                iconBackground = AnalysisColors.AccentCoralSoft.copy(alpha = 0.48f),
                iconTint = AnalysisColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeeklyMetricCard(
                label = "가장 많이 확인한 요일",
                value = busiestDay,
                iconRes = R.drawable.ic_leaf_rounded,
                iconBackground = AnalysisColors.AccentCoralSoft.copy(alpha = 0.48f),
                iconTint = AnalysisColors.AccentCoralDeep,
                modifier = Modifier.weight(1f)
            )
            WeeklyMetricCard(
                label = "평균 재확인 간격",
                value = averageInterval,
                iconRes = R.drawable.ic_clock_rounded,
                iconBackground = AnalysisColors.AccentLavenderSoft,
                iconTint = AnalysisColors.AccentLavenderDeep,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WeeklyMetricCard(
    label: String,
    value: String,
    @DrawableRes iconRes: Int,
    iconBackground: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    SoftAnalysisCard(
        modifier = modifier.height(112.dp),
        radius = 20.dp,
        containerColor = AnalysisColors.SurfacePrimary
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            val compact = maxWidth <= 156.dp
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(
                    iconRes = iconRes,
                    containerColor = iconBackground,
                    iconColor = iconTint,
                    badgeSize = if (compact) 48.dp else 56.dp,
                    iconSize = if (compact) 23.dp else 29.dp
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = label,
                        color = AnalysisColors.TextPrimary,
                        fontSize = if (compact) 11.sp else 13.4.sp,
                        lineHeight = if (compact) 14.sp else 18.sp,
                        fontFamily = analysisFontFamily(),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Clip
                    )
                    Spacer(modifier = Modifier.height(if (compact) 5.dp else 7.dp))
                    Text(
                        text = value,
                        color = Color.Black,
                        fontSize = metricValueSize(value, compact),
                        lineHeight = metricValueLineHeight(value, compact),
                        fontFamily = analysisFontFamily(),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyInsightRows(
    weekDays: List<DailyScreenOnCountUiState>,
    modifier: Modifier = Modifier
) {
    val busiestDay = weekDays
        .filter { it.count > 0 }
        .maxByOrNull { it.count }
        ?.dayOfWeek
        ?.let(::formatLongDayOfWeek)
        ?: "화요일"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        InsightRow(
            iconRes = R.drawable.ic_clock_rounded,
            iconBackground = AnalysisColors.AccentCoralSoft.copy(alpha = 0.52f),
            iconTint = AnalysisColors.AccentCoralDeep,
            title = AnnotatedString("$busiestDay 저녁에 다시 확인하는 빈도가 높아요."),
            description = "18시~21시에 집중되어 있어요."
        )
        InsightRow(
            iconRes = R.drawable.ic_bar_chart_rounded,
            iconBackground = AnalysisColors.AccentLavenderSoft,
            iconTint = AnalysisColors.AccentLavenderDeep,
            title = buildAnnotatedString {
                append("평균보다 짧은 간격의 재확인이 ")
                withStyle(SpanStyle(color = AnalysisColors.AccentLavenderDeep)) {
                    append("오후")
                }
                append("에 집중되어 있어요.")
            },
            description = "12시~18시 사이에 54%가 발생했어요."
        )
    }
}

@Composable
private fun InsightRow(
    @DrawableRes iconRes: Int,
    iconBackground: Color,
    iconTint: Color,
    title: AnnotatedString,
    description: String,
    modifier: Modifier = Modifier
) {
    SoftAnalysisCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp),
        radius = 22.dp,
        containerColor = AnalysisColors.SurfacePrimary
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            val compact = maxWidth <= 330.dp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(if (compact) 11.dp else 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(
                    iconRes = iconRes,
                    containerColor = iconBackground,
                    iconColor = iconTint,
                    badgeSize = if (compact) 48.dp else 56.dp,
                    iconSize = if (compact) 25.dp else 30.dp
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = title,
                        color = AnalysisColors.TextPrimary,
                        fontSize = if (compact) 13.2.sp else 15.sp,
                        lineHeight = if (compact) 17.sp else 20.sp,
                        fontFamily = analysisFontFamily(),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        softWrap = false
                    )
                    Text(
                        text = description,
                        color = AnalysisColors.TextTertiary,
                        fontSize = if (compact) 12.sp else 13.sp,
                        lineHeight = if (compact) 15.sp else 18.sp,
                        fontFamily = analysisFontFamily(),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        softWrap = false
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right_rounded),
                    contentDescription = null,
                    tint = AnalysisColors.IconInactive,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun AnalysisBottomBar(
    selectedTab: AnalysisTab,
    onMainClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AnalysisColors.AppBg)
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 7.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp),
            shape = RoundedCornerShape(22.dp),
            color = AnalysisColors.SurfacePrimary,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, AnalysisColors.BorderSoft)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomTabItem(
                    label = "메인",
                    iconRes = R.drawable.ic_home_rounded,
                    selected = selectedTab == AnalysisTab.Main,
                    onClick = onMainClick,
                    modifier = Modifier.weight(1f)
                )
                BottomTabItem(
                    label = "분석",
                    iconRes = R.drawable.ic_bar_chart_rounded,
                    selected = selectedTab == AnalysisTab.Analysis,
                    onClick = onAnalysisClick,
                    modifier = Modifier.weight(1f)
                )
                BottomTabItem(
                    label = "설정",
                    iconRes = R.drawable.ic_settings_rounded,
                    selected = selectedTab == AnalysisTab.Settings,
                    onClick = onSettingsClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomTabItem(
    label: String,
    @DrawableRes iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val color = if (selected) AnalysisColors.AccentLavenderDeep else AnalysisColors.IconInactive

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(top = 8.dp, bottom = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(27.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            lineHeight = 15.sp,
            fontFamily = analysisFontFamily(),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 0.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selected) AnalysisColors.AccentLavenderDeep else Color.Transparent)
        )
    }
}

@Composable
private fun IconBadge(
    @DrawableRes iconRes: Int,
    containerColor: Color,
    iconColor: Color,
    badgeSize: Dp,
    iconSize: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(badgeSize)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
private fun SoftAnalysisCard(
    modifier: Modifier = Modifier,
    radius: Dp,
    containerColor: Color = AnalysisColors.SurfacePrimary,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(radius),
        color = containerColor,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, AnalysisColors.BorderSoft),
        content = content
    )
}

private fun buildVisibleWeek(
    dailyCounts: List<DailyScreenOnCountUiState>
): List<DailyScreenOnCountUiState> {
    val anchorDate = dailyCounts.maxOfOrNull { it.date } ?: LocalDate.now()
    val weekStart = anchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val countsByDate = dailyCounts.associateBy { it.date }
    return (0L..6L).map { offset ->
        val date = weekStart.plusDays(offset)
        countsByDate[date] ?: DailyScreenOnCountUiState(
            date = date,
            dayOfWeek = date.dayOfWeek,
            count = 0,
            hourlyCounts = (0..23).map { hour ->
                HourlyScreenOnCountUiState(hour = hour, count = 0)
            }
        )
    }
}

private fun defaultSelectedDate(weekDays: List<DailyScreenOnCountUiState>): LocalDate? {
    return weekDays
        .filter { it.count > 0 }
        .maxByOrNull { it.count }
        ?.date
        ?: weekDays.firstOrNull { it.dayOfWeek == DayOfWeek.TUESDAY }?.date
        ?: weekDays.firstOrNull()?.date
}

private fun formatWeekRange(weekDays: List<DailyScreenOnCountUiState>): String {
    val start = weekDays.firstOrNull()?.date ?: LocalDate.now()
    val end = weekDays.lastOrNull()?.date ?: start.plusDays(6)
    return "${start.monthValue}월 ${start.dayOfMonth}일–${end.monthValue}월 ${end.dayOfMonth}일"
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

private fun formatLongDayOfWeek(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "월요일"
        DayOfWeek.TUESDAY -> "화요일"
        DayOfWeek.WEDNESDAY -> "수요일"
        DayOfWeek.THURSDAY -> "목요일"
        DayOfWeek.FRIDAY -> "금요일"
        DayOfWeek.SATURDAY -> "토요일"
        DayOfWeek.SUNDAY -> "일요일"
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

private fun metricValueSize(value: String, compact: Boolean): TextUnit {
    return when {
        compact && value.length >= 5 -> 21.sp
        compact -> 24.sp
        value.length >= 5 -> 25.sp
        else -> 28.sp
    }
}

private fun metricValueLineHeight(value: String, compact: Boolean): TextUnit {
    return when {
        compact && value.length >= 5 -> 25.sp
        compact -> 28.sp
        value.length >= 5 -> 29.sp
        else -> 32.sp
    }
}

@Preview(
    name = "Weekly analysis",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun WeeklyAnalysisScreenPreview() {
    val weekStart = LocalDate.of(2024, 4, 29)
    val counts = listOf(18, 37, 22, 24, 28, 32, 22)
    val dailyCounts = counts.mapIndexed { index, count ->
        val date = weekStart.plusDays(index.toLong())
        DailyScreenOnCountUiState(
            date = date,
            dayOfWeek = date.dayOfWeek,
            count = count,
            hourlyCounts = (0..23).map { hour ->
                HourlyScreenOnCountUiState(
                    hour = hour,
                    count = when {
                        hour in 18..21 && index == 1 -> 5
                        hour in 12..18 -> 2
                        else -> 0
                    }
                )
            }
        )
    }

    TtoKyutNeTheme {
        WeeklyAnalysisScreen(
            analysis = WeeklyAnalysisUiState(
                totalScreenOnCount = 183,
                dailyScreenOnCounts = dailyCounts,
                busiestHour = BusiestHourUiState(hour = 19, count = 18),
                averageIntervalSeconds = 84,
                recheckWithinOneMinuteCount = 22,
                recheckWithinTenMinutesCount = 49
            ),
            onBack = {},
            onOpenTodayAnalysis = {},
            onOpenSettings = {}
        )
    }
}
