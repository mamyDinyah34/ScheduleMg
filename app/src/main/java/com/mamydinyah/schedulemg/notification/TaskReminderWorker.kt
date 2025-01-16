package com.mamydinyah.schedulemg.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TaskReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Reminder"
        val content = inputData.getString("content") ?: "You have a task to complete."
        val taskId = inputData.getInt("taskId", 0)

        NotificationHelper.sendNotification(title, content, taskId, applicationContext)

        return Result.success()
    }
}
