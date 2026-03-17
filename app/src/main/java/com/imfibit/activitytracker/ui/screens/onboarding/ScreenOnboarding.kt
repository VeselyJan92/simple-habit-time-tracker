package com.imfibit.activitytracker.ui.screens.onboarding

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.ui.AppTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Preview(name = "Light Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun ScreenOnboarding_Preview() = AppTheme {
    ScreenOnboarding(onOnboardingDone = {}, initialPage = 0)
}

@Preview(name = "Light Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun ScreenOnboarding_Preview1() = AppTheme {
    ScreenOnboarding(onOnboardingDone = {}, initialPage = 1)
}

@Preview(name = "Light Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun ScreenOnboarding_Preview2() = AppTheme {
    ScreenOnboarding(onOnboardingDone = {}, initialPage = 2)
}

@Preview(name = "Light Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun ScreenOnboarding_Preview3() = AppTheme {
    ScreenOnboarding(onOnboardingDone = {}, initialPage = 3)
}

private data class Page(
    val title: String,
    val description: String,
    val imageLight: Painter,
    val imageDark: Painter,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenOnboarding(
    onOnboardingDone: () -> Unit,
    initialPage: Int = 0
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp) // Let content handle status bar if needed
    ) { paddingValues ->
        val onboardPages = listOf(
            Page(
                stringResource(id = R.string.screen_onboarding_page_habits_title),
                stringResource(id = R.string.screen_onboarding_page_habits_text),
                imageLight = painterResource(id = R.drawable.onboarding_activities),
                imageDark = painterResource(id = R.drawable.onboarding_activities_dark)
            ),
            Page(
                stringResource(id = R.string.screen_onboarding_page_activity_overview_title),
                stringResource(id = R.string.screen_onboarding_page_activity_overview_text),
                imageLight = painterResource(id = R.drawable.onboarding_time_activity),
                imageDark = painterResource(id = R.drawable.onboarding_time_activity_dark)
            ),
            Page(
                stringResource(id = R.string.screen_onboarding_page_daily_checklist_title),
                stringResource(id = R.string.screen_onboarding_page_daily_checklist_text),
                imageLight = painterResource(id = R.drawable.onboarding_daily_checklist),
                imageDark = painterResource(id = R.drawable.onboarding_daily_checklist_dark)
            ),
            Page(
                stringResource(id = R.string.screen_onboarding_page_focus_board_title),
                stringResource(id = R.string.screen_onboarding_page_focus_board_text),
                imageLight = painterResource(id = R.drawable.onboarding_focus_board),
                imageDark = painterResource(id = R.drawable.onboarding_focus_board_dark)
            )
        )

        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { onboardPages.size }
        )

        val isLastPage = pagerState.currentPage == onboardPages.size - 1

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Skip Button at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (!isLastPage) {
                    Text(
                        text = stringResource(id = R.string.screen_onboarding_skip),
                        modifier = Modifier
                            .testTag(TestTag.ONBOARDING_SKIP)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOnboardingDone() }
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

                val scale = lerp(
                    start = 0.85f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
                val alpha = lerp(
                    start = 0.3f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )

                PageUI(
                    modifier = Modifier
                        .testTag(TestTag.ONBOARDING_PAGE + page)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        },
                    page = onboardPages[page]
                )
            }

            // Bottom controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator
                Row(
                    modifier = Modifier.padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(onboardPages.size) { iteration ->
                        val isSelected = pagerState.currentPage == iteration
                        val color by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            label = "color"
                        )
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 10.dp,
                            label = "width"
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(10.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                // Next / Get Started Button
                Button(
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag(if (isLastPage) TestTag.ONBOARDING_PAGE_GO_TO_APP else "ONBOARDING_NEXT"),
                    onClick = {
                        if (isLastPage) {
                            onOnboardingDone()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    AnimatedContent(
                        targetState = isLastPage,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                        }, label = "button_text"
                    ) { lastPage ->
                        if (lastPage) {
                            Text(
                                text = stringResource(id = R.string.screen_onboarding_track),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.action_next),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PageUI(modifier: Modifier, page: Page) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = if (isSystemInDarkTheme()) page.imageDark else page.imageLight,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(0.9f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}