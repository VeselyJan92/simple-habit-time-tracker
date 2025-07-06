package com.imfibit.activitytracker.ui.viewmodels

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.core.navigation.navigate
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.ui.Destinations
import java.time.LocalDate


object RecordNavigatorImpl {

    public fun onDayClicked(nav: NavHostController, activity: TrackedActivity, date: LocalDate){
        if (activity.type != TrackedActivity.Type.CHECKED) {
            nav.navigate(Destinations.DialogActivityDayHistory(activity.id, date))
        }
    }

    public fun onDaylongClicked(
        nav: NavHostController,
        recordViewModel: RecordViewModel,
        activity: TrackedActivity,
        date: LocalDate,
        haptic: HapticFeedback
    ){
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
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                recordViewModel.toggleHabit(activity.id, date.atTime(12, 0))
            }
        }
    }
}