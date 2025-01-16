package com.mamydinyah.schedulemg.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mamydinyah.schedulemg.notification.TaskReminderWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TaskRepository(private val taskDao: TaskDao, private val context: Context) {
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

    private fun scheduleTaskReminder(taskId: Int, title: String, content: String, delayInMillis: Long) {
        val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "taskId" to taskId,
                    "title" to title,
                    "content" to content
                )
            )
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
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

                //start
                val delayStart = taskStartDateTime.time - currentDateTime.time
                if (delayStart > 0) {
                    scheduleTaskReminder(
                        task.id,
                        "Task to Start",
                        "It's time to start the task: ${task.title}",
                        delayStart
                    )
                }

                //end
                val delayEnd = taskEndDateTime.time - currentDateTime.time
                if (delayEnd > 0) {
                    scheduleTaskReminder(
                        task.id,
                        "Task to Complete",
                        "It's time to complete the task: ${task.title}",
                        delayEnd
                    )
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

    fun getTasksForToday(today: String): LiveData<List<Task>> {
        return taskDao.getTasksForToday(today)
    }

    fun getTasksTodoForToday(today: String): LiveData<List<Task>> {
        return taskDao.getTasksTodoForToday(today)
    }

    fun getTasksFinishedForToday(today: String): LiveData<List<Task>> {
        return taskDao.getTasksFinishedForToday(today)
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
        return taskDao.getTasksDateRange(startOfWeek, endOfWeek).map { tasks ->
            tasks.sortedWith(compareBy<Task> { it.date }.thenBy { it.startTime })
        }
    }

    fun getTasksTodoForThisWeek(): LiveData<List<Task>> {
        return getTasksTodoForWeek(0)
    }

    fun getTasksToDoForNextWeek(): LiveData<List<Task>> {
        return getTasksTodoForWeek(1)
    }

    private fun getTasksTodoForWeek(offset: Int): LiveData<List<Task>> {
        val (startOfWeek, endOfWeek) = getWeekRange(offset)
        Log.d("TaskRepository", "Fetching tasks for week between $startOfWeek and $endOfWeek")
        return taskDao.getTasksToDoRange(startOfWeek, endOfWeek).map { tasks ->
            tasks.sortedWith(compareBy<Task> { it.date }.thenBy { it.startTime })
        }
    }

    fun getTasksFinishedForThisWeek(): LiveData<List<Task>> {
        return getTasksFinishedForWeek(0)
    }

    fun getTasksFinishedForLastWeek(): LiveData<List<Task>> {
        return getTasksFinishedForWeek(-1)
    }

    private fun getTasksFinishedForWeek(offset: Int): LiveData<List<Task>> {
        val (startOfWeek, endOfWeek) = getWeekRange(offset)
        Log.d("TaskRepository", "Fetching tasks for week between $startOfWeek and $endOfWeek")
        return taskDao.getTasksFinishedRange(startOfWeek, endOfWeek).map { tasks ->
            tasks.sortedWith(compareBy<Task> { it.date }.thenBy { it.startTime })
        }
    }

    private fun getWeekRange(offset: Int = 0): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.add(Calendar.WEEK_OF_YEAR, offset)

        // Set to first (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startDate = calendar.time

        // Move to last (Sunday)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endDate = calendar.time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedStartDate = dateFormat.format(startDate)
        val formattedEndDate = dateFormat.format(endDate)

        Log.d("TaskRepository", "Week Range: $formattedStartDate to $formattedEndDate")
        return Pair(formattedStartDate, formattedEndDate)
    }

    val totalTaskCount: LiveData<Int> = taskDao.getTotalTaskCount()

    fun getTodayTaskCount(): LiveData<Int> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        return taskDao.getTodayTaskCount(today)
    }

    fun getTasksForTodaySortedByTime(): LiveData<List<Task>> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        return taskDao.getTasksForTodaySortedByTime(today)
    }
}
