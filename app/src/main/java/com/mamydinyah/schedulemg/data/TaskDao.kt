package com.mamydinyah.schedulemg.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert
    fun insertTask(task: Task)

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    fun getAllTasksLiveData(): LiveData<List<Task>>
}