package com.mamydinyah.schedulemg.ui.todo

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

class TodoViewModel(application: Application) : ViewModel() {

    val tasksByStatusToDo: LiveData<List<Task>>
    val tasksTodoForToday: LiveData<List<Task>>
    val getTasksTodoForThisWeek: LiveData<List<Task>>
    val getTasksToDoForNextWeek: LiveData<List<Task>>
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
        tasksByStatusToDo = repository.tasksByStatusToDo()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        tasksTodoForToday = repository.getTasksTodoForToday(today)
        getTasksTodoForThisWeek = repository.getTasksTodoForThisWeek()
        getTasksToDoForNextWeek = repository.getTasksToDoForNextWeek()

        filteredTasks.addSource(tasksByStatusToDo) { tasks ->
            filterTasks(tasks, selectedDate.value)
        }

        filteredTasks.addSource(selectedDate) { date ->
            filterTasks(tasksByStatusToDo.value, date)
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
            "thisWeek" -> getTasksTodoForThisWeek.value
            "nextWeek" -> getTasksToDoForNextWeek.value
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
        currentFilterMode.value = "all"
        filterTasks(tasksByStatusToDo.value, null)
    }

    fun getTaskRepository(): TaskRepository {
        return repository
    }

    /*private val _text = MutableLiveData<String>().apply {
        value = "This is To Do model Fragment"
    }
    val text: LiveData<String> = _text*/
}