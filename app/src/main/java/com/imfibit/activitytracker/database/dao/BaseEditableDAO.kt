package com.imfibit.activitytracker.database.dao

import androidx.room.*
import kotlin.collections.toTypedArray


interface BaseEditableDAO<T>{

    @Update
    suspend fun update(item: T)

    @Update
    suspend fun updateAll(vararg entity: T)

    @Delete
    suspend fun delete(item: T)

    @Delete
    suspend fun deleteAll(vararg entity: T)

    @Insert
    suspend fun insert(vararg users: T)

    @Insert
    suspend fun insert(item: T): Long

    @Upsert
    suspend fun upsert(item: T): Long

    @Upsert
    suspend fun upsertAll(vararg entity: T)

}

suspend inline fun <reified T> BaseEditableDAO<T>.upsertAll(items: List<T>) = upsertAll(*items.toTypedArray<T>())

suspend inline fun <reified T> BaseEditableDAO<T>.updateAll(items: List<T>) = updateAll(*items.toTypedArray<T>())

