package com.imfibit.activitytracker.database.composed

import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord

data class RecordWithActivity(

    val activity: TrackedActivity,

    val record: TrackedActivityRecord

)