package com.imfibit.activitytracker.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.ui.AppTheme
import com.imfibit.activitytracker.ui.components.Colors


@Preview
@Composable
private fun ScreenOnboarding_Preview0() = AppTheme {
    ScreenOnboarding(
        onOnboardingDone = {},
        initialPage = 0
    )
}

@Preview
@Composable
private fun ScreenOnboarding_Previewa() = AppTheme {
    ScreenOnboarding(
        onOnboardingDone = {},
        initialPage = 1
    )
}

@Preview
@Composable
private fun ScreenOnboarding_Preview2() = AppTheme {
    ScreenOnboarding(
        onOnboardingDone = {},
        initialPage = 2
    )
}

@Preview
@Composable
private fun ScreenOnboarding_Preview3() = AppTheme {
    ScreenOnboarding(
        onOnboardingDone = {},
        initialPage = 3
    )
}

private data class Page(
    val title: String,
    val description: String,
    val image: Painter,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenOnboarding(
    onOnboardingDone: () -> Unit,
    initialPage: Int = 0
) {

    Scaffold(
        content = { paddingValues ->
            val onboardPages = listOf(
                Page(
                    stringResource(id = R.string.screen_onboarding_page_habits_title),
                    stringResource(id = R.string.screen_onboarding_page_habits_text),
                    painterResource(id = R.drawable.onboarding_activities)
                ),

                Page(
                    stringResource(id = R.string.screen_onboarding_page_activity_overview_title),
                    stringResource(id = R.string.screen_onboarding_page_activity_overview_text),
                    painterResource(id = R.drawable.onboarding_time_activity)
                ),

                Page(
                    stringResource(id = R.string.screen_onboarding_page_focus_board_title),
                    stringResource(id = R.string.screen_onboarding_page_focus_board_text),
                    painterResource(id = R.drawable.onboarding_focus_board)
                ),

                Page(
                    stringResource(id = R.string.screen_onboarding_page_daily_checklist_title),
                    stringResource(id = R.string.screen_onboarding_page_daily_checklist_text),
                    painterResource(id = R.drawable.onboarding_daily_checklist)
                ),
            )


            val pagerState = rememberPagerState(
                initialPage = initialPage,
                initialPageOffsetFraction = 0f,
                pageCount = { onboardPages.size }
            )

            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                Text(
                    text = stringResource(id = R.string.screen_onboarding_skip),
                    modifier = Modifier
                        .testTag(TestTag.ONBOARDING_SKIP)
                        .fillMaxWidth()
                        .padding(end = 24.dp, top = 16.dp)
                        .align(Alignment.End)
                        .clickable { onOnboardingDone() },
                    textAlign = TextAlign.End,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    PageUI(
                        modifier = Modifier.testTag(TestTag.ONBOARDING_PAGE + page),
                        page = onboardPages[page]
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                        item(key = "item$iteration") {
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(color, CircleShape)
                                    .size(10.dp)
                            )
                        }
                    }
                }


                AnimatedVisibility(visible = pagerState.currentPage == onboardPages.size - 1) {
                    Button(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .testTag(TestTag.ONBOARDING_PAGE_GO_TO_APP)
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                            .padding(horizontal = 8.dp),
                        onClick = { onOnboardingDone() },
                    ) {
                        Text(text = stringResource(id = R.string.screen_onboarding_track))
                    }
                }

            }
        }
    )


}

@Composable
private fun PageUI(modifier: Modifier, page: Page) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = page.title,
            fontSize = 28.sp, fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = page.description,
            textAlign = TextAlign.Center, fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            painter = page.image,
            contentDescription = null
        )
    }
}
