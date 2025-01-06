package com.mamydinyah.schedulemg.data

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasksLiveData()

    fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }
}
