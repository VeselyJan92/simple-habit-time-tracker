
package com.janvesely.activitytracker.ui.activities

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.TickerMode
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.log

object TrackedActivityDiff : DiffUtil.ItemCallback<TrackedActivity>() {
    override fun areItemsTheSame(old: TrackedActivity, new: TrackedActivity): Boolean {
        return old.id == new.id
    }

    override fun areContentsTheSame(old: TrackedActivity, new: TrackedActivity): Boolean {
        return old == new
    }
}



class ActiveTrackedActivityAdapter(
    val vm: ActivitiesViewModel,
    val context: Context
) : ListAdapter<TrackedActivity, ActiveTrackedActivityAdapter.ViewHolder>(TrackedActivityDiff) {

    val  ticker = ticker(1000, 0, vm.viewModelScope.coroutineContext, TickerMode.FIXED_DELAY)

    val observer = mutableListOf<Tick>()


    init {
        vm.viewModelScope.launch {
            ticker?.consumeEach {
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
            LayoutInflater.from(context).inflate(R.layout.rv_running_activity, parent, false)
        )
    }


    inner class ViewHolder(
        val view: View
    ) : RecyclerView.ViewHolder(view), ItemTouchHelperViewHolder, Tick {


        val act: ImageButton = view.btn_act
        val name: TextView = view.tv_name
        val progress: Chip = view.chip_progress


        override fun onItemSelected() = itemView.setBackgroundColor(Color.parseColor("#BDBDBD"))

        override fun onItemClear() = itemView.setBackgroundColor(Color.parseColor("#f5f5f5"))

        fun setData(item: TrackedActivity){

            progress.setTypeface(null, Typeface.BOLD)
            name.text = item.name

            setTime(item.in_session_since!!)


            act.setOnClickListener {
                vm.stopSession(item)
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


        override fun onTick() {
            val time = try {
                //Log.e("ADAPTER", "TICK: $adapterPosition")
                currentList[adapterPosition].in_session_since
            }catch (e: Exception){
                null
            }

            if (time != null){
                setTime(time)
            }
        }


    }


}
