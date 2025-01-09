package com.mamydinyah.schedulemg.ui.all

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mamydinyah.schedulemg.data.Connection
import com.mamydinyah.schedulemg.data.Task
import com.mamydinyah.schedulemg.data.TaskRepository

class AllViewModel(application: Application) : ViewModel() {

    val allTasks: LiveData<List<Task>>
    private val repository: TaskRepository

    init {
        val taskDao = Connection.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
    }

    fun deleteTaskById(id: Int) {
        repository.deleteTaskById(id)
    }
}