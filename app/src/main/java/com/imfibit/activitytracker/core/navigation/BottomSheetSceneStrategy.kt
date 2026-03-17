package com.imfibit.activitytracker.core.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

public class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    public override fun SceneStrategyScope<T>.calculateScene(
        entries: List<NavEntry<T>>
    ): Scene<T>? {
        val lastEntry = entries.lastOrNull()
        val isBottomSheet = lastEntry?.metadata?.get(BottomSheetKey) == true

        return if (isBottomSheet) {
            val stack = entries.dropLast(1)
            BottomSheetScene(
                key = lastEntry.contentKey,
                entry = lastEntry,
                previousEntries = stack, // This list MUST contain the screen beneath
                overlaidEntries = stack,
            )
        } else null
    }

    public companion object {
        public object BottomSheetKey : NavMetadataKey<Boolean>

        /**
         * Helper to mark an entry as a bottom sheet in metadata.
         */
        public fun bottomSheet(): Map<String, Any> = metadata { put(BottomSheetKey, true) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
internal class BottomSheetScene<T : Any>(
    override val key: Any,
    private val entry: NavEntry<T>,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        entry.Content()
    }
}