package com.imfibit.activitytracker.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Insights
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.dataStore
import com.imfibit.activitytracker.ui.SCREEN_ACTIVITIES
import com.imfibit.activitytracker.ui.components.Colors
import kotlinx.coroutines.runBlocking


data class Page(
    val title: String,
    val description: String,
    val image: ImageVector
)



@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
fun ScreenOnboarding(
    nav: NavHostController
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
            Icons.Default.Analytics
        ),
        Page(
            stringResource(id = R.string.screen_onboarding_page_2_title),
            stringResource(id = R.string.screen_onboarding_page_2_text),
            Icons.Default.AddTask
        ),
        Page(
            stringResource(id = R.string.screen_onboarding_page_3_title),
            stringResource(id = R.string.screen_onboarding_page_3_text),
            Icons.Default.Insights
        )
    )


    val pagerState = rememberPagerState()

    Column() {

        Text(text = stringResource(id = R.string.screen_onboarding_skip),modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp, top = 16.dp)
            .align(Alignment.End)
            .clickable { cancel() },
            textAlign = TextAlign.End
        )

        HorizontalPager(state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), count = 3 ) { page -> PageUI(page = onboardPages[page]) }

        HorizontalPagerIndicator(pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            activeColor = Color.Black
        )

        AnimatedVisibility(visible = pagerState.currentPage == 2 ) {
            OutlinedButton(shape = RoundedCornerShape(20.dp) ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .padding(horizontal = 8.dp),
                onClick = { cancel() },
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Colors.ButtonGreen,
                    contentColor = Color.White)
            ) {
                Text(text = stringResource(id = R.string.screen_onboarding_track))
            }
        }

    }
}


@Composable
fun PageUI(page: Page) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)) {
        Icon(
            imageVector = page.image,
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = page.title,
            fontSize = 28.sp, fontWeight = FontWeight.Bold,


        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = page.description,
            textAlign = TextAlign.Center,fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))

    }
}
