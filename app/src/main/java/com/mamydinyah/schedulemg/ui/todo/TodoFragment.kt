package com.mamydinyah.schedulemg.ui.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mamydinyah.schedulemg.crud.DatePickerFragment
import com.mamydinyah.schedulemg.crud.EditModal
import com.mamydinyah.schedulemg.data.TaskAdapter
import com.mamydinyah.schedulemg.databinding.FragmentTodoBinding
import com.mamydinyah.schedulemg.data.Task

class TodoFragment : Fragment() {

    private lateinit var todoViewModel: TodoViewModel
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = TodoViewModelFactory(requireActivity().application)
        todoViewModel = ViewModelProvider(this, factory).get(TodoViewModel::class.java)

        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe tasks
        todoViewModel.filteredTasks.observe(viewLifecycleOwner) { tasks ->
            if (tasks.isNullOrEmpty()) {
                binding.textNoTasks.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.textNoTasks.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                val taskAdapter = TaskAdapter(tasks, { task ->
                    todoViewModel.deleteTaskById(task.id)
                }, { task ->
                    showDeleteConfirmationDialog(task)
                }, { task ->
                    showEditTaskDialog(task)
                })
                binding.recyclerView.adapter = taskAdapter
            }
        }

        binding.filterDate.btnToday.setOnClickListener{
            todoViewModel.tasksTodoForToday.observe(viewLifecycleOwner) { tasks ->
                todoViewModel.filterTasks(tasks, "")
            }
        }

        binding.filterDate.dateInput.setOnClickListener {
            val datePicker = DatePickerFragment { selectedDate ->
                binding.filterDate.dateInput.setText(selectedDate)
                todoViewModel.filterTasksByDate(selectedDate)
            }
            datePicker.show(parentFragmentManager, "datePicker")
        }

        binding.filterDate.resetButton.setOnClickListener {
            binding.filterDate.dateInput.text.clear()
            todoViewModel.resetFilter()
        }

        binding.filterDate.btnAll.setOnClickListener {
            todoViewModel.tasksByStatusToDo.observe(viewLifecycleOwner) { tasks ->
                todoViewModel.filterTasks(tasks, "")
            }
        }

        binding.filterDate.btnLastWeek.visibility = View.GONE

        binding.filterDate.btnThisWeek.setOnClickListener {
            todoViewModel.getTasksTodoForThisWeek.observe(viewLifecycleOwner) { tasks ->
                todoViewModel.filterTasks(tasks, "")
            }
        }

        binding.filterDate.btnNextWeek.setOnClickListener {
            todoViewModel.getTasksToDoForNextWeek.observe(viewLifecycleOwner) { tasks ->
                todoViewModel.filterTasks(tasks, "")
            }
        }

        return root
    }


    override fun onStart() {
        super.onStart()
        todoViewModel.startUpdatingTaskStatus()
    }

    override fun onStop() {
        super.onStop()
        todoViewModel.stopUpdatingTaskStatus()
    }

    private fun showDeleteConfirmationDialog(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { dialog, _ ->
                todoViewModel.deleteTaskById(task.id)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .apply {
                show()
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_red_dark
                    )
                )
                getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_green_dark
                    )
                )
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showEditTaskDialog(task: Task) {
        val editModal = EditModal(task, todoViewModel.getTaskRepository())
        editModal.show(parentFragmentManager, "editTaskModal")
    }
}
