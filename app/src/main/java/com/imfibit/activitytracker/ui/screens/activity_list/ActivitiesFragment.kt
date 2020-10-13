
package com.janvesely.activitytracker.ui.screens.activity_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController

class ActivitiesFragment : Fragment() {

    private val vm by viewModels<ActivitiesViewModel>()

    @OptIn(ExperimentalLayout::class)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    )  = ComposeView(requireContext()).apply {
        setContent {
            ActivitiesScreen(findNavController(), vm)
        }
    }

}

