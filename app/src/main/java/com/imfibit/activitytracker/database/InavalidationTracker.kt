package com.imfibit.activitytracker.database

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import com.imfibit.activitytracker.database.entities.DailyChecklistItem
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItem
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBoardItemTagRelation
import com.imfibit.activitytracker.database.entities.PresetTimer
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityCompletion
import com.imfibit.activitytracker.database.entities.TrackedActivityScore
import com.imfibit.activitytracker.database.entities.TrackedActivityTime
import com.imfibit.activitytracker.database.entities.TrackerActivityGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

val activityTables = arrayOf(
    TrackedActivity.TABLE,
    TrackedActivityTime.TABLE,
    TrackedActivityCompletion.TABLE,
    TrackedActivityScore.TABLE,
    PresetTimer.TABLE,
    TrackerActivityGroup.TABLE,
)

val focusBoardTables = arrayOf(
    FocusBoardItem.TABLE,
    FocusBoardItemTag.TABLE,
    FocusBoardItemTagRelation.TABLE
)

val dailyChecklistTables = arrayOf(
    DailyChecklistItem.TABLE,
    DailyChecklistTimelineItem.TABLE,
)

inline fun createInvalidationTacker(
    vararg tables: String,
    crossinline onInvalidated: (Set<String>)->Unit
): InvalidationTracker.Observer {
    return object : InvalidationTracker.Observer(tables) {
        override fun onInvalidated(tables: Set<String>) =  onInvalidated(tables)
    }
}

fun  <T> ViewModel.invalidationStateFlow(
    db: AppDatabase,
    initialValue: T,
    vararg tables: String,
    source: suspend () -> T
): MutableStateFlow<T> {
    val state = MutableStateFlow(initialValue)

    viewModelScope.launch(Dispatchers.IO)  {
        state.emit(source())
    }

    viewModelScope.launch(Dispatchers.IO)  {
        observerDBAsFlow(db, *tables)
            .combine(state.subscriptionCount){ change, observers -> observers > 0 }
            .filter { it }
            .collectLatest {
                Log.e("InvalidationTracker", "Query DB")

                state.emit(source())
            }
    }

    return state;
}

fun ViewModel.observerDBAsFlow(
    db: RoomDatabase,
    vararg tables: String
): Flow<Unit> = flow {
    val observerChannel = Channel<Unit>(Channel.CONFLATED)

    val observer = createInvalidationTacker(*tables) {
        observerChannel.trySend(Unit)
    }

    db.invalidationTracker.addObserver(observer)

    addCloseable {
        Log.e("InvalidationTracker", "Close: observerDBAsFlow")

        db.invalidationTracker.addObserver(observer)
        observerChannel.cancel()
    }

    for (signal in observerChannel) {
        Log.e("InvalidationTracker", "Observer DB invalidation")
        emit(Unit)
    }
}

