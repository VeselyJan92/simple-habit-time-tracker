package com.janvesely.activitytracker.database.repository.tracked_activity

import androidx.compose.ui.res.stringResource
import androidx.room.withTransaction
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.core.ComposeString
import com.janvesely.activitytracker.core.sumByLong
import com.janvesely.activitytracker.core.iter
import com.janvesely.activitytracker.database.dao.tracked_activity.*
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.*
import com.janvesely.getitdone.database.AppDatabase
import com.janvesely.activitytracker.database.repository.DBEntityRepository
import com.janvesely.activitytracker.ui.components.*
import com.janvesely.activitytracker.ui.screens.activity_list.TrackedActivityWithMetric
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoField

class RepositoryTrackedActivity constructor(
    override val activityDAO: DAOTrackedActivity = AppDatabase.db.activityDAO,
    val completionDAO: DAOTrackedActivityCompletion = AppDatabase.db.completionDAO,
    val scoreDAO: DAOTrackedActivityScore = AppDatabase.db.scoreDAO,
    val sessionDAO: DAOTrackedActivitySession = AppDatabase.db.sessionDAO
) : DBEntityRepository<TrackedActivity>(activityDAO) {

    suspend fun getActivitiesOverview(pastRanges: Int) =  AppDatabase.db.withTransaction {
        return@withTransaction AppDatabase.db.activityDAO.getAllNotInSession().map { activity ->
            val groupedMetric = activity.goalRange.getPastRanges(pastRanges).map {

                val label:ComposeString = { activity.goalRange.getLabel(it.from) }

                if (activity.type != TrackedActivity.Type.COMPLETED){
                    val metric = getMetric(activity.id, activity.type, it.from, it.to)
                    val color =  Colors.getMetricColor(activity.type, activity.goalValue, activity.goalRange, metric, activity.goalRange)

                    MetricWidgetData.Labeled(
                        label,
                        { activity.type.format(metric) },
                        color,
                        Editable(activity.type, metric, it.from, it.to, activity.id)
                    )
                }else{

                    val record = completionDAO.getRecord(activity.id, it.from.toLocalDate())
                    val metric = if (record != null) 1L else 0L
                    val color = if (record != null) Colors.Completed else Colors.NotCompleted
                    val recordId = record?.id ?: 0L

                    MetricWidgetData.Labeled(
                        label,
                        { activity.type.format(metric) },
                        color,
                        Editable(activity.type, metric, it.from, it.to, activity.id,recordId)
                    )
                }
            }
            TrackedActivityWithMetric(activity, groupedMetric)
        }
    }


    suspend fun getRecentActivity(activityId: Long, start: LocalDate, end: LocalDate) = AppDatabase.db.withTransaction {
        val start =  start.with(ChronoField.DAY_OF_WEEK, 7)
        val end = end.with(ChronoField.DAY_OF_WEEK, 1)

        val activity = activityDAO.getById(activityId)

        val data  = hashMapOf<LocalDate, Long>()

        getMetricPerDay(activityId, activity.type,  end, start).forEach {
            data[it.date] = it.metric
        }

        val weeks = arrayListOf<Week>()
        var days: MutableList<MetricWidgetData> = mutableListOf()

        (start iter end).forEach {
            val metric = data[it] ?: 0L

            val dayMetricData = MetricWidgetData.Labeled(
                label = {it.dayOfMonth.toString()},
                metric = {activity.type.format(metric)},
                color = Colors.getMetricColor(activity.type, activity.goalValue, activity.goalRange, metric, TimeRange.DAILY),
                editable = Editable(
                    type = activity.type,
                    metric = metric,
                    from = it.atStartOfDay(),
                    to = it.atStartOfDay().plusDays(1L),
                    activityId = activityId
                )

            )
            days.add(dayMetricData)

            if (it.dayOfWeek == DayOfWeek.MONDAY){

                val sum = days.sumByLong { it.editable!!.metric}

                val metric:ComposeString = if (activity.type == TrackedActivity.Type.COMPLETED)
                    {{ "$sum/7" }}
                else
                    {{ activity.type.format(sum) }}

                val color = if (activity.goalRange == TimeRange.WEEKLY)
                    if(sum >= activity.goalValue ) Colors.Completed else Colors.NotCompleted
                else
                    Colors.AppAccent

                val stat = MetricWidgetData.Labeled(
                    label = { stringResource(R.string.frequency_weekly)},
                    metric = metric,
                    color = color
                )

                weeks.add(Week(
                    from = it.atStartOfDay(),
                    to = it.atStartOfDay().plusWeeks(1L),
                    days = days,
                    stat = stat
                ))

                days = mutableListOf()
            }
        }

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
        TrackedActivity.Type.COMPLETED -> completionDAO.getMetric(activityId, from, to)
    }

    suspend fun getMetricPerDay(
        activityId: Long,
        type: TrackedActivity.Type,
        from: LocalDate,
        to: LocalDate
    ) = when(type){
        TrackedActivity.Type.SESSION -> sessionDAO.getMetricPerDay(activityId, from, to)
        TrackedActivity.Type.SCORE -> scoreDAO.getMetricPerDay(activityId, from, to)
        TrackedActivity.Type.COMPLETED -> completionDAO.getMetricPerDay(activityId, from, to)
    }

    suspend fun getRecords(
        activityId: Long,
        type: TrackedActivity.Type,
        from: LocalDateTime,
        to: LocalDateTime
    ) = when(type){
        TrackedActivity.Type.SESSION -> sessionDAO.getAll(activityId, from, to)
        TrackedActivity.Type.SCORE -> scoreDAO.getAll(activityId, from, to)
        TrackedActivity.Type.COMPLETED -> completionDAO.getAll(activityId, from, to)
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

