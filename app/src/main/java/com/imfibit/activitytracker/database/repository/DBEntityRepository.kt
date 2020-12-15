package com.imfibit.activitytracker.database.repository

import com.imfibit.getitdone.database.AppDatabase
import com.imfibit.activitytracker.database.dao.BaseEditableDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class DBEntityRepository<T>(open val activityDAO: BaseEditableDAO<T>){

    fun launch(call: suspend ()->Unit) = GlobalScope.launch(Dispatchers.IO){
        call.invoke()
    }

    fun insert(item: T) = launch{ activityDAO.insert(item) }

    fun update(item: T) = launch{ activityDAO.update(item) }

    open fun delete(item: T) = launch{ activityDAO.delete(item) }


    fun transaction(work: ()->Unit){
        AppDatabase.db.runInTransaction {
            work.invoke()
        }
    }
}



