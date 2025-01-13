package com.mamydinyah.schedulemg.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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

    @Query("SELECT * FROM task_table")
    fun getAllTasksSync(): List<Task>

    @Query("SELECT * FROM task_table WHERE id = :id LIMIT 1")
    fun getTaskById(id: Int): LiveData<Task>

    @Update
    fun update(task: Task)

    @Query("""
    SELECT * FROM task_table
    WHERE strftime('%Y-%W', date) = strftime('%Y-%W', 'now')
    ORDER BY id ASC
    """)
    fun getTasksForThisWeek(): LiveData<List<Task>>


    @Query("""
    SELECT * FROM task_table
    WHERE strftime('%Y-%W', date) = strftime('%Y-%W', 'now', '-1 week')
    ORDER BY id ASC
    """)
    fun getTasksForLastWeek(): LiveData<List<Task>>


    @Query("""
        SELECT * FROM task_table
        WHERE strftime('%Y-%W', date) = strftime('%Y-%W', 'now', '+1 week')
        ORDER BY id ASC
    """)
    fun getTasksForNextWeek(): LiveData<List<Task>>
    @Query("""
    SELECT * FROM task_table
    WHERE date BETWEEN :startDate AND :endDate
    ORDER BY id ASC
    """)
    fun getTasksWithinDateRange(startDate: String, endDate: String): LiveData<List<Task>>

}