package com.imfibit.activitytracker.tests

import android.os.Build
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.imfibit.activitytracker.TestCookBook
import com.imfibit.activitytracker.core.AppSettings
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.core.extensions.randomString
import com.imfibit.activitytracker.ui.MainActivity
import com.imfibit.activitytracker.ui.components.Colors
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.random.Random

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TrackedActivityTimeTest {

    //Prevent notification popup
    @get:Rule
    val permissionRule: GrantPermissionRule =
        if (Build.VERSION.SDK_INT >= 33) {
            GrantPermissionRule.grant(
                "android.permission.POST_NOTIFICATIONS"
            )
        }else{
            GrantPermissionRule.grant()
        }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    val cookBook = TestCookBook(composeTestRule);

    @Test
    fun updateName() {
        createActivity()

        val activityName = composeTestRule.onNodeWithTag(TestTag.TRACKED_ACTIVITY_EDIT_NAME)

        activityName.assertExists()

        activityName.performTextClearance()

        val newName = Random.randomString(12)
        activityName.performTextInput(newName)

        cookBook.goBackWithInAppButton()

        composeTestRule.onNodeWithText(newName).assertExists()
    }


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun startSession() {
        createActivity()

        composeTestRule.onNodeWithTag(TestTag.TRACKED_ACTIVITY_TIME_CLOSE_SESSION).assertDoesNotExist()

        val actionButton = composeTestRule.onNodeWithTag(TestTag.TRACKED_ACTIVITY_ACTION_BUTTON)

        actionButton.performClick()

        composeTestRule.waitUntilExactlyOneExists(hasTestTag(TestTag.TRACKED_ACTIVITY_TIME_CLOSE_SESSION), 500)

        actionButton.performClick()

        composeTestRule.waitUntilDoesNotExist(hasTestTag(TestTag.TRACKED_ACTIVITY_TIME_CLOSE_SESSION), 500)

        composeTestRule.onNodeWithTag(TestTag.MONTH_GRID_TODAY).performClick()

        composeTestRule.onAllNodesWithTag(TestTag.ACTIVITY_RECORD).assertCountEquals(1)
    }


    fun createActivity(){
        cookBook.skipOnboarding()
        cookBook.openDialogAddActivity()
        cookBook.createActivityTime()
        composeTestRule.onNodeWithTag(TestTag.TRACKED_ACTIVITY_SCREEN)
    }



}