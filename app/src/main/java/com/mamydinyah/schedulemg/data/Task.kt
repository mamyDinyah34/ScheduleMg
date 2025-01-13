package com.mamydinyah.schedulemg.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var title: String,
    var description: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var status: String
)