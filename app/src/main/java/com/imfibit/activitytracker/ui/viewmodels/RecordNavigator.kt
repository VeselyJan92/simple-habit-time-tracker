package com.imfibit.activitytracker.ui.viewmodels

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.ui.AppDestination
import com.imfibit.activitytracker.ui.Destinations
import java.time.LocalDate


object RecordNavigatorImpl {

    public fun onDayClicked(navigate: (AppDestination) -> Unit, activity: TrackedActivity, date: LocalDate){
        if (activity.type != TrackedActivity.Type.CHECKED) {
            navigate(Destinations.DialogActivityDayHistory(activity.id, date))
        }
    }

    public fun onDaylongClicked(
        navigate: (AppDestination) -> Unit,
        recordViewModel: RecordViewModel,
        activity: TrackedActivity,
        date: LocalDate,
        haptic: HapticFeedback
    ){
        when (activity.type) {
            TrackedActivity.Type.TIME ->{
                navigate(
                    Destinations.DialogEditRecord(
                        TrackedActivityTime(activity_id = activity.id, datetime_start = date.atTime(12, 0), datetime_end = date.atTime(12, 0))
                    )
                )
            }
            TrackedActivity.Type.SCORE -> {
                navigate(
                    Destinations.DialogEditRecord(
                         TrackedActivityScore(activity_id = activity.id, datetime_completed = date.atTime(12, 0), score = 1)
                    )
                )
            }
            TrackedActivity.Type.CHECKED -> {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                recordViewModel.toggleHabit(activity.id, date.atTime(12, 0))
            }
        }
    }
}