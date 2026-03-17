package com.imfibit.activitytracker.ui.screens.focus_board

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import com.imfibit.activitytracker.core.BaseViewModel
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.FocusBoardItemWithTags
import com.imfibit.activitytracker.database.embedable.Markdown
import com.imfibit.activitytracker.database.entities.FocusBoardItem
import com.imfibit.activitytracker.database.entities.FocusBoardItemTag
import com.imfibit.activitytracker.database.focusBoardTables
import com.imfibit.activitytracker.database.invalidationStateFlow
import com.imfibit.activitytracker.database.repository.tracked_activity.RepositoryFocusBoard
import com.imfibit.activitytracker.ui.components.editor.RichContentEditorState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = ScreenEditFocusBoardItemViewModel.Factory::class)
class ScreenEditFocusBoardItemViewModel @AssistedInject constructor(
    private val db: AppDatabase,
    private val rep: RepositoryFocusBoard,
    @Assisted("bundleId") val bundleId: Long,
    @Assisted("noteId") val noteId: Long
) : BaseViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("bundleId") bundleId: Long, 
            @Assisted("noteId") noteId: Long
        ): ScreenEditFocusBoardItemViewModel
    }

    val titleState = TextFieldState("")
    val contentState = RichContentEditorState("")

    @Volatile
    private var isInitialized = false

    val bundle = invalidationStateFlow(db, null, *focusBoardTables) {
        db.focusBundleDAO().getById(bundleId)
    }

    val tags = invalidationStateFlow(db, listOf(), *focusBoardTables) {
        db.focusBoardItemTagDAO().getAllByBundle(bundleId)
    }

    val item = invalidationStateFlow(db, null, *focusBoardTables) {
        rep.getFocusItemWithTags(noteId)
    }

    init {
        loadInitialData()
        setupAutoSave()
    }

    private fun loadInitialData() = launchIO {
        rep.getFocusItemWithTags(noteId)?.let { initial ->
            titleState.setTextAndPlaceCursorAtEnd(initial.item.title)
            contentState.textFieldState.setTextAndPlaceCursorAtEnd(initial.item.content.value)
            isInitialized = true
        }
    }

    override fun onCleared() {
        if (isInitialized) {
            val title = titleState.text.toString()
            val content = contentState.textFieldState.text.toString()
            launchIO {
                withContext(NonCancellable) {
                    persist(title, content)
                }
            }
        }
        super.onCleared()
    }

    @OptIn(FlowPreview::class)
    private fun setupAutoSave() = launchIO {
        combine(
            snapshotFlow { titleState.text },
            snapshotFlow { contentState.textFieldState.text }
        ) { title, content -> title to content }
            .debounce(1000)
            .collectLatest { (title, content) ->
                persist(title.toString(), content.toString())
            }
    }

    private suspend fun persist(title: String, content: String) {
        if (!isInitialized) return
        val currentTags = item.value?.tags ?: emptyList()
        val currentItem = item.value?.item ?: rep.getFocusItemWithTags(noteId)?.item ?: return

        val itemToUpdate = currentItem.copy(
            title = title,
            content = Markdown(content)
        )
        
        rep.updateFocusItem(
            FocusBoardItemWithTags(
                itemToUpdate,
                currentTags
            )
        )
    }

    fun onToggleTag(tag: FocusBoardItemTag) = launchIO {
        if (!isInitialized) return@launchIO
        val currentTags = item.value?.tags ?: emptyList()
        val currentItem = item.value?.item ?: rep.getFocusItemWithTags(noteId)?.item ?: return@launchIO
        
        val newTags = if (currentTags.any { it.id == tag.id }) {
            currentTags.filter { it.id != tag.id }
        } else {
            currentTags + tag
        }

        val itemToUpdate = currentItem.copy(
            title = titleState.text.toString(),
            content = Markdown(contentState.textFieldState.text.toString())
        )

        rep.updateFocusItem(
            FocusBoardItemWithTags(
                itemToUpdate,
                newTags
            )
        )
    }

    fun onTogglePin() = launchIO {
        item.value?.let { metadata ->
            val newItem = metadata.item.copy(isPinned = !metadata.item.isPinned)
            rep.updateFocusItem(metadata.copy(item = newItem))
        }
    }

    fun onToggleStar() = launchIO {
        item.value?.let { metadata ->
            val newItem = metadata.item.copy(isStarred = !metadata.item.isStarred)
            rep.updateFocusItem(metadata.copy(item = newItem))
        }
    }

    fun onFocusItemDelete() = launchIO {
        rep.deleteFocusItem(FocusBoardItem(id = noteId, bundleId = bundleId, title = ""))
    }
}