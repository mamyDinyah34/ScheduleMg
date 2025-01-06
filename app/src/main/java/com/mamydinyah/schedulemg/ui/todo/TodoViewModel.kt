package com.mamydinyah.schedulemg.ui.todo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mamydinyah.schedulemg.data.Connection
import com.mamydinyah.schedulemg.data.Task
import com.mamydinyah.schedulemg.data.TaskRepository

class TodoViewModel(application: Application) : ViewModel() {

    val allTasks: LiveData<List<Task>>
    private val repository: TaskRepository

    init {
        val taskDao = Connection.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
    }

    /*private val _text = MutableLiveData<String>().apply {
        value = "This is To Do model Fragment"
    }
    val text: LiveData<String> = _text*/
}