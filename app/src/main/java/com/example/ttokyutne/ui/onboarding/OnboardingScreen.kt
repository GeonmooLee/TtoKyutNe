package com.example.ttokyutne.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

private data class OnboardingPage(
    val badge: String,
    val title: String,
    val description: String,
    val containerColor: Color,
    val accentColor: Color
)

private val onboardingPages = listOf(
    OnboardingPage(
        badge = "알아차림",
        title = "방금 봤는데, 또 켰나요?",
        description = "또켰네는 무의식적으로 폰을 다시 켜는 순간을 알아차리도록 도와주는 앱이에요.",
        containerColor = Mint,
        accentColor = Forest
    ),
    OnboardingPage(
        badge = "기록",
        title = "화면을 켠 간격을 기록해요",
        description = "언제 폰을 켰는지, 얼마나 빨리 다시 켰는지를 기기 안에만 저장해요.",
        containerColor = Blue,
        accentColor = BlueInk
    ),
    OnboardingPage(
        badge = "알림",
        title = "불편하지 않게 알려드릴게요",
        description = "짧은 간격으로 다시 켰을 때만 조용히 알려드려요. 알림 방식은 설정에서 언제든 바꿀 수 있어요.",
        containerColor = Warm,
        accentColor = WarmInk
    ),
    OnboardingPage(
        badge = "개인정보",
        title = "기록은 내 폰 안에만",
        description = "로그인도, 서버 전송도 없습니다. 모든 기록은 이 기기 안에만 저장돼요.",
        containerColor = Color.White,
        accentColor = ForestDark
    ),
    OnboardingPage(
        badge = "시작",
        title = "시작하기",
        description = "알림 권한을 허용하면 짧은 간격으로 다시 켰을 때 또켰네가 조용히 알려드릴 수 있어요.",
        containerColor = Mint,
        accentColor = Forest
    )
)

@Composable
fun OnboardingScreen(
    notificationPermissionGranted: Boolean,
    onRequestNotificationPermission: () -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pageIndex by remember { mutableStateOf(0) }
    val currentPage = onboardingPages[pageIndex]
    val isLastPage = pageIndex == onboardingPages.lastIndex

    BackHandler(enabled = pageIndex > 0) {
        pageIndex -= 1
    }

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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Header(onSkip = onSkip)
            OnboardingCard(
                page = currentPage,
                pageIndex = pageIndex,
                pageCount = onboardingPages.size,
                notificationPermissionGranted = notificationPermissionGranted,
                isLastPage = isLastPage
            )
            PageIndicators(
                pageIndex = pageIndex,
                pageCount = onboardingPages.size
            )
            ActionButtons(
                isFirstPage = pageIndex == 0,
                isLastPage = isLastPage,
                notificationPermissionGranted = notificationPermissionGranted,
                onPrevious = { pageIndex -= 1 },
                onNext = { pageIndex += 1 },
                onStart = {
                    if (!notificationPermissionGranted) {
                        onRequestNotificationPermission()
                    }
                    onComplete()
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun Header(onSkip: () -> Unit) {
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
                text = "처음 시작하기",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
        TextButton(onClick = onSkip) {
            Text(
                text = "건너뛰기",
                fontWeight = FontWeight.Bold,
                color = Forest
            )
        }
    }
}

@Composable
private fun OnboardingCard(
    page: OnboardingPage,
    pageIndex: Int,
    pageCount: Int,
    notificationPermissionGranted: Boolean,
    isLastPage: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = page.containerColor,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = Color.White.copy(alpha = 0.72f),
                border = BorderStroke(1.dp, Line)
            ) {
                Text(
                    text = "${page.badge} ${pageIndex + 1}/$pageCount",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = page.accentColor
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Muted
                )
            }

            if (isLastPage) {
                PermissionNotice(notificationPermissionGranted = notificationPermissionGranted)
            }
        }
    }
}

@Composable
private fun PermissionNotice(notificationPermissionGranted: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (notificationPermissionGranted) "알림 권한이 준비됐어요" else "알림 권한이 필요해요",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
            Text(
                text = if (notificationPermissionGranted) {
                    "이제 설정한 조건에 맞을 때 재확인 알림을 받을 수 있어요."
                } else {
                    "시작하기를 누르면 Android 알림 권한을 요청합니다. 나중에 설정에서 바꿀 수도 있어요."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

@Composable
private fun PageIndicators(
    pageIndex: Int,
    pageCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (index == pageIndex) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (index == pageIndex) Forest else Color(0xFFD4DDE3))
            )
        }
    }
}

@Composable
private fun ActionButtons(
    isFirstPage: Boolean,
    isLastPage: Boolean,
    notificationPermissionGranted: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onStart: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = if (isLastPage) onStart else onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Forest,
                contentColor = Color.White
            )
        ) {
            Text(
                text = when {
                    !isLastPage -> "다음"
                    notificationPermissionGranted -> "시작하기"
                    else -> "알림 권한 허용하고 시작"
                },
                fontWeight = FontWeight.Bold
            )
        }

        if (!isFirstPage) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFB8C9C4)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Forest
                )
            ) {
                Text(text = "이전", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    TtoKyutNeTheme {
        OnboardingScreen(
            notificationPermissionGranted = false,
            onRequestNotificationPermission = {},
            onComplete = {},
            onSkip = {}
        )
    }
}
