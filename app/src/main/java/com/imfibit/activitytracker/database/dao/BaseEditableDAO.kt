package com.imfibit.activitytracker.database.dao

import androidx.room.*


interface BaseEditableDAO<T>{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSync(item: T): Long

    @Update
    suspend fun update(item: T)

    @Update
    suspend fun updateAll(vararg entity: T)

    @Delete
    suspend fun delete(item: T)

    @Insert
    suspend fun insert(vararg users: T)

    @Insert
    suspend fun insert(item: T): Long

    @Upsert
    suspend fun upsert(item: T): Long

}