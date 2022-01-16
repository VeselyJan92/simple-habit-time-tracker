package com.imfibit.activitytracker.database.dao.tracked_activity

import android.util.Log
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.imfibit.activitytracker.core.DateIterator
import com.imfibit.activitytracker.core.iter
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.composed.MetricAggregation
import com.imfibit.activitytracker.database.composed.toHashMap
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

@Dao
interface DAOTrackedActivityMetric {

   /* @Query("""
        SELECT * FROM tracked_activity_metric
    """)
    suspend fun getAll(): List<TrackedActivityMetric>*/


    @Query(
        """
        SELECT
            TOTAL(metric) as metric
        FROM tracked_activity_metric
        WHERE  date >= :from AND date <=:to AND tracked_activity_id=:activityId
    """
    )
    suspend fun getMetric(activityId: Long, from: LocalDate, to: LocalDate): Long

    @Query("""
       select ta.*,  data.metric from tracked_activity ta 
       left join (
            SELECT tracked_activity_id, TOTAL(metric) as metric
            from tracked_activity_metric   
            WHERE  date >= :from AND date <= :to  
            GROUP BY tracked_activity_id
       ) data USING(tracked_activity_id)
    """
    )
    suspend fun getActivitiesWithMetric(from: LocalDate, to: LocalDate): List<ActivityWithMetric>


    @Query(
        """
        SELECT 
            date as from_date,
            date(date, '+1 day') as to_date,
            TOTAL(metric) as metric
        FROM tracked_activity_metric
        WHERE  date > :from AND date <=:to AND tracked_activity_id=:activityId
        GROUP BY from_date
        ORDER BY from_date ASC
    """
    )
    suspend fun getRawMetricDaily(
        activityId: Long,
        from: LocalDate, //exclusive
        to: LocalDate // inclusive
    ): List<MetricAggregation>

    @Transaction
    suspend fun getMetricByDay(
        activityId: Long,
        from: LocalDate,
        to: LocalDate
    ): MutableList<MetricAggregation> {
        val list = mutableListOf<MetricAggregation>()
        val map = getRawMetricDaily(activityId, from, to).toHashMap()

        (from iter to).forEach {
            list.add(map[it] ?: MetricAggregation(it, it.plusDays(1L), 0L))
        }

        return list
    }

    @Query(
        """
        SELECT
           CASE DATE(date, 'weekday ' || :sqlFirstWeekDay) WHEN DATE(date) THEN DATE(date)  ELSE DATE(date, 'weekday ' || :sqlFirstWeekDay, '-7 days')   END AS from_date,
           CASE DATE(date, 'weekday ' || :sqlFirstWeekDay) WHEN DATE(date) THEN DATE(date, '+6 days')  ELSE DATE(date, 'weekday ' || :sqlFirstWeekDay, '-1 days' ) END AS to_date,
           TOTAL(metric) AS metric
        FROM tracked_activity_metric
        WHERE  date >= :from AND date <= :to AND tracked_activity_id=:activityId
        GROUP BY from_date
        ORDER BY from_date ASC
    """
    )
    suspend fun getRawMetricWeekly(
        activityId: Long,
        from: LocalDate,
        to: LocalDate,
        sqlFirstWeekDay: Int
    ): List<MetricAggregation>


    @Transaction
    suspend fun getMetricByWeek(
        activityId: Long,
        firstDayInWeek: LocalDate, //Inclusive
        weeks: Int,
    ): MutableList<MetricAggregation> {
        val from = firstDayInWeek.minusWeeks(weeks.toLong()).plusDays(1L)
        val list = mutableListOf<MetricAggregation>()

        val sqlFirstWeekDay =(firstDayInWeek.dayOfWeek.value +2 )%7 -1

        val map = getRawMetricWeekly(
            activityId = activityId,
            from = from,
            to = firstDayInWeek,
            sqlFirstWeekDay = sqlFirstWeekDay
        ).toHashMap()

        DateIterator(from, firstDayInWeek, 7).forEach {
            val data = map[it]

            list.add(
                data ?: MetricAggregation(it, it.plusWeeks(1).minusDays(1L), 0L)
            )
        }

        return list.asReversed()
    }

    @Query(
        """
        SELECT 
            date(strftime("%Y-%m", date) || "-01") as from_date,
            date(strftime("%Y-%m", date) || "-01", "+1 month", '-1 day') as to_date,
            TOTAL(metric) as metric
        FROM tracked_activity_metric
        WHERE  date >= :from AND date <= :to AND tracked_activity_id=:activityId
        GROUP BY from_date
        ORDER BY from_date ASC
    """
    )
    fun getRawMetricByMonth(activityId: Long, from: LocalDate, to: LocalDate):List<MetricAggregation>

    @Transaction
    fun getMetricByMonth(
        activityId: Long,
        first: YearMonth,
        months: Int
    ): MutableList<MetricAggregation> {

        val from = first.minusMonths(months.toLong()).atDay(1)

        val list = mutableListOf<MetricAggregation>()

        val map = getRawMetricByMonth(activityId, from, first.atEndOfMonth()).toHashMap()


        repeat(months){
            val data = map[first.minusMonths(it.toLong()).atDay(1)]

            list.add(
                data ?: MetricAggregation(
                    first.minusMonths(it.toLong()).atDay(1),
                    first.minusMonths(it.toLong()).atEndOfMonth(),
                    0L
                )
            )
        }
        return list
    }





}