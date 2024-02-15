package com.imfibit.activitytracker.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.imfibit.activitytracker.TestCookBook
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OnboardingTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    val cookBook = TestCookBook(composeTestRule)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun swipe_through_pages() {
        composeTestRule.onNodeWithTag(TestTag.ONBOARDING_PAGE + 0).assertExists().performTouchInput { swipeLeft() }

        var page = 1;
        while (!composeTestRule.onNodeWithTag(TestTag.ONBOARDING_PAGE_GO_TO_APP).isDisplayed()){
            composeTestRule.onNodeWithTag(TestTag.ONBOARDING_PAGE + page).assertExists().performTouchInput { swipeLeft() }
            page ++
        }

        composeTestRule.onNodeWithTag(TestTag.ONBOARDING_PAGE + page).assertExists()

        composeTestRule.onNodeWithTag(TestTag.ONBOARDING_PAGE_GO_TO_APP).assertExists().performClick();
        composeTestRule.onNodeWithTag(TestTag.DASHBOARD_ACTIVITIES_CONTENT).assertIsDisplayed()
    }

    @Test
    fun skip_pages() {
        composeTestRule.onNodeWithTag(TestTag.ONBOARDING_PAGE + 0).assertExists()
        cookBook.skipOnboarding()
        composeTestRule.onNodeWithTag(TestTag.DASHBOARD_ACTIVITIES_CONTENT).assertIsDisplayed()
    }
}