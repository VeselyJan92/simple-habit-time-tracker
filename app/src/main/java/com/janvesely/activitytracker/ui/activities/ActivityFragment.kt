package com.janvesely.activitytracker.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.core.setDivider
import com.janvesely.activitytracker.ui.other.dragandrop.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_activities.*

class ActivityFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    )  = inflater.inflate(R.layout.fragment_activity, container, false)



}