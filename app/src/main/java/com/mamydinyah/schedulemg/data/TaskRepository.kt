package com.mamydinyah.schedulemg.data

import android.util.Log
import java.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
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
        return getTasksForWeek(0)
    }

    fun getTasksForLastWeek(): LiveData<List<Task>> {
        return getTasksForWeek(-1)
    }

    fun getTasksForNextWeek(): LiveData<List<Task>> {
        return getTasksForWeek(1)
    }

    private fun getTasksForWeek(offset: Int): LiveData<List<Task>> {
        val (startOfWeek, endOfWeek) = getWeekRange(offset)
        Log.d("TaskRepository", "Fetching tasks for week between $startOfWeek and $endOfWeek")
        return taskDao.getTasksWithinDateRange(startOfWeek, endOfWeek).map { tasks ->
            tasks.sortedWith(compareBy<Task> { it.date }.thenBy { it.startTime })
        }
    }

    private fun getWeekRange(offset: Int = 0): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.add(Calendar.WEEK_OF_YEAR, offset)

        // Set to first day of week (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startDate = calendar.time

        // Move to last day of week (Sunday)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endDate = calendar.time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedStartDate = dateFormat.format(startDate)
        val formattedEndDate = dateFormat.format(endDate)

        Log.d("TaskRepository", "Week Range: $formattedStartDate to $formattedEndDate")
        return Pair(formattedStartDate, formattedEndDate)
    }

}
