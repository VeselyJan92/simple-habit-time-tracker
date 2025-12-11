package com.imfibit.activitytracker.database.dao.tracked_activity

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.imfibit.activitytracker.core.DateIterator
import com.imfibit.activitytracker.core.iter
import com.imfibit.activitytracker.database.composed.ActivityWithMetric
import com.imfibit.activitytracker.database.composed.MetricAggregation
import com.imfibit.activitytracker.database.composed.toHashMap
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

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
        WHERE  date >= :from AND date <=:to AND tracked_activity_id=:activityId
        GROUP BY from_date
        ORDER BY from_date ASC
    """
    )
    suspend fun getRawMetricDaily(
        activityId: Long,
        from: LocalDate,
        to: LocalDate
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
            list.add(map[it] ?: MetricAggregation(it, it.plus(1, DateTimeUnit.DAY), 0L))
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
        val from = firstDayInWeek.minus(weeks, DateTimeUnit.WEEK).plus(1, DateTimeUnit.DAY)
        val list = mutableListOf<MetricAggregation>()

        // 1 = Monday, 7 = Sunday
        // (1 + 2) % 7 - 1 = 3 % 7 - 1 = 2
        // (7 + 2) % 7 - 1 = 9 % 7 - 1 = 2 - 1 = 1
        // SQLite: Sunday=0, Monday=1, ..., Saturday=6
        // We need to verify if this logic still holds or if ordinal is needed.
        // firstDayInWeek.dayOfWeek.value in java.time was 1 (Mon) to 7 (Sun)
        // kotlinx.datetime.DayOfWeek.value is 1 (Mon) to 7 (Sun) if available (it is).
        // If not available, use ordinal + 1.
        val dayValue = firstDayInWeek.dayOfWeek.ordinal + 1
        val sqlFirstWeekDay = (dayValue + 2 ) % 7 - 1

        val map = getRawMetricWeekly(
            activityId = activityId,
            from = from,
            to = firstDayInWeek,
            sqlFirstWeekDay = sqlFirstWeekDay
        ).toHashMap()

        DateIterator(from, firstDayInWeek, 7).forEach {
            val data = map[it]

            list.add(
                data ?: MetricAggregation(it, it.plus(1, DateTimeUnit.WEEK).minus(1, DateTimeUnit.DAY), 0L)
            )
        }

        return list
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
        year: Int,
        month: Int,
        months: Int
    ): MutableList<MetricAggregation> {
        val first = LocalDate(year, month, 1)

        val from = first.minus(months, DateTimeUnit.MONTH)

        val list = mutableListOf<MetricAggregation>()

        val map = getRawMetricByMonth(activityId, from, first.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)).toHashMap()


        repeat(months){
            val currentFirst = first.minus(it, DateTimeUnit.MONTH)
            val data = map[currentFirst]

            list.add(
                data ?: MetricAggregation(
                    currentFirst,
                    currentFirst.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY),
                    0L
                )
            )
        }
        return list.reversed().toMutableList()
    }


    suspend fun getMetricToday(activityId: Long): Long {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return getMetric(activityId, today, today)
    }

}
