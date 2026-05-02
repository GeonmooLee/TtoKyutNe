package com.example.ttokyutne.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ttokyutne.R
import com.example.ttokyutne.ui.theme.TtoKyutNeTheme

private object HomeDesignTokens {
    val AppBg = Color(0xFFFBF7F3)
    val SurfacePrimary = Color(0xF4FFFFFF).copy(alpha = 0.4f)
    val SurfaceSecondary = Color(0xFFFFF8F4)
    val BorderSoft = Color(0xA8F1E7DF)
    val AccentCoral = Color(0xFFFF8F70)
    val AccentCoralSoft = Color(0xFFFFD9CB)
    val AccentLavender = Color(0xFFA99AE7)
    val AccentLavenderSoft = Color(0xFFECE7FF)
    val AccentBeige = Color(0xFFE8D8C6)
    val TextPrimary = Color(0xFF3F312A)
    val TextSecondary = Color(0xFF6D625D)
    val TextTertiary = Color(0xFF9B928C)
    val IconInactive = Color(0xFF8D837D)
    val White = Color(0xFFFFFFFF)
}

@Composable
private fun homeFontFamily(): FontFamily {
    return MaterialTheme.typography.bodyLarge.fontFamily ?: FontFamily.SansSerif
}

private enum class HomeTab {
    Main,
    Analysis,
    Settings
}

