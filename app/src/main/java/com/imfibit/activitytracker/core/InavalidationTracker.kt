package com.imfibit.activitytracker.core

import androidx.room.InvalidationTracker
import com.imfibit.activitytracker.database.entities.*


inline fun createInvalidationTacker(
    vararg tables: String,
    crossinline onInvalidated: (MutableSet<String>)->Unit
): InvalidationTracker.Observer {
    return object : InvalidationTracker.Observer(tables) {
        override fun onInvalidated(tables: MutableSet<String>) = onInvalidated(tables)
    }
}

fun activityInvalidationTracker( onInvalidated: (MutableSet<String>)->Unit) = createInvalidationTacker(
    TrackedActivity.TABLE,
    TrackedActivityTime.TABLE,
    TrackedActivityCompletion.TABLE,
    TrackedActivityScore.TABLE,
    PresetTimer.TABLE,
    onInvalidated = onInvalidated
)