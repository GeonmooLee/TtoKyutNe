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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.ttokyutne.ui.home.HourlyScreenOnCountUiState
import com.example.ttokyutne.ui.home.RecentScreenOnRecordUiState
import com.example.ttokyutne.ui.home.TodayAnalysisUiState
import com.example.ttokyutne.ui.theme.TtoKyutNeTheme
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max

private object DailyAnalysisColors {
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

private enum class DailyAnalysisTab {
    Main,
    Analysis,
    Settings
}

private data class DisplayRecord(
    val time: String,
    val message: String
)

@Composable
fun TodayAnalysisScreen(
    analysis: TodayAnalysisUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenWeeklyAnalysis: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onPreviousDateClick: () -> Unit = {},
    onNextDateClick: () -> Unit = {},
    onChartHelpClick: () -> Unit = {}
) {
    BackHandler(onBack = onBack)

    val chartData = remember(analysis.hourlyScreenOnCounts) {
        dailyChartValues(analysis.hourlyScreenOnCounts)
    }
    val selectedHour = remember(chartData) { selectedChartHour(chartData) }
    val selectedValue = chartData.getOrElse(selectedHour) { 0 }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DailyAnalysisColors.AppBg,
        bottomBar = {
            DailyAnalysisBottomBar(
                selectedTab = DailyAnalysisTab.Analysis,
                onMainClick = onBack,
                onAnalysisClick = {},
                onSettingsClick = onOpenSettings
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DailyAnalysisColors.AppBg)
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 18.dp)
        ) {
            DailyAnalysisHeader()
            Spacer(modifier = Modifier.height(24.dp))
            DailyAnalysisSegmentedControl(
                selectedWeekly = false,
                onDailyClick = {},
                onWeeklyClick = onOpenWeeklyAnalysis
            )
            Spacer(modifier = Modifier.height(18.dp))
            DailyDateSelector(
                dateText = formatDailyDateLabel(),
                onPreviousClick = onPreviousDateClick,
                onNextClick = onNextDateClick
            )
            Spacer(modifier = Modifier.height(18.dp))
            DailyLineChartCard(
                data = chartData,
                selectedHour = selectedHour,
                selectedValue = selectedValue,
                onHelpClick = onChartHelpClick
            )
            Spacer(modifier = Modifier.height(16.dp))
            DailyMetricGrid(analysis = analysis)
            Spacer(modifier = Modifier.height(16.dp))
            DailyInsightCard(selectedHour = selectedHour)
            Spacer(modifier = Modifier.height(20.dp))
            RecentRecordsSection(
                records = analysis.recentRecords
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun dailyAnalysisFontFamily(): FontFamily {
    return MaterialTheme.typography.bodyLarge.fontFamily ?: FontFamily.SansSerif
}

@Composable
private fun DailyAnalysisHeader(modifier: Modifier = Modifier) {
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
                color = DailyAnalysisColors.TextPrimary,
                fontSize = if (compact) 44.sp else 50.sp,
                lineHeight = if (compact) 49.sp else 55.sp,
                fontFamily = dailyAnalysisFontFamily(),
                fontWeight = FontWeight.Black,
                letterSpacing = 0.sp
            )
            Text(
                text = "오늘과 이번 주의 확인 패턴을 차분히 살펴보세요.",
                color = DailyAnalysisColors.TextSecondary,
                fontSize = if (compact) 13.2.sp else 15.sp,
                lineHeight = if (compact) 19.sp else 22.sp,
                fontFamily = dailyAnalysisFontFamily(),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
                maxLines = 2,
                overflow = TextOverflow.Clip
            )
        }
    }
}

