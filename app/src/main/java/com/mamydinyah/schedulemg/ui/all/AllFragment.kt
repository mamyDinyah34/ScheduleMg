package com.mamydinyah.schedulemg.ui.all

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
import com.mamydinyah.schedulemg.data.Task
import com.mamydinyah.schedulemg.data.TaskAdapter
import com.mamydinyah.schedulemg.databinding.FragmentAllBinding

class AllFragment : Fragment() {

    private lateinit var allViewModel: AllViewModel
    private var _binding: FragmentAllBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = AllViewModelFactory(requireActivity().application)
        allViewModel = ViewModelProvider(this, factory).get(AllViewModel::class.java)

        _binding = FragmentAllBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe filtered tasks
        allViewModel.filteredTasks.observe(viewLifecycleOwner) { tasks ->
            if (tasks.isNullOrEmpty()) {
                binding.textNoTasks.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.textNoTasks.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                val taskAdapter = TaskAdapter(tasks, { task ->
                    allViewModel.deleteTaskById(task.id)
                }, { task ->
                    showDeleteConfirmationDialog(task)
                }, { task ->
                    showEditTaskDialog(task)
                })
                binding.recyclerView.adapter = taskAdapter
            }
        }

        binding.filterDate.btnToday.setOnClickListener{
            allViewModel.tasksForToday.observe(viewLifecycleOwner) { tasks ->
                allViewModel.filterTasks(tasks, "")
            }
        }

        binding.filterDate.dateInput.setOnClickListener {
            val datePicker = DatePickerFragment { selectedDate ->
                binding.filterDate.dateInput.setText(selectedDate)
                allViewModel.filterTasksByDate(selectedDate)
            }
            datePicker.show(parentFragmentManager, "datePicker")
        }

        binding.filterDate.resetButton.setOnClickListener {
            binding.filterDate.dateInput.text.clear()
            allViewModel.resetFilter()
        }

        binding.filterDate.btnAll.setOnClickListener {
            allViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                allViewModel.filterTasks(tasks, "")
            }
        }

        binding.filterDate.btnLastWeek.setOnClickListener {
            allViewModel.tasksForLastWeek.observe(viewLifecycleOwner) { tasks ->
                allViewModel.filterTasks(tasks, "")
            }
        }

        binding.filterDate.btnThisWeek.setOnClickListener {
            allViewModel.tasksForThisWeek.observe(viewLifecycleOwner) { tasks ->
                allViewModel.filterTasks(tasks, "")
            }
        }

        binding.filterDate.btnNextWeek.setOnClickListener {
            allViewModel.tasksForNextWeek.observe(viewLifecycleOwner) { tasks ->
                allViewModel.filterTasks(tasks, "")
            }
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        allViewModel.startUpdatingTaskStatus()
    }

    override fun onStop() {
        super.onStop()
        allViewModel.stopUpdatingTaskStatus()
    }

    private fun showDeleteConfirmationDialog(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { dialog, _ ->
                allViewModel.deleteTaskById(task.id)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .apply {
                show()
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            }
    }

    private fun showEditTaskDialog(task: Task) {
        val editModal = EditModal(task, allViewModel.getTaskRepository())
        editModal.show(parentFragmentManager, "editTaskModal")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


/*private fun deleteOldDatabase() {
    val context = requireContext()
    val dbName = "task_database"
    context.deleteDatabase(dbName)
    Toast.makeText(context, "Old database deleted", Toast.LENGTH_SHORT).show()
}*/

