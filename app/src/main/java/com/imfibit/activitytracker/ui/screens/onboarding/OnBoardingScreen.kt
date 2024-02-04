package com.imfibit.activitytracker.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.dataStore
import com.imfibit.activitytracker.ui.SCREEN_ACTIVITIES
import com.imfibit.activitytracker.ui.components.Colors
import kotlinx.coroutines.runBlocking


private data class Page(
    val title: String,
    val description: String,
    val image: Painter
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenOnboarding(
    nav: NavHostController,
    scaffoldState: ScaffoldState
) {
    val context = LocalContext.current

    fun cancel() = runBlocking {
        context.dataStore.edit { settings -> settings[PreferencesKeys.ONBOARDING_COMPLETED] = true }
        nav.navigate(SCREEN_ACTIVITIES)
    }

    val onboardPages = listOf(
        Page(
            stringResource(id = R.string.screen_onboarding_page_1_title),
            stringResource(id = R.string.screen_onboarding_page_1_text),
            painterResource(id = R.drawable.onboarding_activities)
        ),

        Page(
            stringResource(id = R.string.screen_onboarding_page_2_title),
            stringResource(id = R.string.screen_onboarding_page_2_text),
            painterResource(id = R.drawable.onboarding_focus_board)
        ),

        Page(
            stringResource(id = R.string.screen_onboarding_page_3_title),
            stringResource(id = R.string.screen_onboarding_page_3_text),
            painterResource(id = R.drawable.onboarding_time_activity)
        )
    )


    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = {onboardPages.size}
    )

    Column() {

        Text(text = stringResource(id = R.string.screen_onboarding_skip),modifier = Modifier
            .fillMaxWidth()
            .padding(end = 24.dp, top = 16.dp)
            .align(Alignment.End)
            .clickable { cancel() },
            textAlign = TextAlign.End,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )

        HorizontalPager(state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            page -> PageUI(page = onboardPages[page])
        }

        LazyRow(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                item(key = "item$iteration"){
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(color, CircleShape)
                            .size(10.dp)
                    )
                }
            }
        }


        AnimatedVisibility(visible = pagerState.currentPage == onboardPages.size - 1 ) {
            Button(shape = RoundedCornerShape(20.dp) ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .padding(horizontal = 8.dp),
                onClick = { cancel() },
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Colors.ButtonX,
                    contentColor = Color.Black
                )
            ) {
                Text(text = stringResource(id = R.string.screen_onboarding_track))
            }
        }

    }
}


@Composable
private fun PageUI(page: Page) {
    Column(
        modifier = Modifier
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
            textAlign = TextAlign.Center,fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            painter = page.image,
            contentDescription =  null
        )
    }
}
