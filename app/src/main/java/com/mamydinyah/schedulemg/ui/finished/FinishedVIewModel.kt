package com.mamydinyah.schedulemg.ui.finished

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mamydinyah.schedulemg.crud.TaskStatusUpdater
import com.mamydinyah.schedulemg.data.Connection
import com.mamydinyah.schedulemg.data.Task
import com.mamydinyah.schedulemg.data.TaskRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinishedVIewModel(application: Application) : ViewModel() {

    val tasksByStatusFinished: LiveData<List<Task>>
    val tasksFinishedForToday: LiveData<List<Task>>
    val tasksFinishedForThisWeek: LiveData<List<Task>>
    val tasksFinishedForLastWeek: LiveData<List<Task>>
    private val repository: TaskRepository
    private val selectedDate = MutableLiveData<String>()
    val filteredTasks = MediatorLiveData<List<Task>>().apply {
        value = emptyList()
    }
    private val currentFilterMode = MutableLiveData<String>()
    private val taskStatusUpdater: TaskStatusUpdater

    init {
        val taskDao = Connection.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao, application)
        tasksByStatusFinished = repository.tasksByStatusFinished()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        tasksFinishedForToday = repository.getTasksFinishedForToday(today)
        tasksFinishedForThisWeek = repository.getTasksFinishedForThisWeek()
        tasksFinishedForLastWeek = repository.getTasksFinishedForLastWeek()

        filteredTasks.addSource(tasksByStatusFinished) { tasks ->
            filterTasks(tasks, selectedDate.value)
        }

        filteredTasks.addSource(selectedDate) { date ->
            filterTasks(tasksByStatusFinished.value, date)
        }
        taskStatusUpdater = TaskStatusUpdater(repository)
    }

    fun startUpdatingTaskStatus() {
        taskStatusUpdater.start()
    }

    fun stopUpdatingTaskStatus() {
        taskStatusUpdater.stop()
    }

    fun filterTasks(tasks: List<Task>?, date: String?) {
        val filteredByWeek = when (currentFilterMode.value) {
            "lastWeek" -> tasksFinishedForLastWeek.value
            "thisWeek" -> tasksFinishedForThisWeek.value
            else -> tasks
        }

        if (date.isNullOrEmpty()) {
            filteredTasks.value = filteredByWeek
        } else {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val dateToCompare = try {
                val parsedDate = dateFormat.parse(date)
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDate)
            } catch (e: Exception) {
                null
            }

            if (dateToCompare != null) {
                val filtered = filteredByWeek?.filter { it.date == dateToCompare }
                filteredTasks.value = filtered
            } else {
                filteredTasks.value = emptyList()
            }
        }
    }

    fun filterTasksByDate(date: String) {
        selectedDate.value = date
    }

    fun deleteTaskById(id: Int) {
        repository.deleteTaskById(id)
    }

    fun resetFilter() {
        selectedDate.value = ""
    }

    fun getTaskRepository(): TaskRepository {
        return repository
    }
}