@Composable
private fun DailyAnalysisSegmentedControl(
    selectedWeekly: Boolean,
    onDailyClick: () -> Unit,
    onWeeklyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        color = DailyAnalysisColors.SurfaceRaised,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, DailyAnalysisColors.BorderSoft)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            DailySegmentOption(
                label = "일간",
                selected = !selectedWeekly,
                onClick = onDailyClick,
                modifier = Modifier.weight(1f)
            )
            DailySegmentOption(
                label = "주간",
                selected = selectedWeekly,
                onClick = onWeeklyClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DailySegmentOption(
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
        color = if (selected) {
            DailyAnalysisColors.AccentCoralSoft.copy(alpha = 0.28f)
        } else {
            Color.Transparent
        },
        border = if (selected) {
            BorderStroke(1.dp, DailyAnalysisColors.AccentCoralSoft)
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
                color = if (selected) {
                    DailyAnalysisColors.TextPrimary
                } else {
                    DailyAnalysisColors.TextSecondary
                },
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontFamily = dailyAnalysisFontFamily(),
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                letterSpacing = 0.sp
            )
        }
    }
}

@Composable
private fun DailyDateSelector(
    dateText: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        contentAlignment = Alignment.Center
    ) {
        val compact = maxWidth <= 360.dp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DailyRoundIconButton(
                iconRes = R.drawable.ic_chevron_right_rounded,
                contentDescription = "이전 날짜",
                onClick = onPreviousClick,
                mirrorIcon = true
            )
            Row(
                modifier = Modifier
                    .widthIn(min = if (compact) 180.dp else 230.dp)
                    .padding(horizontal = if (compact) 10.dp else 18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateText,
                    color = DailyAnalysisColors.TextPrimary,
                    fontSize = if (compact) 18.sp else 21.sp,
                    lineHeight = if (compact) 23.sp else 27.sp,
                    fontFamily = dailyAnalysisFontFamily(),
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    softWrap = false
                )
                Spacer(modifier = Modifier.width(9.dp))
                DailyCalendarIcon(
                    color = DailyAnalysisColors.TextPrimary,
                    modifier = Modifier.size(if (compact) 21.dp else 24.dp)
                )
            }
            DailyRoundIconButton(
                iconRes = R.drawable.ic_chevron_right_rounded,
                contentDescription = "다음 날짜",
                onClick = onNextClick
            )
        }
    }
}