@Composable
@Suppress("UNUSED_PARAMETER")
fun HomeScreen(
    uiState: HomeUiState = HomeUiState(),
    notificationPermissionGranted: Boolean = true,
    onOpenTodayAnalysis: () -> Unit = {},
    onOpenWeeklyAnalysis: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onRequestNotificationPermission: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = HomeDesignTokens.AppBg,
        bottomBar = {
            JjamkkanBottomBar(
                selectedTab = HomeTab.Main,
                onMainClick = {},
                onAnalysisClick = onOpenTodayAnalysis,
                onSettingsClick = onOpenSettings
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val compactScreen = maxWidth <= 420.dp
            val horizontalPadding = if (compactScreen) 18.dp else 24.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding)
                    .padding(top = if (compactScreen) 0.dp else 12.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(if (compactScreen) 5.dp else 11.dp)
            ) {
                HomeHeader()
                CoreInsightCard(lastRecheckIntervalText = uiState.lastRecheckIntervalText)
                TodaySummaryCards(
                    todayScreenOnCount = uiState.todayScreenOnCount,
                    shortRecheckCount = uiState.shortRecheckCount
                )
                MinRecheckIntervalCard(
                    shortestRecheckIntervalText = uiState.shortestRecheckIntervalText
                )
                ChangeSummaryCard(diffFromYesterday = uiState.diffFromYesterday)
                AnalysisCtaCard(onClick = onOpenTodayAnalysis)
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun HomeHeader(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val compact = maxWidth <= 390.dp
        val headerHeight = if (compact) 128.dp else 170.dp
        val imageWidth = if (compact) 178.dp else 238.dp
        val titleSize = if (compact) 43.sp else 51.sp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_header_landscape),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = if (compact) 18.dp else 10.dp)
                    .padding(bottom = 0.dp)
                    .width(imageWidth)
                    .alpha(0.94f),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = if (compact) 12.dp else 22.dp)
                    .fillMaxWidth(if (compact) 0.9f else 0.82f),
                verticalArrangement = Arrangement.spacedBy(if (compact) 9.dp else 13.dp)
            ) {
                Text(
                    text = "잠깐",
                    color = HomeDesignTokens.TextPrimary,
                    fontSize = titleSize,
                    lineHeight = (titleSize.value + 4).sp,
                    fontFamily = homeFontFamily(),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.sp
                )
                Text(
                    text = "무의식적인 확인 습관을 부드럽게 알아차려요",
                    color = HomeDesignTokens.TextSecondary,
                    fontSize = if (compact) 12.2.sp else 14.5.sp,
                    lineHeight = if (compact) 16.sp else 20.sp,
                    fontFamily = homeFontFamily(),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
private fun CoreInsightCard(
    lastRecheckIntervalText: String,
    modifier: Modifier = Modifier
) {
    SoftCard(
        modifier = modifier.fillMaxWidth(),
        radius = 20.dp,
        shadowElevation = 0.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 146.dp)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            val compact = maxWidth <= 420.dp
            val illustrationSize = when {
                maxWidth <= 330.dp -> 104.dp
                maxWidth <= 420.dp -> 126.dp
                else -> 154.dp
            }
            val headlineSize = if (compact) 28.5.sp else 34.sp

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home_core_insight_illustration),
                    contentDescription = null,
                    modifier = Modifier.size(illustrationSize),
                    contentScale = ContentScale.Fit
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 9.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = HomeDesignTokens.AccentCoral)) {
                                    append(lastRecheckIntervalText)
                                }
                                append(" 만에")
                            },
                            color = HomeDesignTokens.TextPrimary,
                            fontSize = headlineSize,
                            lineHeight = (headlineSize.value + 7).sp,
                            fontFamily = homeFontFamily(),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.sp
                        )
                        Text(
                            text = "다시 켰어요",
                            color = HomeDesignTokens.TextPrimary,
                            fontSize = headlineSize,
                            lineHeight = (headlineSize.value + 7).sp,
                            fontFamily = homeFontFamily(),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(
                            text = "방금 확인했는데 또 켠 순간이에요.",
                            color = HomeDesignTokens.TextSecondary,
                            fontSize = if (compact) 9.4.sp else 13.5.sp,
                            lineHeight = if (compact) 12.5.sp else 19.sp,
                            fontFamily = homeFontFamily(),
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            softWrap = false
                        )
                        Text(
                            text = "잠깐 멈추고, 왜 켰는지 떠올려봐요.",
                            color = HomeDesignTokens.TextSecondary,
                            fontSize = if (compact) 9.4.sp else 13.5.sp,
                            lineHeight = if (compact) 12.5.sp else 19.sp,
                            fontFamily = homeFontFamily(),
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodaySummaryCards(
    todayScreenOnCount: Int,
    shortRecheckCount: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val compact = maxWidth <= 420.dp
        val cardHeight = if (compact) 104.dp else 126.dp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)
        ) {
            SummaryMetricCard(
                title = "오늘 화면 켠 횟수",
                compactTitle = "오늘 화면 켠 횟수",
                value = todayScreenOnCount.toString(),
                unit = "회",
                iconRes = R.drawable.ic_phone_android_rounded,
                badgeColor = HomeDesignTokens.AccentLavenderSoft,
                iconColor = Color(0xFF6F5FC2),
                modifier = Modifier
                    .weight(1f)
                    .height(cardHeight)
            )
            SummaryMetricCard(
                title = "짧은 재확인 횟수",
                compactTitle = "짧은 재확인 횟수",
                value = shortRecheckCount.toString(),
                unit = "회",
                caption = "1분 안에 다시 켠 횟수",
                compactCaption = "1분 안에 다시 켠 횟수",
                iconRes = R.drawable.ic_repeat_rounded,
                badgeColor = HomeDesignTokens.AccentCoralSoft.copy(alpha = 0.58f),
                iconColor = HomeDesignTokens.TextPrimary,
                modifier = Modifier
                    .weight(1f)
                    .height(cardHeight)
            )
        }
    }
}

