package com.imfibit.activitytracker.ui.viewmodels

import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.core.extensions.navigate
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import java.time.LocalDate


object RecordNavigatorImpl {

    public fun onDayClicked(nav: NavHostController, activity: TrackedActivity, date: LocalDate){
        if (activity.type != TrackedActivity.Type.CHECKED) {
            nav.navigate("screen_day_history/${activity.id}/${date}")
        }
    }

    public fun onDaylongClicked(nav: NavHostController, recordViewModel: RecordViewModel, activity: TrackedActivity, date: LocalDate){
        when (activity.type) {
            TrackedActivity.Type.TIME ->{
                nav.navigate(
                    "dialog_edit_record/{record}",
                    bundleOf("record" to TrackedActivityTime(activity_id = activity.id, datetime_start = date.atTime(12, 0), datetime_end = date.atTime(12, 0)))
                )
            }
            TrackedActivity.Type.SCORE -> {
                nav.navigate(
                    "dialog_edit_record/{record}",
                    bundleOf("record" to TrackedActivityScore(activity_id = activity.id, datetime_completed = date.atTime(12, 0), score = 1))
                )
            }
            TrackedActivity.Type.CHECKED -> {
                recordViewModel.toggleHabit(activity.id, date.atTime(12, 0))
            }
        }
    }
}