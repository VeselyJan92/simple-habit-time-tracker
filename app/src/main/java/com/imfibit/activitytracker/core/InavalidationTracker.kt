package com.imfibit.activitytracker.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.InvalidationTracker
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


inline fun createInvalidationTacker(
    vararg tables: String,
    crossinline onInvalidated: (Set<String>)->Unit
): InvalidationTracker.Observer {
    return object : InvalidationTracker.Observer(tables) {

        override fun onInvalidated(tables: Set<String>) =  onInvalidated(tables)
    }
}

fun createActivityInvalidationTracker(onInvalidated: (Set<String>)->Unit) = createInvalidationTacker(
    TrackedActivity.TABLE,
    TrackedActivityTime.TABLE,
    TrackedActivityCompletion.TABLE,
    TrackedActivityScore.TABLE,
    PresetTimer.TABLE,
    TrackerActivityGroup.TABLE,
    onInvalidated = onInvalidated
)

fun  <T> ViewModel.activityInvalidationTracker(db: AppDatabase, source: ()->T){


    val tracker = createActivityInvalidationTracker {
        source.invoke()
    }

    db.invalidationTracker.addObserver(tracker)


    this.addCloseable {
        db.invalidationTracker.removeObserver(tracker)
    }
}


fun  <T> ViewModel.invalidationFlow(db: AppDatabase, source: suspend ()->T) = callbackFlow {

    viewModelScope.launch(Dispatchers.IO) {
        trySend(source.invoke())
    }

    val tracker = createActivityInvalidationTracker {
        viewModelScope.launch(Dispatchers.IO) {
            trySend(source.invoke())
        }
    }

    db.invalidationTracker.addObserver(tracker)

    awaitClose {
        db.invalidationTracker.removeObserver(tracker)
    }

}



fun  <T> ViewModel.invalidationFlow(db: AppDatabase, vararg tables: String, source: suspend ()->T) = callbackFlow {
    viewModelScope.launch(Dispatchers.IO) {
        trySend(source.invoke())
    }

    val tracker = createInvalidationTacker(tables = tables){
        viewModelScope.launch(Dispatchers.IO) {
            trySend(source.invoke())
        }
    }

    db.invalidationTracker.addObserver(tracker)

    awaitClose {
        db.invalidationTracker.removeObserver(tracker)
    }

}
