package com.pesto.pestotodotask.utils

import com.pesto.pestotodotask.data.TaskData

interface TaskListClickListener {
    fun onTaskClickListener(taskData: TaskData)
}