@Composable
private fun SummaryMetricCard(
    title: String,
    compactTitle: String,
    value: String,
    unit: String,
    @DrawableRes iconRes: Int,
    badgeColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    caption: String? = null,
    compactCaption: String? = caption
) {
    SoftCard(
        modifier = modifier,
        radius = 18.dp,
        shadowElevation = 0.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 9.dp)
        ) {
            val compact = maxWidth <= 190.dp
            val badgeSize = if (compact) 38.dp else 54.dp
            val valueSize = if (compact) 27.sp else 34.sp

            Row(
                modifier = Modifier.fillMaxSize()
                    .padding(start = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(
                    iconRes = iconRes,
                    containerColor = badgeColor,
                    iconColor = iconColor,
                    badgeSize = badgeSize,
                    iconSize = if (compact) 20.dp else 29.dp
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = if (compact) Alignment.CenterHorizontally else Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (compact) compactTitle else title,
                        color = HomeDesignTokens.TextPrimary,
                        fontSize = if (compact) 9.6.sp else 14.sp,
                        lineHeight = if (compact) 12.4.sp else 19.sp,
                        fontFamily = homeFontFamily(),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                        textAlign = if (compact) androidx.compose.ui.text.style.TextAlign.Center else androidx.compose.ui.text.style.TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.height(if (compact) 4.dp else 7.dp))
                    MetricValue(
                        value = value,
                        unit = unit,
                        valueSize = valueSize,
                        unitSize = if (compact) 13.sp else 15.sp,
                        color = Color.Black
                    )
                    if (caption != null) {
                        Spacer(modifier = Modifier.height(if (compact) 1.dp else 4.dp))
                        Text(
                            text = if (compact) compactCaption.orEmpty() else caption,
                            color = HomeDesignTokens.TextSecondary,
                            fontSize = if (compact) 7.9.sp else 11.sp,
                            lineHeight = if (compact) 10.4.sp else 15.sp,
                            fontFamily = homeFontFamily(),
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.sp,
                            textAlign = if (compact) androidx.compose.ui.text.style.TextAlign.Center else androidx.compose.ui.text.style.TextAlign.Start,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MinRecheckIntervalCard(
    shortestRecheckIntervalText: String,
    modifier: Modifier = Modifier
) {
    SoftCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp),
        radius = 18.dp,
        shadowElevation = 0.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
        ) {
            val compact = maxWidth <= 420.dp
            val ornamentWidth = if (compact) 138.dp else 172.dp
            val endTextGuard = if (compact) 86.dp else 108.dp

            Image(
                painter = painterResource(id = R.drawable.home_min_interval_ornament),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(ornamentWidth)
                    .alpha(0.86f),
                contentScale = ContentScale.Fit
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 14.dp, top = 11.dp, end = endTextGuard, bottom = 11.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBadge(
                    iconRes = R.drawable.ic_clock_rounded,
                    containerColor = HomeDesignTokens.AccentBeige,
                    iconColor = HomeDesignTokens.TextSecondary,
                    badgeSize = if (compact) 40.dp else 50.dp,
                    iconSize = if (compact) 22.dp else 27.dp
                )
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "가장 짧은 재확인 간격",
                        color = HomeDesignTokens.TextPrimary,
                        fontSize = if (compact) 13.2.sp else 14.sp,
                        lineHeight = if (compact) 17.sp else 18.sp,
                        fontFamily = homeFontFamily(),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        softWrap = false
                    )
                    Text(
                        text = shortestRecheckIntervalText,
                        color = Color.Black,
                        fontSize = if (compact) 24.sp else 28.sp,
                        lineHeight = if (compact) 27.sp else 32.sp,
                        fontFamily = homeFontFamily(),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangeSummaryCard(
    diffFromYesterday: Int,
    modifier: Modifier = Modifier
) {
    SoftCard(
        modifier = modifier.fillMaxWidth(),
        radius = 18.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(
                iconRes = R.drawable.ic_leaf_rounded,
                containerColor = HomeDesignTokens.AccentCoralSoft.copy(alpha = 0.72f),
                iconColor = HomeDesignTokens.AccentCoral,
                badgeSize = 38.dp,
                iconSize = 20.dp
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = buildAnnotatedString {
                        append("어제보다 ")
                        withStyle(SpanStyle(color = HomeDesignTokens.AccentCoral)) {
                            append("${diffFromYesterday}번")
                        }
                        append(" 적어요")
                    },
                    color = HomeDesignTokens.TextPrimary,
                    fontSize = 13.2.sp,
                    lineHeight = 17.sp,
                    fontFamily = homeFontFamily(),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    softWrap = false
                )
                Text(
                    text = "조금씩 달라지고 있어요.",
                    color = HomeDesignTokens.TextSecondary,
                    fontSize = 11.2.sp,
                    lineHeight = 15.sp,
                    fontFamily = homeFontFamily(),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
private fun AnalysisCtaCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SoftCard(
        modifier = modifier.fillMaxWidth(),
        radius = 20.dp,
        shadowElevation = 0.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val compact = maxWidth <= 420.dp
            val ornamentWidth = if (compact) 204.dp else 272.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 112.dp else 128.dp)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home_analysis_graph_ornament),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(ornamentWidth)
                        .alpha(0.34f),
                    contentScale = ContentScale.Fit
                )
                AnalysisCardLabel(
                    compact = compact,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth(if (compact) 0.66f else 0.58f)
                )
                AnalysisButton(
                    compact = compact,
                    onClick = onClick,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
private fun AnalysisCardLabel(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBadge(
            iconRes = R.drawable.ic_bar_chart_rounded,
            containerColor = HomeDesignTokens.AccentLavender,
            iconColor = HomeDesignTokens.White,
            badgeSize = if (compact) 44.dp else 60.dp,
            iconSize = if (compact) 24.dp else 32.dp
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "오늘 분석 보기",
                color = HomeDesignTokens.TextPrimary,
                fontSize = if (compact) 17.sp else 23.sp,
                lineHeight = if (compact) 22.sp else 29.sp,
                fontFamily = homeFontFamily(),
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                softWrap = false
            )
            Text(
                text = "언제 가장 자주 다시 켰는지",
                color = HomeDesignTokens.TextSecondary,
                fontSize = if (compact) 10.4.sp else 13.sp,
                lineHeight = if (compact) 14.sp else 18.sp,
                fontFamily = homeFontFamily(),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                softWrap = false
            )
            Text(
                text = "확인해볼까요?",
                color = HomeDesignTokens.TextSecondary,
                fontSize = if (compact) 10.4.sp else 13.sp,
                lineHeight = if (compact) 14.sp else 18.sp,
                fontFamily = homeFontFamily(),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                softWrap = false
            )
        }
    }
}

@Composable
private fun AnalysisButton(
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(if (compact) 38.dp else 46.dp)
            .widthIn(min = if (compact) 118.dp else 156.dp),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(start = if (compact) 13.dp else 20.dp, end = 9.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = HomeDesignTokens.AccentLavender,
            contentColor = HomeDesignTokens.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "분석 보러가기",
            fontSize = if (compact) 11.4.sp else 15.sp,
            lineHeight = if (compact) 16.sp else 20.sp,
            fontFamily = homeFontFamily(),
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right_rounded),
            contentDescription = null,
            modifier = Modifier.size(if (compact) 20.dp else 24.dp)
        )
    }
}

@Composable
private fun JjamkkanBottomBar(
    selectedTab: HomeTab,
    onMainClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(HomeDesignTokens.AppBg)
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 7.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp),
            shape = RoundedCornerShape(22.dp),
            color = HomeDesignTokens.SurfacePrimary,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, HomeDesignTokens.BorderSoft)
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
                    selected = selectedTab == HomeTab.Main,
                    onClick = onMainClick,
                    modifier = Modifier.weight(1f)
                )
                BottomTabItem(
                    label = "분석",
                    iconRes = R.drawable.ic_bar_chart_rounded,
                    selected = selectedTab == HomeTab.Analysis,
                    onClick = onAnalysisClick,
                    modifier = Modifier.weight(1f)
                )
                BottomTabItem(
                    label = "설정",
                    iconRes = R.drawable.ic_settings_rounded,
                    selected = selectedTab == HomeTab.Settings,
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
    val color = if (selected) HomeDesignTokens.AccentLavender else HomeDesignTokens.IconInactive

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
            fontFamily = homeFontFamily(),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 0.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selected) HomeDesignTokens.AccentLavender else Color.Transparent)
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
private fun MetricValue(
    value: String,
    unit: String,
    valueSize: androidx.compose.ui.unit.TextUnit,
    unitSize: androidx.compose.ui.unit.TextUnit = 15.sp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = value,
            color = color,
            fontSize = valueSize,
            lineHeight = (valueSize.value + 4).sp,
            fontFamily = homeFontFamily(),
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.sp
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = unit,
            color = HomeDesignTokens.TextPrimary,
            fontSize = unitSize,
            lineHeight = (unitSize.value + 5).sp,
            fontFamily = homeFontFamily(),
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp,
            modifier = Modifier.padding(bottom = 3.dp)
        )
    }
}

@Composable
private fun SoftCard(
    modifier: Modifier = Modifier,
    radius: Dp,
    shadowElevation: Dp,
    containerColor: Color = HomeDesignTokens.SurfacePrimary,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(radius),
        color = containerColor,
        shadowElevation = shadowElevation,
        border = BorderStroke(1.dp, HomeDesignTokens.BorderSoft),
        content = content
    )
}

@PreviewScreenSizes
@Composable
private fun HomeScreenPreview() {
    TtoKyutNeTheme {
        HomeScreen()
    }
}
