package com.imfibit.activitytracker.database.repository.tracked_activity

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.room.withTransaction
import com.imfibit.activitytracker.core.ContextString
import com.imfibit.activitytracker.database.dao.tracked_activity.*
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.RecordWithActivity
import com.imfibit.activitytracker.ui.components.*
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.*
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoField
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class RepositoryTrackedActivity @Inject constructor(
    val db: AppDatabase
) {

    data class Day(
        val label: ContextString,
        val metric: Long,
        val color: Color,
        val date: LocalDate,
        val type: TrackedActivity.Type
    )

    data class Week(
        val from: LocalDateTime,
        val to: LocalDateTime,
        val days: List<Day>,
        val total: Long,
    )

    data class Month(
        val weeks:List<Week>,
        val month: YearMonth,
    )

    val completionDAO: DAOTrackedActivityChecked = db.completionDAO
    val activityDAO: DAOTrackedActivity = db.activityDAO
    val scoreDAO: DAOTrackedActivityScore = db.scoreDAO
    val sessionDAO: DAOTrackedActivityTime = db.sessionDAO
    val metricDAO: DAOTrackedActivityMetric = db.metricDAO
    val timers: DAOPresetTimers = db.presetTimersDAO


    suspend fun getActivityOverview(activity: TrackedActivity): TrackedActivityRecentOverview {
        val pastRanges = 4

        val today = metricDAO.getMetricByDay(activity.id, LocalDate.now().minusDays(pastRanges.toLong()), LocalDate.now())

        val data = when(activity.goal.range){
            TimeRange.DAILY -> today
            TimeRange.WEEKLY -> metricDAO.getMetricByWeek(activity.id, LocalDate.now().with(ChronoField.DAY_OF_WEEK, 7), pastRanges+1)
            TimeRange.MONTHLY -> metricDAO.getMetricByMonth(activity.id, YearMonth.now(), pastRanges+1)
        }

        val actionButton = when {
            TrackedActivity.Type.TIME == activity.type && activity.isInSession() -> IN_SESSION
            TrackedActivity.Type.CHECKED == activity.type && metricDAO.getMetricToday(activity.id) > 0 -> CHECKED
            else -> DEFAULT
        }

        val groupedMetric = data.map {
            val color = Colors.getMetricColor(
                activity.goal,
                it.metric,
                activity.goal.range,
                Colors.ChipGray
            )

            val metric = activity.type.getLabel(
                it.metric,
                if (activity.type == TrackedActivity.Type.CHECKED) activity.type.getCheckedFraction(activity.goal.range, it.from) else null
            )

            MetricWidgetData(
                metric,
                color,
                activity.goal.range.getShortLabel(it.from),
            )
        }


        return TrackedActivityRecentOverview(activity, groupedMetric, actionButton, today.last())

    }

    suspend fun getActivitiesOverview(activities: List<TrackedActivity>) =  db.withTransaction {
        return@withTransaction activities.map { activity -> getActivityOverview(activity) }
    }

    suspend fun getMonthData(
        activityId: Long,
        yearMonth: YearMonth
    ) = db.withTransaction {
        Log.e("REP", "START")

        val to  = yearMonth.atDay(1).plusMonths(1).with(ChronoField.DAY_OF_WEEK, 7)
        val from = to.minusMonths(1).withDayOfMonth(1).with(ChronoField.DAY_OF_WEEK, 7).minusDays(6)

        val activity = activityDAO.getById(activityId)

        val weeks =  metricDAO.getMetricByDay(activityId, from, to).chunked(7).map {

            val days = it.map {
                Day(
                    label = {it.from.dayOfMonth.toString()},
                    metric = it.metric,
                    color = Colors.getMetricColor(activity.goal, it.metric, TimeRange.DAILY, Colors.ChipGray),
                    date = it.from,
                    type = activity.type
                )
            }

            val metricSum =  it.map { it.metric }.sum()

            Week(
                from = it.first().from.atStartOfDay(),
                to =  it.last().to.atStartOfDay(),
                days = days,
                total = metricSum,
            )
        }

        return@withTransaction Month(
            weeks = weeks, month = yearMonth
        )
    }

    suspend fun getRecords(
        activityId: Long,
        type: TrackedActivity.Type,
        from: LocalDateTime,
        to: LocalDateTime
    ) = when(type){
        TrackedActivity.Type.TIME -> sessionDAO.getAll(activityId, from, to)
        TrackedActivity.Type.SCORE -> scoreDAO.getAll(activityId, from, to)
        TrackedActivity.Type.CHECKED -> completionDAO.getAll(activityId, from, to)
    }

    suspend fun deleteRecordById(activityId: Long, recordId: Long) = db.withTransaction {
        val activity = activityDAO.flowById(activityId).first()

        when(activity.type) {
            TrackedActivity.Type.TIME -> sessionDAO.deleteById(recordId)
            TrackedActivity.Type.SCORE -> scoreDAO.deleteById(recordId)
            TrackedActivity.Type.CHECKED ->  completionDAO.deleteById(recordId)
        }
    }


    suspend fun getAllRecords(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<TrackedActivityRecord> = listOf(
        sessionDAO.getAll(from, to),
        scoreDAO.getAll(from, to),
        completionDAO.getAll(from.toLocalDate(), to.toLocalDate())
    ).flatten().sortedBy {it.order }



    suspend fun getAllRecordsWithActivity(
        from: LocalDateTime,
        to: LocalDateTime
    ) = db.withTransaction {
        val activities = activityDAO.getAll().map { Pair(it.id, it) }.toMap()

        return@withTransaction getAllRecords(from, to).map {
            RecordWithActivity(
                activities[it.activity_id] ?: error("Activity not found"),
                it
            )
        }
    }




}

