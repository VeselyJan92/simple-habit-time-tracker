package com.janvesely.activitytracker.ui.activities

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.setContent
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.ui.activities.composable.BaseRow

class ActivityFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                BaseRow()
            }
        }
    }

}



