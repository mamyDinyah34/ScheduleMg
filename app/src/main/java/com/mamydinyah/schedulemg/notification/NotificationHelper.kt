package com.mamydinyah.schedulemg.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.Date
import java.util.concurrent.TimeUnit

object NotificationHelper {

    private const val CHANNEL_ID = "task_channel"
    private const val CHANNEL_NAME = "Task Notifications"

    fun isWithinTimeRange(currentTime: Date, targetTime: Date, minutesRange: Int): Boolean {
        val diffInMillis = Math.abs(currentTime.time - targetTime.time)
        return diffInMillis <= TimeUnit.MINUTES.toMillis(minutesRange.toLong())
    }

    fun sendNotification(title: String, content: String, taskId: Int, context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(taskId, notification)
    }
}
