package com.mamydinyah.schedulemg.ui.finished

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mamydinyah.schedulemg.data.Connection
import com.mamydinyah.schedulemg.data.Task
import com.mamydinyah.schedulemg.data.TaskRepository

class FinishedVIewModel(application: Application) : ViewModel() {

    val tasksByStatusFinished: LiveData<List<Task>>
    private val repository: TaskRepository

    init {
        val taskDao = Connection.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        tasksByStatusFinished = repository.tasksByStatusFinished()
    }

    fun deleteTaskById(id: Int) {
        repository.deleteTaskById(id)
    }
}