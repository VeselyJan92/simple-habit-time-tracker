package com.janvesely.getitdone.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseEditableDAO<T>{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertSync(item: T): Long

    @Update
    suspend fun update(item: T)

    @Delete
    suspend fun delete(item: T)

    @Insert
    suspend fun insert(vararg users: T)

    @Insert
    suspend fun insert(user: T): Long
}