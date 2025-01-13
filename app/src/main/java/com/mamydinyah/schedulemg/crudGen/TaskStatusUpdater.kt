package com.mamydinyah.schedulemg.crudGen

import android.os.Handler
import android.os.Looper
import com.mamydinyah.schedulemg.data.TaskRepository


class TaskStatusUpdater(
    private val repository: TaskRepository,
    private val updateInterval: Long = 20000 // 60 secondes
    )
{
    private val handler = Handler(Looper.getMainLooper())

    fun start() {
        handler.post(object : Runnable {
            override fun run() {
                repository.updateTaskStatus()
                handler.postDelayed(this, updateInterval)
            }
        })
    }

    fun stop() {
        handler.removeCallbacksAndMessages(null)
    }
}
