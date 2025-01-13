package com.mamydinyah.schedulemg.data

import java.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import java.util.Calendar
import java.util.Locale
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

    fun updateTaskStatus() {
        Executors.newSingleThreadExecutor().execute {
            val tasks = taskDao.getAllTasksSync()
            val currentDateTime = Calendar.getInstance().time
            val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            tasks.forEach { task ->
                val taskStartDateTime = dateTimeFormat.parse("${task.date} ${task.startTime}")
                val taskEndDateTime = dateTimeFormat.parse("${task.date} ${task.endTime}")

                val newStatus = when {
                    taskEndDateTime.before(currentDateTime) -> "finished"
                    taskStartDateTime.after(currentDateTime) -> "to do"
                    taskStartDateTime.before(currentDateTime) && taskEndDateTime.after(currentDateTime) -> "in progress"
                    else -> task.status
                }

                if (task.status != newStatus) {
                    task.status = newStatus
                    taskDao.update(task)
                }
            }
        }
    }

    fun getTaskById(id: Int): LiveData<Task> {
        return taskDao.getTaskById(id)
    }

    fun updateTask(task: Task) {
        taskDao.update(task)
    }

    fun getTasksForThisWeek(): LiveData<List<Task>> {
        return taskDao.getTasksForThisWeek()
    }

    fun getTasksForLastWeek(): LiveData<List<Task>> {
        return taskDao.getTasksForLastWeek()
    }

    fun getTasksForNextWeek(): LiveData<List<Task>> {
        return taskDao.getTasksForNextWeek()
    }
}
