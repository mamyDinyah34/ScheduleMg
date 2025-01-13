package com.mamydinyah.schedulemg.ui.all

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
import java.util.Locale

class AllViewModel(application: Application) : ViewModel() {

    val allTasks: LiveData<List<Task>>
    val tasksForThisWeek: LiveData<List<Task>>
    val tasksForLastWeek: LiveData<List<Task>>
    val tasksForNextWeek: LiveData<List<Task>>
    private val repository: TaskRepository
    private val selectedDate = MutableLiveData<String>()
    val filteredTasks = MediatorLiveData<List<Task>>().apply {
        value = emptyList()
    }
    private val currentFilterMode = MutableLiveData<String>()
    private val taskStatusUpdater: TaskStatusUpdater

    init {
        val taskDao = Connection.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
        tasksForThisWeek = repository.getTasksForThisWeek()
        tasksForLastWeek = repository.getTasksForLastWeek()
        tasksForNextWeek = repository.getTasksForNextWeek()

        filteredTasks.addSource(allTasks) { tasks ->
            filterTasks(tasks, selectedDate.value)
        }

        filteredTasks.addSource(selectedDate) { date ->
            filterTasks(allTasks.value, date)
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
            "lastWeek" -> tasksForLastWeek.value
            "thisWeek" -> tasksForThisWeek.value
            "nextWeek" -> tasksForNextWeek.value
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

    fun setFilterMode(mode: String) {
        currentFilterMode.value = mode
        filterTasks(allTasks.value, selectedDate.value)
    }

    fun resetFilter() {
        selectedDate.value = ""
        currentFilterMode.value = "all"
        filterTasks(allTasks.value, null)
    }

    fun filterTasksByDate(date: String) {
        selectedDate.value = date
    }

    fun deleteTaskById(id: Int) {
        repository.deleteTaskById(id)
    }
    fun getTaskById(id: Int): LiveData<Task> {
        return repository.getTaskById(id)
    }

    fun getTaskRepository(): TaskRepository {
        return repository
    }
}
