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
    fun getAllTasks(): LiveData<List<Task>>

    @Query("DELETE FROM task_table WHERE id = :id")
    fun deleteTaskById(id: Int)

    @Query("SELECT * FROM task_table WHERE status = 'to do' ORDER BY id ASC")
    fun getTasksByStatusToDo(): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE status = 'in progress' ORDER BY id ASC")
    fun getTasksByStatusInProgress(): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE status = 'finished' ORDER BY id ASC")
    fun getTasksByStatusFinished(): LiveData<List<Task>>
}