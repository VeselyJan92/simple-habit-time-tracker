
package com.janvesely.activitytracker.ui.screens.records

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class FragmentTrackedActivityRecords : Fragment() {

    @ExperimentalFocus
    @ExperimentalFoundationApi
    @OptIn(ExperimentalLayout::class)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    )  = ComposeView(requireContext()).apply {
        setContent {
            TrackedActivityRecordsScreen()
        }
    }



}

