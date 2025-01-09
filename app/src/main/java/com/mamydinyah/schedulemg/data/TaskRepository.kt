package com.mamydinyah.schedulemg.data

import androidx.lifecycle.LiveData
import java.util.concurrent.Executors

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    fun insertTask(task: Task) {
        Executors.newSingleThreadExecutor().execute {
            taskDao.insertTask(task)
        }
    }

    fun deleteTaskById(id: Int) {
        Executors.newSingleThreadExecutor().execute {
            taskDao.deleteTaskById(id)
        }
    }

    fun tasksByStatusToDo(): LiveData<List<Task>> {
        return taskDao.getTasksByStatusToDo()
    }

    fun tasksByStatusInProgress(): LiveData<List<Task>> {
        return taskDao.getTasksByStatusInProgress()
    }

    fun tasksByStatusFinished(): LiveData<List<Task>> {
        return taskDao.getTasksByStatusFinished()
    }
}
