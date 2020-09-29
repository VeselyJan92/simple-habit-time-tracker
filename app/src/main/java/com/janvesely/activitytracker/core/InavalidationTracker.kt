package com.janvesely.activitytracker.core

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.room.InvalidationTracker
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.database.entities.TrackedActivityCompletion
import com.janvesely.activitytracker.database.entities.TrackedActivityScore
import com.janvesely.activitytracker.database.entities.TrackedActivitySession
import kotlinx.coroutines.launch


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
    TrackedActivitySession.TABLE,
    TrackedActivityCompletion.TABLE,
    TrackedActivityScore.TABLE,
    onInvalidated = onInvalidated
)