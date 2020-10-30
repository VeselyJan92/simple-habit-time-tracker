package com.imfibit.activitytracker.ui.screens.upcomming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.TrackerTopAppBar

class UpcommingFragment : Fragment() {


    @ExperimentalFocus
    @ExperimentalFoundationApi
    @OptIn(ExperimentalLayout::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )  = ComposeView(requireContext()).apply {
        setContent {
            Scaffold(
                topBar = { TrackerTopAppBar("") },
                bodyContent = {
                    Box(Modifier.fillMaxWidth().fillMaxHeight(), alignment = Alignment.Center) {
                        Text(text = "UPCOMMING FEATURE", style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ))
                    }
                },
                backgroundColor = Colors.AppBackground
            )
        }
    }

}
