package com.imfibit.activitytracker

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.imfibit.activitytracker.core.TestTag
import com.imfibit.activitytracker.ui.MainActivity


typealias ComposeRule = AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

class TestCookBook(private val composeTestRule: ComposeRule) {

    fun skipOnboarding(){
        composeTestRule.onNodeWithTag(TestTag.ONBOARDING_SKIP).performClick()
    }

    fun openDialogAddActivity(){
        composeTestRule.onNodeWithTag(TestTag.DASHBOARD_ADD_ACTIVITY).performClick()
    }

    fun createActivityTime(){
        composeTestRule.onNodeWithTag(TestTag.DIALOG_ADD_ACTIVITY_TIME).performClick()
    }

    fun createActivityScore(){
        composeTestRule.onNodeWithTag(TestTag.DIALOG_ADD_ACTIVITY_SCORE).performClick()
    }

    fun createActivityCompletion(){
        composeTestRule.onNodeWithTag(TestTag.DIALOG_ADD_ACTIVITY_COMPLETION).performClick()
    }

    fun goBackWithInAppButton(){
        composeTestRule.onNodeWithTag(TestTag.GENERAL_BACK_BUTTON).performClick()
    }


    fun createDailyChecklistItem(){
        composeTestRule.onNodeWithTag(TestTag.DIALOG_ADD_CHECKLIST_ITEM).performClick()
    }


}