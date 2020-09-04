
package com.janvesely.activitytracker.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.core.setDivider
import com.janvesely.activitytracker.ui.activities.composable.TrackedActivitiesList
import com.janvesely.activitytracker.ui.other.dragandrop.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_activities.*

class ActivitiesFragment : Fragment() {

    private val vm by viewModels<ActivitiesViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    )  = ComposeView(requireContext()).apply {
        setContent {
            TrackedActivitiesList(vm.activities)
        }
    }

 /*   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val drag =  object : ItemTouchHelper(SimpleItemTouchHelperCallback(vm)){


        }.apply {
            attachToRecyclerView(rv_activities)
        }


        val activities = TrackedActivityAdapter(vm, requireContext(), drag, findNavController())
        rv_activities.adapter = activities
        rv_activities.layoutManager = LinearLayoutManager(requireContext())

        val active_activities = ActiveTrackedActivityAdapter(vm, requireContext())
        active_tasks.adapter = active_activities
        active_tasks.setDivider(R.drawable.active_task_divider)
        active_tasks.layoutManager = LinearLayoutManager(requireContext())


        vm.activities.observe(viewLifecycleOwner, Observer{
            activities.submitList(it)
            Log.e("xxx", "RENDER")
        })

        vm.live.observe(viewLifecycleOwner, Observer {
            active_activities.submitList(it)
        })


    }*/

}

