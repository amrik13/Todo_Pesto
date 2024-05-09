package com.pesto.pestotodotask.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pesto.pestotodotask.data.TaskData
import com.pesto.pestotodotask.databinding.TaskListItemBinding
import com.pesto.pestotodotask.utils.TaskListClickListener

class TaskListAdapter(private var taskList: ArrayList<TaskData>): RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder>() {

    private lateinit var taskListClickListener: TaskListClickListener

    fun setTaskList(taskList: ArrayList<TaskData>) {
        this.taskList = taskList
    }

    fun setItemClickListener(taskListClickListener: TaskListClickListener) {
        this.taskListClickListener = taskListClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val itemBinding = TaskListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        holder.binding.title.text = taskList[position].title
        holder.binding.statusText.text = taskList[position].status
    }

    override fun getItemCount(): Int = taskList.size

    inner class TaskListViewHolder(val binding: TaskListItemBinding): RecyclerView.ViewHolder(binding.root), OnClickListener {
        init {
            binding.cardView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            taskListClickListener.onTaskClickListener(taskList[adapterPosition])
        }
    }

}