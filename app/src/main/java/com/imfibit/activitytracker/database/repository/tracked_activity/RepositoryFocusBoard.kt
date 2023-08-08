package com.imfibit.activitytracker.database.repository.tracked_activity

import android.util.Log
import androidx.room.withTransaction
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.dao.tracked_activity.*
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.*
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class RepositoryFocusBoard @Inject constructor(
    val db: AppDatabase
) {

    suspend fun getFocusItemsWithTags() = db.withTransaction {
        db.focusBoardItemDAO().getAll().map {
            FocusBoardItemWithTags(it, db.focusBoardItemTagDAO().getFocusItemTag(it.id))
        }
    }

    suspend fun updateFocusItem(item: FocusBoardItemWithTags) = db.withTransaction{
        Log.e("tag", item.tags.toString())

        db.focusBoardItemDAO().update(item.item)
        db.focusBoardItemTagRelationDAO().updateTags(item)
    }

    suspend fun deleteFocusItem(item: FocusBoardItem) = db.withTransaction{
        db.focusBoardItemDAO().delete(item)
        db.focusBoardItemTagRelationDAO().deleteTagsFromFocusItem(item.id)
    }

    suspend fun insertFocusItemWithTags(focusItem: FocusBoardItem, tags: List<FocusBoardItemTag>){
        val focusItemId = db.focusBoardItemDAO().insert(focusItem)

        tags.forEachIndexed{ index, it ->
            db.focusBoardItemTagRelationDAO().insert(FocusBoardItemTagRelation(focusItemId, it.id, index == 0))
        }

    }

}

