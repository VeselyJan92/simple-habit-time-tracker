package com.imfibit.activitytracker.database.repository.tracked_activity

import android.util.Log
import androidx.room.withTransaction
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.dao.updateAll
import com.imfibit.activitytracker.database.dao.upsertAll
import com.imfibit.activitytracker.database.entities.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryFocusBoard @Inject constructor(
    private val db: AppDatabase
) {

    // Bundle methods
    suspend fun getBundles() = db.focusBundleDAO().getAll()

    suspend fun saveBundle(bundle: FocusBundle, tags: List<FocusBoardItemTag>) = db.withTransaction {
        if (bundle.id == 0L) {
            val id = db.focusBundleDAO().insert(bundle)
            if (tags.isNotEmpty()){
                val tagsToInsert = tags.map { it.copy(bundleId = id) }
                db.focusBoardItemTagDAO().upsertAll(*tagsToInsert.toTypedArray())
            }
            id
        } else {
            db.focusBundleDAO().update(bundle)
            
            val bundleTags = db.focusBoardItemTagDAO().getAllByBundle(bundle.id)
            
            val toDelete = bundleTags.filter { bt -> tags.none { it.id == bt.id } }
            val toUpdate = tags.mapIndexed { index, tag -> tag.copy(position = index, bundleId = bundle.id) }
            
            db.focusBoardItemTagDAO().deleteAll(*toDelete.toTypedArray())
            db.focusBoardItemTagDAO().upsertAll(*toUpdate.toTypedArray())
            bundle.id
        }
    }

    suspend fun createBundle(bundle: FocusBundle, tags: List<FocusBoardItemTag> = emptyList()) = saveBundle(bundle, tags)

    suspend fun updateBundle(bundle: FocusBundle) = db.focusBundleDAO().update(bundle)

    suspend fun updateBundles(bundles: List<FocusBundle>) = db.focusBundleDAO().upsertAll(bundles)

    suspend fun deleteBundle(bundle: FocusBundle) = db.withTransaction {
        db.focusBundleDAO().delete(bundle)
    }


    // Item methods
    suspend fun getFocusItemsWithTags(bundleId: Long) = db.focusBoardItemDAO().getItemsWithTagsByBundle(bundleId)
    
    suspend fun getDashboardItems() = db.focusBoardItemDAO().getDashboardItems()

    suspend fun getFocusItemWithTags(noteId: Long) = db.focusBoardItemDAO().getItemWithTags(noteId)

    suspend fun updateFocusItem(item: FocusBoardItemWithTags) = db.withTransaction {
        Log.e("tag", item.tags.toString())

        db.focusBoardItemDAO().update(item.item)
        db.focusBoardItemTagRelationDAO().updateTags(item)
    }

    suspend fun updateFocusBoardItems(items: List<FocusBoardItem>) = db.focusBoardItemDAO().updateAll(items)

    suspend fun deleteFocusItem(item: FocusBoardItem) = db.withTransaction {
        db.focusBoardItemDAO().delete(item)
        db.focusBoardItemTagRelationDAO().deleteTagsFromFocusItem(item.id)
    }

    suspend fun insertFocusItemWithTags(focusItem: FocusBoardItem, tags: List<FocusBoardItemTag>): Long = db.withTransaction {
        val focusItemId = db.focusBoardItemDAO().insert(focusItem)

        tags.forEachIndexed { index, it ->
            db.focusBoardItemTagRelationDAO()
                .insert(FocusBoardItemTagRelation(focusItemId, it.id, index == 0))
        }
        
        focusItemId
    }

    suspend fun toggleFocusItemCompletion(itemId: Long) = db.withTransaction {
        val item = db.focusBoardItemDAO().getItemWithTags(itemId)?.item ?: return@withTransaction
        val currentList = db.focusBoardItemDAO().getAllByBundle(item.bundleId).toMutableList()
        
        val isCompleted = !item.isCompleted
        
        val index = currentList.indexOfFirst { it.id == itemId }
        if (index == -1) return@withTransaction
        
        val toggledItem = currentList.removeAt(index)
        val updatedItem = toggledItem.copy(
            isCompleted = isCompleted,
            completedAt = if (isCompleted) System.currentTimeMillis() else null
        )
        
        fun getSubsetOrder(isPinned: Boolean, isCompleted: Boolean): Int {
            if (isCompleted) return 2
            if (isPinned) return 0
            return 1
        }
        
        val targetOrder = getSubsetOrder(updatedItem.isPinned, updatedItem.isCompleted)
        val insertIndex = currentList.indexOfFirst { 
            getSubsetOrder(it.isPinned, it.isCompleted) >= targetOrder 
        }
        
        if (insertIndex != -1) {
            currentList.add(insertIndex, updatedItem)
        } else {
            currentList.add(updatedItem)
        }
        
        val toUpdate = currentList.mapIndexed { i, it -> it.copy(bundlePosition = i) }
        
        db.focusBoardItemDAO().updateAll(toUpdate)
    }
}
