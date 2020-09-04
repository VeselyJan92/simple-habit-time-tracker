/*

package com.janvesely.activitytracker.ui.activities

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.database.composed.TrackedActivityWithMetric
import com.janvesely.activitytracker.database.composed.TrackedActivityWithMetricDiff
import com.janvesely.activitytracker.database.composed.ViewRangeData
import com.janvesely.activitytracker.database.entities.TrackedActivity
import com.janvesely.activitytracker.ui.other.dragandrop.ItemTouchHelperAdapter
import com.janvesely.activitytracker.ui.other.dragandrop.ItemTouchHelperViewHolder
import kotlinx.android.synthetic.main.rv_activity.view.*
import kotlinx.coroutines.channels.TickerMode
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

interface Tick{
    fun onTick();
}


class TrackedActivityAdapter(
    val vm: ActivitiesViewModel,
    val context: Context,
    val drag: ItemTouchHelper,
    val nav: NavController
) : ListAdapter<TrackedActivityWithMetric, TrackedActivityAdapter.ViewHolder>(
    TrackedActivityWithMetricDiff
), ItemTouchHelperAdapter {

    val  ticker = ticker(1000, 0, vm.viewModelScope.coroutineContext, TickerMode.FIXED_DELAY)

    val observer = mutableListOf<Tick>()


    init {
        vm.viewModelScope.launch {
            ticker?.consumeEach {
               // Log.e("ADAPTER", "TICK XXXXXXXXXXXXXXXXXXXXXXx")
                observer.forEach { it.onTick() }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        observer.add(holder)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        observer.remove(holder)
    }


    operator fun get(position: Int) = super.getItem(position)!!


    override fun onBindViewHolder(h: ViewHolder, p: Int) = h.setData(this[p])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_activity, parent, false)
        )
    }

    override fun onItemDismiss(position: Int) {
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        TODO("move in viewmodel")
    }

    inner class ViewHolder(
        val view: View
    ) : RecyclerView.ViewHolder(view), ItemTouchHelperViewHolder, Tick {


        val act: ImageButton = view.btn_act
        val name: TextView = view.tv_name
        val progress: Chip = view.chip_progress

        val indicator_1: TextView = view.indicator_1
        val indicator_label_1: TextView = view.indicator_label_1

        val indicator_2: TextView = view.indicator_2
        val indicator_label_2: TextView = view.indicator_label_2

        val indicator_3: TextView = view.indicator_3
        val indicator_label_3: TextView = view.indicator_label_3

        val indicator_4: TextView = view.indicator_4
        val indicator_label_4: TextView = view.indicator_label_4

        val indicator_5: TextView = view.indicator_5
        val indicator_label_5: TextView = view.indicator_label_5


    */
/*    val act: ImageButton = TODO()
        val name: TextView = TODO()
        val progress: Chip = TODO()

        val indicator_1: TextView = TODO()
        val indicator_label_1: TextView = TODO()

        val indicator_2: TextView = TODO()
        val indicator_label_2: TextView = TODO()

        val indicator_3: TextView =TODO()
        val indicator_label_3: TextView = TODO()

        val indicator_4: TextView = TODO()
        val indicator_label_4: TextView =TODO()

        val indicator_5: TextView = TODO()
        val indicator_label_5: TextView = TODO()*//*





        override fun onItemSelected() = itemView.setBackgroundColor(Color.parseColor("#BDBDBD"))

        override fun onItemClear() = itemView.setBackgroundColor(Color.parseColor("#f5f5f5"))

        fun setData(item: TrackedActivityWithMetric){
            name.text = item.activity.name

            setIndicators(item)


            if (item.activity.expected == 0){
                progress.text = item.activity.type.format(item.past[0].metric)

                progress.chipIcon = ContextCompat.getDrawable(context,R.drawable.ic_baseline_assessment_24)
            }
            else{
                progress.text = if (item.activity.type != TrackedActivity.Type.COMPLETED)
                    "${item.activity.type.format(item.past[0].metric)} / ${item.activity.formatGoal()}"
                else
                    item.activity.type.format(item.past[0].metric)

                if (item.completed)
                    progress.chipIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_assignment_turned_in_24)
                else
                    progress.chipIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_assignment_late_24)

            }


            when(item.activity.type){
                TrackedActivity.Type.SESSION ->{

                    if (item.activity.in_session_since == null){
                        act.setBackgroundResource(R.drawable.ic_play_circle_filled_green_45dp)
                        progress.setTypeface(null, Typeface.NORMAL)

                    } else{
                        act.setBackgroundResource(R.drawable.ic_baseline_pause_circle_filled_24)

                        setTime(item.activity.in_session_since!!)


                        progress.setTypeface(null, Typeface.BOLD)
                    }

                }
                TrackedActivity.Type.SCORE ->{
                    act.setBackgroundResource(R.drawable.ic_acitivity_add_circle_40dp)
                }
                TrackedActivity.Type.COMPLETED -> {
                    if (!item.completed)
                        act.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24)
                    else
                        act.setBackgroundResource(R.drawable.ic_activity_check_circle_outline_40dp)
                }
            }

            act.setOnClickListener {
                when(item.activity.type){
                    TrackedActivity.Type.SESSION -> {
                        if (item.activity.in_session_since == null)
                            vm.startSession(item.activity.copy())
                        else
                            vm.stopSession(item.activity.copy())
                    }
                    TrackedActivity.Type.SCORE -> TODO()
                    TrackedActivity.Type.COMPLETED -> {
                       */
/* if ()

                        vm.completeTask()*//*

                    }
                }
            }


            //Set up Drag and Drop listener
            view.setOnLongClickListener {
                drag.startDrag(this)
                true
            }

            view.setOnClickListener {
                //FragmentBaseTrackedTask.navigate(navController, item.task.id)
                nav.navigate(R.id.action_navigation_dashboard_to_activityFragment)
            }
        }

        fun setTime(start: LocalDateTime){
            val time = Duration.between(start, LocalDateTime.now()).seconds

            val h = (time / 3600).toInt()
            val m = (time - h * 3600).toInt() / 60
            val s = (time - h * 3600 - m * 60).toInt() / 1
            val t = (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s

            progress.text = t
        }


        fun setIndicators(item: TrackedActivityWithMetric){

            fun setIndicator(indicator: TextView, label:TextView, data: ViewRangeData, current: Boolean = false){
                val drawable = if (data.metric >= item.activity.expected)
                    R.drawable.dr_activity_metric_indicator_completed
                else
                    R.drawable.dr_activity_metric_indicator_pending

                indicator.background = ContextCompat.getDrawable(context, drawable).apply {
                    if (current)
                        (this as GradientDrawable).setStroke(5, Color.BLACK)
                    else
                        (this as GradientDrawable).setStroke(0, null)
                }

                indicator.text = item.activity.type.format(data.metric)
                label.text = data.getLabel(context)
            }

            setIndicator(indicator_1, indicator_label_1, item.past[4])
            setIndicator(indicator_2, indicator_label_2, item.past[3])
            setIndicator(indicator_3, indicator_label_3, item.past[2])
            setIndicator(indicator_4, indicator_label_4, item.past[1])
            setIndicator(indicator_5, indicator_label_5, item.past[0], true)
        }

        override fun onTick() {
            val time = try {
                //Log.e("ADAPTER", "TICK: $adapterPosition")
                currentList[adapterPosition].activity.in_session_since
            }catch (e: Exception){
                null
            }

            if (time != null){
                setTime(time)
            }
        }


    }


}
*/
