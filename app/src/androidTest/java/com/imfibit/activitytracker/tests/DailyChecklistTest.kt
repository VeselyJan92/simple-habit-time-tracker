package com.imfibit.activitytracker.tests

import android.os.Build
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.imfibit.activitytracker.TestCookBook
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.core.TestTag.DAILY_CHECKLIST_EMPTY_SECTION
import com.imfibit.activitytracker.core.TestTag.DAILY_CHECKLIST_LIST
import com.imfibit.activitytracker.core.extensions.randomString
import com.imfibit.activitytracker.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DailyChecklistTest {

    val NAME = Random.randomString(12)
    val DESCRIPTION = Random.randomString(12)

    //Prevent notification popup
    @get:Rule
    val permissionRule: GrantPermissionRule =
        if (Build.VERSION.SDK_INT >= 33) {
            GrantPermissionRule.grant(
                "android.permission.POST_NOTIFICATIONS"
            )
        } else {
            GrantPermissionRule.grant()
        }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    val cookBook = TestCookBook(composeTestRule);

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun daily_checklist_item_test_create_update_delete() {
        createDailyChecklistItem()

        composeTestRule.waitUntilExactlyOneExists(hasTestTag(DAILY_CHECKLIST_LIST), 500)

        composeTestRule.onNodeWithText(NAME).performClick()

        val newTitle = Random.randomString(10)
        val newDescription = Random.randomString(10)

        fillEditDialog(newTitle, newDescription)

        saveEditDialog()

        composeTestRule.waitUntilExactlyOneExists(hasText(newTitle), 500)
        composeTestRule.waitUntilExactlyOneExists(hasText(newDescription), 500)

        composeTestRule.onNodeWithText(newTitle).performClick()

        waitForEditDialog()

        composeTestRule.onNodeWithTag(TestTag.DAILY_CHECKLIST_EDIT_DELETE).performClick()

        composeTestRule.waitUntilExactlyOneExists(hasTestTag(DAILY_CHECKLIST_EMPTY_SECTION), 500)
    }

    /*    @OptIn(ExperimentalTestApi::class)
        @Test
        fun xx() {
            createDailyChecklistItem()

            composeTestRule.waitUntilExactlyOneExists(hasTestTag(DAILY_CHECKLIST_LIST_ITEM), 500)


            composeTestRule.onNodeWithTag(DAILY_CHECKLIST_LIST_ITEM).onAncestors().filterToOne(
                hasTestTag(TestTag.CHECKBOX)
            ).performClick()


            composeTestRule.onNodeWithTag(TestTag.DAILY_CHECKLIST_MONTH_GRID_DAY + LocalDate.now().format(
                DateTimeFormatter.ISO_LOCAL_DATE)).assertTextContains("yes")
        }*/

    @OptIn(ExperimentalTestApi::class)
    fun createDailyChecklistItem(
        title: String = NAME,
        description: String = DESCRIPTION,
    ) {
        cookBook.skipOnboarding()
        cookBook.openDialogAddActivity()
        cookBook.createDailyChecklistItem()

        waitForEditDialog()

        fillEditDialog(title, description)

        composeTestRule.waitUntilExactlyOneExists(hasText(title), 500)
        composeTestRule.waitUntilExactlyOneExists(hasText(description), 500)

        saveEditDialog()
    }

    fun fillEditDialog(
        title: String,
        description: String,
    ) {
        composeTestRule.onNodeWithTag(TestTag.DAILY_CHECKLIST_EDIT_TITLE).apply {
            performTextClearance()
            performTextInput(title)
        }
        composeTestRule.onNodeWithTag(TestTag.DAILY_CHECKLIST_EDIT_DESCRIPTION).apply {
            performTextClearance()
            performTextInput(description)
        }
    }

    fun saveEditDialog() {
        composeTestRule.onNodeWithTag(TestTag.DAILY_CHECKLIST_EDIT_CONTINUE).performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    fun waitForEditDialog() {
        composeTestRule.waitUntilExactlyOneExists(
            hasTestTag(TestTag.DAILY_CHECKLIST_EDIT_DIALOG),
            500
        )

    }

}