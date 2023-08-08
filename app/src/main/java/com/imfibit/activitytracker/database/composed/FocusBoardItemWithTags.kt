package com.imfibit.activitytracker.database.composed

import androidx.compose.runtime.Stable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.entities.FocusBoardItemTagRelation

@Stable
data class FocusBoardItemWithTags(
    @Embedded val item: FocusBoardItem,

    @Relation(
        parentColumn = "focus_board_item_id",
        entityColumn = "focus_board_item_tag_id",
        associateBy = Junction(FocusBoardItemTagRelation::class),
    )
    val tags: List<FocusBoardItemTag>

){
    fun getMainTag() = tags.firstOrNull()
}