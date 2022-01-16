package com.imfibit.activitytracker.core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.InvalidationTracker
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Flow


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
    TrackerActivityGroup.TABLE,
    onInvalidated = onInvalidated
)


fun  <T> ViewModel.invalidationFlow(db: AppDatabase, source: suspend ()->T) = callbackFlow {

    viewModelScope.launch(Dispatchers.IO) {
        trySend(source.invoke())
    }

    val tracker = activityInvalidationTracker {
        viewModelScope.launch(Dispatchers.IO) {
            trySend(source.invoke())
        }
    }

    db.invalidationTracker.addObserver(tracker)

    awaitClose {
        db.invalidationTracker.removeObserver(tracker)
    }

}