@Composable
private fun DailyCalendarIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.1.dp.toPx()
        val corner = 3.5.dp.toPx()
        val left = size.width * 0.13f
        val top = size.height * 0.2f
        val right = size.width * 0.87f
        val bottom = size.height * 0.86f

        drawRoundRect(
            color = color,
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = color,
            start = Offset(left, size.height * 0.42f),
            end = Offset(right, size.height * 0.42f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.32f, size.height * 0.12f),
            end = Offset(size.width * 0.32f, size.height * 0.27f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.68f, size.height * 0.12f),
            end = Offset(size.width * 0.68f, size.height * 0.27f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun DailyRoundIconButton(
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
        color = DailyAnalysisColors.SurfaceRaised,
        border = BorderStroke(1.dp, DailyAnalysisColors.BorderSoft),
        shadowElevation = 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (mirrorIcon) {
                Text(
                    text = "‹",
                    color = DailyAnalysisColors.IconInactive,
                    fontSize = 34.sp,
                    lineHeight = 34.sp,
                    fontFamily = dailyAnalysisFontFamily(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-1).dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = contentDescription,
                    tint = DailyAnalysisColors.IconInactive,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun DailyLineChartCard(
    data: List<Int>,
    selectedHour: Int,
    selectedValue: Int,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SoftDailyAnalysisCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        radius = 28.dp,
        containerColor = DailyAnalysisColors.SurfaceRaised
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "화면 켠 횟수 (회)",
                    color = DailyAnalysisColors.TextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontFamily = dailyAnalysisFontFamily(),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                    modifier = Modifier.weight(1f)
                )

            }

            DailyLineChart(
                data = data,
                selectedHour = selectedHour,
                selectedValue = selectedValue
            )
        }
    }
}



@Composable
private fun DailyLineChart(
    data: List<Int>,
    selectedHour: Int,
    selectedValue: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        val compact = maxWidth <= 320.dp
        val labelWidth = if (compact) 34.dp else 40.dp
        val topSpace = 35.dp
        val plotHeight = if (compact) 168.dp else 174.dp
        val bottomLabelHeight = 34.dp
        val axisMax = max(80, ((data.maxOrNull() ?: 0) + 19) / 20 * 20).toFloat()
        val labels = listOf(80, 60, 40, 20, 0)
        val gridEffect = remember {
            PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        }
        val guideEffect = remember {
            PathEffect.dashPathEffect(floatArrayOf(7f, 7f), 0f)
        }
        val plotWidth = maxWidth - labelWidth
        val selectedIndex = selectedHour.coerceIn(0, (data.size - 1).coerceAtLeast(0))
        val selectedFraction = if (data.size <= 1) 0f else selectedIndex / (data.size - 1).toFloat()
        val selectedYFraction = 1f - (selectedValue / axisMax).coerceIn(0f, 1f)
        val tooltipWidth = if (compact) 96.dp else 108.dp
        val tooltipX = (labelWidth + plotWidth * selectedFraction - tooltipWidth / 2f)
            .coerceIn(0.dp, maxWidth - tooltipWidth)
        val tooltipY = (topSpace + plotHeight * selectedYFraction - 26.dp)
            .coerceIn(0.dp, topSpace + plotHeight - 40.dp)

        Canvas(modifier = Modifier.matchParentSize()) {
            val labelWidthPx = labelWidth.toPx()
            val topPx = topSpace.toPx()
            val plotHeightPx = plotHeight.toPx()
            val plotWidthPx = size.width - labelWidthPx
            val bottomPx = topPx + plotHeightPx

            labels.forEach { label ->
                val y = topPx + plotHeightPx * (1f - label / axisMax)
                drawLine(
                    color = if (label == 0) {
                        DailyAnalysisColors.Divider
                    } else {
                        DailyAnalysisColors.GridLine
                    },
                    start = Offset(labelWidthPx, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = if (label == 0) null else gridEffect
                )
            }

            if (data.isNotEmpty()) {
                val points = data.mapIndexed { index, value ->
                    val x = labelWidthPx + plotWidthPx * index / (data.size - 1).coerceAtLeast(1)
                    val y = topPx + plotHeightPx * (1f - (value / axisMax).coerceIn(0f, 1f))
                    Offset(x, y)
                }
                val selectedPoint = points[selectedIndex]

                drawLine(
                    color = DailyAnalysisColors.AccentCoralSoft,
                    start = Offset(selectedPoint.x, topPx),
                    end = Offset(selectedPoint.x, bottomPx),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = guideEffect
                )

                val path = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEachIndexed { index, point ->
                        val previous = points[index]
                        val midX = (previous.x + point.x) / 2f
                        cubicTo(midX, previous.y, midX, point.y, point.x, point.y)
                    }
                }

                drawPath(
                    path = path,
                    color = DailyAnalysisColors.AccentLavender,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                points.forEachIndexed { index, point ->
                    val selected = index == selectedIndex
                    drawCircle(
                        color = if (selected) DailyAnalysisColors.White else DailyAnalysisColors.AccentLavender,
                        radius = if (selected) 9.dp.toPx() else 4.2.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = if (selected) DailyAnalysisColors.AccentCoral else DailyAnalysisColors.AccentLavender,
                        radius = if (selected) 6.2.dp.toPx() else 4.2.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        labels.forEach { label ->
            val yOffset = topSpace + plotHeight * (1f - label / axisMax) - 9.dp
            Text(
                text = label.toString(),
                color = DailyAnalysisColors.TextSecondary.copy(alpha = 0.88f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontFamily = dailyAnalysisFontFamily(),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = yOffset)
                    .width(labelWidth)
            )
        }

        listOf(0, 6, 12, 18, 24).forEach { hour ->
            val fraction = hour / 24f
            val xOffset = (labelWidth + plotWidth * fraction - 14.dp)
                .coerceIn(labelWidth - 2.dp, maxWidth - 28.dp)
            Text(
                text = hour.toString().padStart(2, '0'),
                color = DailyAnalysisColors.TextSecondary.copy(alpha = 0.9f),
                fontSize = 15.sp,
                lineHeight = 18.sp,
                fontFamily = dailyAnalysisFontFamily(),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = xOffset, y = topSpace + plotHeight + bottomLabelHeight - 29.dp)
                    .width(28.dp)
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = tooltipX, y = tooltipY)
                .width(tooltipWidth)
                .height(34.dp),
            shape = RoundedCornerShape(11.dp),
            color = DailyAnalysisColors.White,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, DailyAnalysisColors.AccentLavender.copy(alpha = 0.55f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${selectedHour}시 · ${selectedValue}회",
                    color = DailyAnalysisColors.AccentLavenderDeep,
                    fontSize = if (compact) 13.2.sp else 14.5.sp,
                    lineHeight = 18.sp,
                    fontFamily = dailyAnalysisFontFamily(),
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}

@Composable
private fun DailyMetricGrid(
    analysis: TodayAnalysisUiState,
    modifier: Modifier = Modifier
) {
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
            DailyMetricCard(
                label = "오늘 화면 켠 횟수",
                value = "${analysis.totalScreenOnCount}회",
                iconRes = R.drawable.ic_phone_android_rounded,
                iconBackground = DailyAnalysisColors.AccentLavenderSoft,
                iconTint = DailyAnalysisColors.AccentLavenderDeep,
                modifier = Modifier.weight(1f)
            )
            DailyMetricCard(
                label = "평균 재확인 간격",
                value = analysis.averageIntervalSeconds?.let(::formatIntervalSeconds) ?: "계산 중",
                iconRes = R.drawable.ic_clock_rounded,
                iconBackground = DailyAnalysisColors.AccentCoralSoft.copy(alpha = 0.48f),
                iconTint = DailyAnalysisColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DailyMetricCard(
                label = "가장 짧은 재확인 간격",
                value = analysis.shortestIntervalSeconds?.let(::formatIntervalSeconds) ?: "계산 중",
                iconRes = R.drawable.ic_clock_rounded,
                iconBackground = DailyAnalysisColors.AccentBeige,
                iconTint = DailyAnalysisColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            DailyMetricCard(
                label = "10분 이내 재확인 횟수",
                value = "${analysis.recheckWithinTenMinutesCount}회",
                iconRes = R.drawable.ic_repeat_rounded,
                iconBackground = DailyAnalysisColors.AccentLavenderSoft,
                iconTint = DailyAnalysisColors.AccentLavenderDeep,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DailyMetricCard(
    label: String,
    value: String,
    @DrawableRes iconRes: Int,
    iconBackground: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    SoftDailyAnalysisCard(
        modifier = modifier.height(112.dp),
        radius = 20.dp,
        containerColor = DailyAnalysisColors.SurfacePrimary
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
                DailyIconBadge(
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
                        color = DailyAnalysisColors.TextPrimary,
                        fontSize = if (compact) 11.sp else 13.4.sp,
                        lineHeight = if (compact) 14.sp else 18.sp,
                        fontFamily = dailyAnalysisFontFamily(),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Clip
                    )
                    Spacer(modifier = Modifier.height(if (compact) 5.dp else 7.dp))
                    Text(
                        text = value,
                        color = Color.Black,
                        fontSize = dailyMetricValueSize(value, compact),
                        lineHeight = dailyMetricValueLineHeight(value, compact),
                        fontFamily = dailyAnalysisFontFamily(),
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
private fun DailyInsightCard(
    selectedHour: Int,
    modifier: Modifier = Modifier
) {
    SoftDailyAnalysisCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .heightIn(min = 64.dp),
        radius = 18.dp,
        containerColor = DailyAnalysisColors.AccentCoralSoft.copy(alpha = 0.18f),
        borderColor = DailyAnalysisColors.AccentCoralSoft.copy(alpha = 0.78f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DailyIconBadge(
                iconRes = R.drawable.ic_leaf_rounded,
                containerColor = DailyAnalysisColors.AccentCoralSoft.copy(alpha = 0.72f),
                iconColor = DailyAnalysisColors.AccentCoralDeep,
                badgeSize = 42.dp,
                iconSize = 23.dp
            )
            Text(
                text = busiestHourInsight(selectedHour),
                color = DailyAnalysisColors.TextPrimary,
                fontSize = 15.2.sp,
                lineHeight = 20.sp,
                fontFamily = dailyAnalysisFontFamily(),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                maxLines = 2,
                overflow = TextOverflow.Clip,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RecentRecordsSection(
    records: List<RecentScreenOnRecordUiState>,
    modifier: Modifier = Modifier
) {
    val displayRecords = records
        .take(4)
        .map { record ->
            DisplayRecord(
                time = formatRecordTime(record.screenOnTime),
                message = record.intervalSeconds?.let { "${formatIntervalSeconds(it)} 만에 다시 켰어요" }
                    ?: "오늘의 첫 확인이에요"
            )
        }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Text(
            text = "최근 기록",
            color = DailyAnalysisColors.TextPrimary,
            fontSize = 20.sp,
            lineHeight = 25.sp,
            fontFamily = dailyAnalysisFontFamily(),
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.sp
        )

        SoftDailyAnalysisCard(
            modifier = Modifier.fillMaxWidth(),
            radius = 22.dp,
            containerColor = DailyAnalysisColors.SurfaceRaised
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                if (displayRecords.isEmpty()) {
                    EmptyRecordRow()
                } else {
                    displayRecords.forEachIndexed { index, record ->
                        RecentRecordRow(record = record)
                        if (index != displayRecords.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(DailyAnalysisColors.Divider)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentRecordRow(
    record: DisplayRecord,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_clock_rounded),
            contentDescription = null,
            tint = DailyAnalysisColors.AccentLavender,
            modifier = Modifier.size(29.dp)
        )
        Text(
            text = record.time,
            color = DailyAnalysisColors.TextPrimary,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontFamily = dailyAnalysisFontFamily(),
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = record.message,
            color = DailyAnalysisColors.TextSecondary,
            fontSize = 14.5.sp,
            lineHeight = 19.sp,
            fontFamily = dailyAnalysisFontFamily(),
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EmptyRecordRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_clock_rounded),
            contentDescription = null,
            tint = DailyAnalysisColors.AccentLavender.copy(alpha = 0.72f),
            modifier = Modifier.size(29.dp)
        )
        Text(
            text = "오늘 기록이 쌓이면 여기에 보여드릴게요.",
            color = DailyAnalysisColors.TextSecondary,
            fontSize = 14.5.sp,
            lineHeight = 19.sp,
            fontFamily = dailyAnalysisFontFamily(),
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DailyAnalysisBottomBar(
    selectedTab: DailyAnalysisTab,
    onMainClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DailyAnalysisColors.AppBg)
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 7.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp),
            shape = RoundedCornerShape(22.dp),
            color = DailyAnalysisColors.SurfacePrimary,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, DailyAnalysisColors.BorderSoft)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DailyBottomTabItem(
                    label = "메인",
                    iconRes = R.drawable.ic_home_rounded,
                    selected = selectedTab == DailyAnalysisTab.Main,
                    onClick = onMainClick,
                    modifier = Modifier.weight(1f)
                )
                DailyBottomTabItem(
                    label = "분석",
                    iconRes = R.drawable.ic_bar_chart_rounded,
                    selected = selectedTab == DailyAnalysisTab.Analysis,
                    onClick = onAnalysisClick,
                    modifier = Modifier.weight(1f)
                )
                DailyBottomTabItem(
                    label = "설정",
                    iconRes = R.drawable.ic_settings_rounded,
                    selected = selectedTab == DailyAnalysisTab.Settings,
                    onClick = onSettingsClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DailyBottomTabItem(
    label: String,
    @DrawableRes iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val color = if (selected) {
        DailyAnalysisColors.AccentLavenderDeep
    } else {
        DailyAnalysisColors.IconInactive
    }

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
            fontFamily = dailyAnalysisFontFamily(),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 0.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selected) DailyAnalysisColors.AccentLavenderDeep else Color.Transparent)
        )
    }
}

@Composable
private fun DailyIconBadge(
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
private fun SoftDailyAnalysisCard(
    modifier: Modifier = Modifier,
    radius: Dp,
    containerColor: Color = DailyAnalysisColors.SurfacePrimary,
    borderColor: Color = DailyAnalysisColors.BorderSoft,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(radius),
        color = containerColor,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, borderColor),
        content = content
    )
}

private fun dailyChartValues(hourlyCounts: List<HourlyScreenOnCountUiState>): List<Int> {
    if (hourlyCounts.isEmpty()) {
        return listOf(
            2, 1, 1, 2, 3, 5, 14, 7,
            4, 8, 6, 9, 5, 5, 7, 16,
            17, 26, 27, 42, 68, 48, 22, 4, 3
        )
    }

    val countsByHour = hourlyCounts.associate { it.hour to it.count }
    return (0..24).map { hour ->
        if (hour == 24) 0 else countsByHour[hour] ?: 0
    }
}

private fun selectedChartHour(data: List<Int>): Int {
    if (data.isEmpty()) return 20
    val maxValue = data.maxOrNull() ?: 0
    if (maxValue == 0) return 20
    return data.indexOf(maxValue).coerceIn(0, 23)
}

private fun formatDailyDateLabel(date: LocalDate = LocalDate.now()): String {
    return "오늘 · ${date.monthValue}월 ${date.dayOfMonth}일 ${formatShortDayOfWeek(date.dayOfWeek)}"
}

private fun formatShortDayOfWeek(dayOfWeek: DayOfWeek): String {
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

private fun formatRecordTime(timeMillis: Long): String {
    return DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(timeMillis))
}

private fun formatIntervalSeconds(intervalSeconds: Long): String {
    return when {
        intervalSeconds < 60 -> "${intervalSeconds}초"
        else -> {
            val minutes = intervalSeconds / 60
            val seconds = intervalSeconds % 60
            if (seconds == 0L) {
                "${minutes}분"
            } else {
                "${minutes}분 ${seconds}초"
            }
        }
    }
}

private fun dailyMetricValueSize(value: String, compact: Boolean): TextUnit {
    return when {
        compact && value.length >= 6 -> 20.sp
        compact && value.length >= 4 -> 23.sp
        compact -> 28.sp
        value.length >= 6 -> 24.sp
        value.length >= 4 -> 27.sp
        else -> 30.sp
    }
}

private fun dailyMetricValueLineHeight(value: String, compact: Boolean): TextUnit {
    return when {
        compact && value.length >= 6 -> 24.sp
        compact && value.length >= 4 -> 27.sp
        compact -> 32.sp
        value.length >= 6 -> 28.sp
        value.length >= 4 -> 31.sp
        else -> 34.sp
    }
}

private fun busiestHourInsight(selectedHour: Int): AnnotatedString {
    val period = if (selectedHour < 12) "오전" else "오후"
    val hour12 = when (val hour = selectedHour % 12) {
        0 -> 12
        else -> hour
    }
    return buildAnnotatedString {
        append("$period ")
        withStyle(SpanStyle(color = DailyAnalysisColors.AccentCoralDeep)) {
            append("${hour12}시")
        }
        append(" 전후에 다시 확인이 가장 많아요.")
    }
}

@Preview(
    name = "Daily analysis",
    showBackground = true,
    widthDp = 393,
    heightDp = 852
)
@Composable
private fun TodayAnalysisScreenPreview() {
    val baseTime = LocalDate.now()
        .atTime(21, 18)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
    TtoKyutNeTheme {
        TodayAnalysisScreen(
            analysis = TodayAnalysisUiState(
                totalScreenOnCount = 27,
                averageIntervalSeconds = 78,
                shortestIntervalSeconds = 12,
                recheckWithinTenMinutesCount = 8,
                hourlyScreenOnCounts = listOf(
                    2, 1, 1, 2, 3, 5, 14, 7,
                    4, 8, 6, 9, 5, 5, 7, 16,
                    17, 26, 27, 42, 68, 48, 22, 4
                ).mapIndexed { hour, count ->
                    HourlyScreenOnCountUiState(hour = hour, count = count)
                },
                recentRecords = listOf(
                    RecentScreenOnRecordUiState(4, baseTime, 42),
                    RecentScreenOnRecordUiState(3, baseTime - 32 * 60_000L, 72),
                    RecentScreenOnRecordUiState(2, baseTime - 135 * 60_000L, 125),
                    RecentScreenOnRecordUiState(1, baseTime - 230 * 60_000L, 55)
                )
            ),
            onBack = {},
            onOpenWeeklyAnalysis = {}
        )
    }
}
