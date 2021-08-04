package com.imfibit.activitytracker.database.repository.tracked_activity

import androidx.compose.ui.res.stringResource
import androidx.room.withTransaction
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.imfibit.activitytracker.R
import com.imfibit.activitytracker.core.ComposeString
import com.imfibit.activitytracker.core.sumByLong
import com.imfibit.activitytracker.database.dao.tracked_activity.*
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.entities.*
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.database.composed.RecordWithActivity
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
    val sessionDAO: DAOTrackedActivityTime = AppDatabase.db.sessionDAO,
    val metricDAO: DAOTrackedActivityMetric = AppDatabase.db.metricDAO,
    val timers: DAOPresetTimers = AppDatabase.db.presetTimersDAO
) : DBEntityRepository<TrackedActivity>(activityDAO) {

    suspend fun getActivitiesOverview(pastRanges: Int) =  AppDatabase.db.withTransaction {
        return@withTransaction AppDatabase.db.activityDAO.getAllNotInSession().map { activity ->

            val data = when(activity.goal.range){
                TimeRange.DAILY -> metricDAO.getMetricByDay(activity.id,LocalDate.now().minusDays(pastRanges.toLong()), LocalDate.now())
                TimeRange.WEEKLY -> metricDAO.getMetricByWeek(activity.id, LocalDate.now().with(ChronoField.DAY_OF_WEEK, 7), pastRanges)
                TimeRange.MONTHLY -> metricDAO.getMetricByMonth(activity.id, YearMonth.now(), pastRanges)
            }

            val hasMetricToday = if (activity.type == TrackedActivity.Type.CHECKED){
                val metric = metricDAO.getMetricByDay(activity.id, LocalDate.now().minusDays(1), LocalDate.now())

                metric.first().metric > 0
            }
            else false



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
                    { activity.goal.range.getShortLabel(it.from) },
                    metric,
                    color,
                    Editable(activity.type, it.metric, it.from.atStartOfDay(), it.to.atStartOfDay(), activity.id)
                )
            }
            TrackedActivityWithMetric(activity, groupedMetric, hasMetricToday)
        }
    }

    suspend fun getRecentActivity(
        activityId: Long,
        firstDayInWeek: LocalDate,
        weeks: Int
    ) = AppDatabase.db.withTransaction {
        val from  = firstDayInWeek.minusWeeks(weeks.toLong()).plusDays(1)
        val activity = activityDAO.getById(activityId)

        return@withTransaction metricDAO.getMetricByDay(activityId, from, firstDayInWeek).chunked(7).map {

            val days = it.map {
                MetricWidgetData.Labeled(
                    label = {it.from.dayOfMonth.toString()},
                    metric = activity.type.getComposeString(it.metric),
                    color = Colors.getMetricColor(activity.goal, it.metric, TimeRange.DAILY, Colors.ChipGray),
                    editable = Editable(
                            type = activity.type,
                            metric = it.metric,
                            from = it.from.atStartOfDay(),
                            to = it.to.atStartOfDay().plusDays(1L),
                            activityId = activityId
                    )
                )
            }

            val metricSum = days.sumByLong { it.editable!!.metric}

            val metric:ComposeString = when (activity.type) {
                TrackedActivity.Type.CHECKED -> {{ "$metricSum/7" }}
                else -> activity.type.getComposeString(metricSum)
            }

            val color = when {
                activity.goal.range == TimeRange.WEEKLY && activity.goal.isSet() -> {
                    if (activity.goal.value <= metricSum)
                        Colors.Completed
                    else
                        Colors.NotCompleted
                }
                else -> {
                    Colors.AppAccent
                }
            }

            Week(
                    from = it.first().from.atStartOfDay(),
                    to =  it.last().from.atStartOfDay(),
                    days = days.reversed(),
                    stat = MetricWidgetData.Labeled(
                            label = { stringResource(R.string.frequency_weekly)},
                            metric = metric,
                            color = color
                    )
            )

        }
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

    suspend fun deleteRecordById(activityId: Long, recordId: Long) = transaction {
        val activity = activityDAO.getById(activityId)

        when(activity.type) {
            TrackedActivity.Type.TIME -> sessionDAO.deleteById(recordId)
            TrackedActivity.Type.SCORE -> scoreDAO.deleteById(recordId)
            TrackedActivity.Type.CHECKED ->  completionDAO.deleteById(recordId)
        }
    }


    suspend fun startSession(activityId: Long) = AppDatabase.db.withTransaction {
        activityDAO.update(activityDAO.getById(activityId).copy(inSessionSince = LocalDateTime.now()))
    }

    suspend fun commitLiveSession(activityId: Long) = AppDatabase.db.withTransaction {
        val activity = activityDAO.getById(activityId)

        if (activity.inSessionSince != null){
            val session = TrackedActivityTime(0, activityId, activity.inSessionSince!!, LocalDateTime.now())
            sessionDAO.insert(session)
            activityDAO.update(activity.copy(inSessionSince = null))
        }else
            FirebaseCrashlytics.getInstance().recordException(IllegalArgumentException("Committing already committed session"))

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
    ) = AppDatabase.db.withTransaction {
        val activities = activityDAO.getAll().map { Pair(it.id, it) }.toMap()

        return@withTransaction getAllRecords(from, to).map {
            RecordWithActivity(
                activities[it.activity_id] ?: error("Activity not found"),
                it
            )
        }
    }




}

