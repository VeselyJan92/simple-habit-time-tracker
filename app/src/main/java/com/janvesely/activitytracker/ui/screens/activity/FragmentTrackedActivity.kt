
package com.janvesely.activitytracker.ui.screens.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs


class FragmentTrackedActivity : Fragment() {




    private val vm by viewModels<TrackedActivityViewModel>{
        TrackedActivityVMFactory(requireArguments().getLong("tracked_activity_id"))
    }

    @ExperimentalFocus
    @ExperimentalFoundationApi
    @OptIn(ExperimentalLayout::class)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    )  = ComposeView(requireContext()).apply {
        setContent {
            TrackedActivityScreen(findNavController(), vm)
        }
    }



}

