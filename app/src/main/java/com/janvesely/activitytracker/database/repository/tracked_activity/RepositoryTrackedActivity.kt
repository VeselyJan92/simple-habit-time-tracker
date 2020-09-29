package com.janvesely.activitytracker.database.repository.tracked_activity

import androidx.compose.ui.res.stringResource
import androidx.room.withTransaction
import com.janvesely.activitytracker.R
import com.janvesely.activitytracker.database.dao.tracked_activity.*
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.*
import com.janvesely.getitdone.database.AppDatabase
import com.janvesely.activitytracker.database.repository.DBEntityRepository
import com.janvesely.activitytracker.ui.components.BaseMetricData
import com.janvesely.activitytracker.ui.components.Colors
import com.janvesely.activitytracker.ui.components.Editable
import com.janvesely.activitytracker.ui.components.Week
import com.janvesely.activitytracker.ui.screens.activity_list.TrackedActivityWithMetric
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoField

class RepositoryTrackedActivity constructor(
    override val activityDAO: DAOTrackedActivity = AppDatabase.db.activityDAO,
    val completionDAO: DAOTrackedActivityCompletion = AppDatabase.db.completionDAO,
    val scoreDAO: DAOTrackedActivityScore = AppDatabase.db.scoreDAO,
    val sessionDAO: DAOTrackedActivitySession = AppDatabase.db.sessionDAO
) : DBEntityRepository<TrackedActivity>(activityDAO) {


    suspend fun getAllActivities(pastRanges: Int) =  AppDatabase.db.withTransaction {
        return@withTransaction AppDatabase.db.activityDAO.getAllNotInSession().map { activity ->
            val groupedMetric = activity.metric_range.getPastRanges(pastRanges).map {

                if (activity.type != TrackedActivity.Type.COMPLETED){
                    val metric = getMetric(activity.id, activity.type, it.from, it.to)
                    val color =  Colors.getMetricColor(activity.type, activity.expected, activity.metric_range, metric, activity.metric_range)

                    BaseMetricData(activity.type, metric, color, Editable(activity.id, it.from, it.to)){ activity.metric_range.getLabel(it.from)}
                }else{
                    require(activity.metric_range == TimeRange.DAILY)

                    val record = completionDAO.getRecord(activity.id, it.from.toLocalDate())
                    val metric = if (record != null) 1L else 0L
                    val color = if (record != null) Colors.Completed else Colors.NotCompleted
                    val recordId = record?.id ?: 0L

                    BaseMetricData(activity.type, metric, color, Editable(activity.id, it.from, it.to, recordId)){ activity.metric_range.getLabel(it.from)}
                }
            }
            TrackedActivityWithMetric(activity, groupedMetric)
        }
    }


    suspend fun getRecentActivity(activityId: Long, from: LocalDate, to: LocalDate) = AppDatabase.db.withTransaction {
        val activity = activityDAO.getById(activityId)

        var weekIter = to
            .with(ChronoField.DAY_OF_WEEK, 1)
            .atStartOfDay()

        val weeks = arrayListOf<Week>()

        while (from.atStartOfDay() < weekIter ){

            var weekSum = 0L
            val days = ArrayList<BaseMetricData>(7)

            for (day in 0L..6L){
                val from = weekIter.minusDays(day + 1L)
                val to = weekIter.minusDays(day)

                val metric  = getMetric(activityId, activity.type, from, to)

                weekSum += metric

                val data = BaseMetricData(
                    activity.type,
                    metric,
                    Colors.getMetricColor(activity.type, activity.expected, activity.metric_range, metric, TimeRange.DAILY),
                    Editable(activityId, from, to)
                ){ from.dayOfMonth.toString()}

                days.add(data)
            }

            val summary = if (activity.type != TrackedActivity.Type.COMPLETED) {
                val color = when (activity.metric_range) {
                    TimeRange.WEEKLY -> when {
                        weekSum >= activity.expected -> Colors.Completed
                        else -> Colors.NotCompleted
                    }
                    else -> Colors.AppAccent
                }
                BaseMetricData(activity.type, weekSum, color ) { stringResource(id = R.string.frequency_weekly)}
            } else {
                null
            }

            weeks.add(
                Week(weekIter.minusDays(7), weekIter, days, summary)
            )

            weekIter = weekIter.minusDays(7)
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

    suspend fun deleteRecordById(recordId: Long, ){
        sessionDAO.deleteById(recordId)
        scoreDAO.deleteById(recordId)
        completionDAO.deleteById(recordId)
    }


    suspend fun startSession(activityId: Long) = AppDatabase.db.withTransaction {
        activityDAO.update(activityDAO.getById(activityId).copy(in_session_since = LocalDateTime.now()))
    }

    suspend fun commitLiveSession(activityId: Long) = AppDatabase.db.withTransaction {
        val activity = activityDAO.getById(activityId)
        val session = TrackedActivitySession(0, activityId, activity.in_session_since!!, LocalDateTime.now())
        sessionDAO.insert(session)
        activityDAO.update(activity.copy(in_session_since = null))
    }

    suspend fun commitSession(
        activityId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ){
        sessionDAO.insert(TrackedActivitySession(0, activityId, from, to))
    }

    suspend fun commitCompletion(activityId: Long, date: LocalDate) = AppDatabase.db.withTransaction{
        val isCompleted = completionDAO.getMetric(activityId, date.atStartOfDay(), date.atStartOfDay().plusDays(1))

        if (isCompleted == 0L){
            completionDAO.insert(TrackedActivityCompletion(0, activityId, date))
        }
    }

    suspend fun commitScore(activityId: Long, datetime: LocalDateTime,  score: Long){
        scoreDAO.insert(TrackedActivityScore(0, activityId, datetime, score))
    }

    suspend fun getRecords(){

    }

}

