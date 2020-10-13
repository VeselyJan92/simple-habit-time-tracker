package com.imfibit.activitytracker.database.repository.tracked_activity

import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.room.withTransaction
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.ComposeString
import com.imfibit.activitytracker.core.sumByLong
import com.imfibit.activitytracker.database.dao.tracked_activity.*
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.getitdone.database.AppDatabase
import com.imfibit.activitytracker.database.repository.DBEntityRepository
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityWithMetric
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoField

class RepositoryTrackedActivity constructor(
    override val activityDAO: DAOTrackedActivity = AppDatabase.db.activityDAO,
    val completionDAO: DAOTrackedActivityChecked = AppDatabase.db.completionDAO,
    val scoreDAO: DAOTrackedActivityScore = AppDatabase.db.scoreDAO,
    val sessionDAO: DAOTrackedActivitySession = AppDatabase.db.sessionDAO,
    val metricDAO: DAOTrackedActivityMetric = AppDatabase.db.metricDAO
) : DBEntityRepository<TrackedActivity>(activityDAO) {

    suspend fun getActivitiesOverview(pastRanges: Int) =  AppDatabase.db.withTransaction {
        return@withTransaction AppDatabase.db.activityDAO.getAllNotInSession().map { activity ->

            val data = when(activity.goal.range){
                TimeRange.DAILY -> metricDAO.getMetricByDay(activity.id,LocalDate.now().minusDays(pastRanges.toLong()), LocalDate.now())
                TimeRange.WEEKLY -> metricDAO.getMetricByWeek(activity.id, LocalDate.now().with(ChronoField.DAY_OF_WEEK, 7), pastRanges)
                TimeRange.MONTHLY -> metricDAO.getMetricByMonth(activity.id, YearMonth.now(), pastRanges)
            }

            val groupedMetric = data.map {
                val color = Colors.getMetricColor(
                    activity.goal,
                    it.metric,
                    activity.goal.range,
                    Colors.ChipGray
                )


                val metric: ComposeString = activity.type.getComposeString(
                    it.metric,
                    if (activity.type == TrackedActivity.Type.CHECKED)
                        activity.type.getCheckedFraction(activity.goal.range, it.from)
                    else
                        null
                )


                MetricWidgetData.Labeled(
                    { activity.goal.range.getLabel(it.from) },
                    metric,
                    color,
                    Editable(activity.type, it.metric, it.from.atStartOfDay(), it.to.atStartOfDay(), activity.id)
                )
            }
            Log.e("adasd", "Asdasd")
            TrackedActivityWithMetric(activity, groupedMetric)
        }
    }

    suspend fun getRecentActivity(
        activityId: Long,
        firstDayInWeek: LocalDate,
        weeks: Int
    ) = AppDatabase.db.withTransaction {
        val from  = firstDayInWeek.minusWeeks(weeks.toLong())
        val activity = activityDAO.getById(activityId)

        val weeks = arrayListOf<Week>()
        var days: MutableList<MetricWidgetData> = mutableListOf()

        AppDatabase.db.metricDAO.getMetricByDay(activityId, from, firstDayInWeek).forEach {

            if (it.from.dayOfWeek == firstDayInWeek.dayOfWeek && days.isNotEmpty()){

                val sum = days.sumByLong { it.editable!!.metric}

                val metric:ComposeString = if (activity.type == TrackedActivity.Type.CHECKED)
                    {{ "$sum/7" }}
                else
                    activity.type.getComposeString(sum)

                val stat = MetricWidgetData.Labeled(
                    label = { stringResource(R.string.frequency_weekly)},
                    metric = metric,
                    color = if (activity.goal.range == TimeRange.WEEKLY && activity.goal.isSet())
                                if (activity.goal.value <= sum) Colors.Completed else Colors.NotCompleted
                            else
                                Colors.AppAccent
                )

                weeks.add(Week(
                    from = it.from.atStartOfDay(),
                    to = it.from.atStartOfDay().plusWeeks(1L),
                    days = days.reversed(),
                    stat = stat
                ))

                days = mutableListOf()
            }

            val dayMetricData = MetricWidgetData.Labeled(
                label = {it.from.dayOfMonth.toString()},
                metric = {activity.type.format(it.metric)},
                color = Colors.getMetricColor(activity.goal, it.metric, TimeRange.DAILY, Colors.ChipGray),
                editable = Editable(
                    type = activity.type,
                    metric = it.metric,
                    from = it.from.atStartOfDay(),
                    to = it.to.atStartOfDay().plusDays(1L),
                    activityId = activityId
                )

            )
            days.add(dayMetricData)

        }
        Log.e("x", "x")


        return@withTransaction weeks
    }


    suspend fun getMetric(
        activityId: Long,
        type: TrackedActivity.Type,
        from: LocalDateTime,
        to: LocalDateTime
    ) = when(type){
        TrackedActivity.Type.SESSION -> sessionDAO.getMetric(activityId, from, to)
        TrackedActivity.Type.SCORE -> scoreDAO.getMetric(activityId, from, to)
        TrackedActivity.Type.CHECKED -> completionDAO.getMetric(activityId, from, to)
    }



    suspend fun getRecords(
        activityId: Long,
        type: TrackedActivity.Type,
        from: LocalDateTime,
        to: LocalDateTime
    ) = when(type){
        TrackedActivity.Type.SESSION -> sessionDAO.getAll(activityId, from, to)
        TrackedActivity.Type.SCORE -> scoreDAO.getAll(activityId, from, to)
        TrackedActivity.Type.CHECKED -> completionDAO.getAll(activityId, from, to)
    }

    suspend fun deleteRecordById(recordId: Long){
        sessionDAO.deleteById(recordId)
        scoreDAO.deleteById(recordId)
        completionDAO.deleteById(recordId)
    }


    suspend fun startSession(activityId: Long) = AppDatabase.db.withTransaction {
        activityDAO.update(activityDAO.getById(activityId).copy(inSessionSince = LocalDateTime.now()))
    }

    suspend fun commitLiveSession(activityId: Long) = AppDatabase.db.withTransaction {
        val activity = activityDAO.getById(activityId)
        val session = TrackedActivitySession(0, activityId, activity.inSessionSince!!, LocalDateTime.now())
        sessionDAO.insert(session)
        activityDAO.update(activity.copy(inSessionSince = null))
    }

}

