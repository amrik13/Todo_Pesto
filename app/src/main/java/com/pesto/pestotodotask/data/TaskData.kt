package com.pesto.pestotodotask.data

data class TaskData(
    val taskId: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: Long = 0,
    val status: String = ""
)
