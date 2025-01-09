package com.mamydinyah.schedulemg.data

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mamydinyah.schedulemg.databinding.ItemBinding

class TaskAdapter(
    private val tasks: List<Task>,
    private val deleteTaskCallback: (Task) -> Unit,
    private val confirmDeleteCallback: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.title.text = task.title
            binding.description.text = task.description
            binding.dateTitle.text = task.date
            binding.timeTitle.text = task.time
            binding.status.text = task.status
            binding.statusPoint.text = "**"
            binding.statusPoint.setTextColor(getStatusColor(task.status)) // Définir la couleur du point

            binding.delete.setOnClickListener {
                confirmDeleteCallback(task)
            }
        }

        private fun getStatusColor(status: String): Int {
            return when (status) {
                "in progress" -> Color.GREEN
                "finished" -> Color.RED
                "to do" -> Color.YELLOW
                else -> Color.GRAY
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size
}
