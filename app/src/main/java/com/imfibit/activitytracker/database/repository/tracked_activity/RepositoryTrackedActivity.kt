package com.imfibit.activitytracker.database.repository.tracked_activity

import androidx.compose.ui.graphics.Color
import androidx.room.withTransaction
import com.imfibit.activitytracker.core.ContextString
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.RecordWithActivity
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOPresetTimers
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOTrackedActivity
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOTrackedActivityChecked
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOTrackedActivityMetric
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOTrackedActivityScore
import com.imfibit.activitytracker.database.dao.tracked_activity.DAOTrackedActivityTime
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.database.entities.TrackedActivityRecord
import com.imfibit.activitytracker.ui.components.Colors
import com.imfibit.activitytracker.ui.components.MetricWidgetData
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.CHECKED
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.DEFAULT
import com.imfibit.activitytracker.ui.screens.activity_list.TrackedActivityRecentOverview.ActionButton.IN_SESSION
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.YearMonth
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
        val date: LocalDate
    )

    data class Week(
        val from: LocalDate,
        val to: LocalDate,
        val days: List<Day>,
        val total: Long,
    )

    data class Month(
        val weeks:List<Week>,
        val year: Int,
        val month: Int,
    )

    val completionDAO: DAOTrackedActivityChecked = db.completionDAO()
    val activityDAO: DAOTrackedActivity = db.activityDAO()
    val scoreDAO: DAOTrackedActivityScore = db.scoreDAO()
    val sessionDAO: DAOTrackedActivityTime = db.sessionDAO()
    val metricDAO: DAOTrackedActivityMetric = db.metricDAO()
    val timers: DAOPresetTimers = db.presetTimersDAO()


    suspend fun getActivityOverview(activity: TrackedActivity): TrackedActivityRecentOverview {
        val pastRanges = 10
        val todayDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val today = metricDAO.getMetricByDay(activity.id, todayDate.minus(pastRanges - 1, DateTimeUnit.DAY), todayDate)

        val data = when(activity.goal.range){
            TimeRange.DAILY -> today
            TimeRange.WEEKLY -> {
                var endOfWeek = todayDate
                // 1 = Monday, 7 = Sunday. DayOfWeek.value in JDK is 1-7
                // kotlinx.datetime.DayOfWeek is enum. ordinal 0-6.
                // We use ordinal + 1 to get 1-7 (Mon-Sun).
                while ((endOfWeek.dayOfWeek.ordinal + 1) != 7) {
                    endOfWeek = endOfWeek.plus(1, DateTimeUnit.DAY)
                }
                metricDAO.getMetricByWeek(activity.id, endOfWeek, pastRanges)
            }
            TimeRange.MONTHLY -> metricDAO.getMetricByMonth(activity.id, todayDate.year, todayDate.monthNumber, pastRanges)
        }

        val actionButton = when {
            TrackedActivity.Type.TIME == activity.type && activity.isInSession() -> IN_SESSION
            TrackedActivity.Type.CHECKED == activity.type && metricDAO.getMetricToday(activity.id) > 0 -> CHECKED
            else -> DEFAULT
        }

        val groupedMetric = data.reversed().map {
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

        val challengeMetric = getChallengeMetric(activity.id, activity.challenge.from, activity.challenge.to)


        return TrackedActivityRecentOverview(activity, challengeMetric, groupedMetric, actionButton, today.last())

    }

    suspend fun getChallengeMetric(activityId: Long, from: LocalDate?, to: LocalDate?) = metricDAO.getMetric(activityId, from ?: LocalDate(2000, 1, 1), to ?: LocalDate(2100, 1, 1))

    suspend fun getActivitiesOverview(activities: List<TrackedActivity>) =  db.withTransaction {
        return@withTransaction activities.map { activity -> getActivityOverview(activity) }
    }

    suspend fun getMonthData(
        activityId: Long,
        yearMonth: YearMonth
    ): Month = getMonthData(activityId, yearMonth.year, yearMonth.monthValue)

    suspend fun getMonthData(
        activityId: Long,
        year: Int,
        month: Int
    ) = db.withTransaction {
        val firstOfMonth = LocalDate(year, month, 1)

        var start = firstOfMonth
        // 1 = Monday. ordinal 0
        while((start.dayOfWeek.ordinal + 1) != 1) { 
            start = start.minus(1, DateTimeUnit.DAY)
        }

        val endOfMonth = firstOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
        var end = endOfMonth
        // 7 = Sunday. ordinal 6
        while((end.dayOfWeek.ordinal + 1) != 7) { 
            end = end.plus(1, DateTimeUnit.DAY)
        }

        val activity = activityDAO.getById(activityId)

        val weeks =  metricDAO.getMetricByDay(activityId, start, end).chunked(7).map {

            val days = it.map {
                Day(
                    label = {it.from.dayOfMonth.toString()},
                    metric = it.metric,
                    color = Colors.getMetricColor(activity.goal, it.metric, TimeRange.DAILY, Colors.ChipGray),
                    date = it.from,
                )
            }

            val metricSum = it.sumOf { it.metric }

            Week(
                from = it.first().from,
                to =  it.last().to.minus(1, DateTimeUnit.DAY),
                days = days,
                total = metricSum,
            )
        }

        return@withTransaction Month(
            weeks = weeks, year = year, month = month
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

    suspend fun getRecordById(activityId: Long, recordId: Long) = db.withTransaction {
        val activity = activityDAO.flowById(activityId).first()

        when(activity.type) {
            TrackedActivity.Type.TIME -> sessionDAO.getById(recordId)
            TrackedActivity.Type.SCORE -> scoreDAO.getById(recordId)
            TrackedActivity.Type.CHECKED ->  completionDAO.getById(recordId)
        }
    }


    suspend fun getAllRecords(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<TrackedActivityRecord> = listOf(
        sessionDAO.getAll(from, to),
        scoreDAO.getAll(from, to),
        completionDAO.getAll(from.date, to.date)
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
