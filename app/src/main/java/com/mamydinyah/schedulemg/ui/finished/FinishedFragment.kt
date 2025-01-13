package com.mamydinyah.schedulemg.ui.finished

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
import com.mamydinyah.schedulemg.databinding.FragmentFinishedBinding

class FinishedFragment : Fragment() {

    private lateinit var finishedVIewModel: FinishedVIewModel
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = FinishedVIewModelFactory(requireActivity().application)
        finishedVIewModel = ViewModelProvider(this, factory).get(FinishedVIewModel::class.java)

        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe tasks
        finishedVIewModel.filteredTasks.observe(viewLifecycleOwner) { tasks ->
            if (tasks.isNullOrEmpty()) {
                binding.textNoTasks.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.textNoTasks.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                val taskAdapter = TaskAdapter(tasks, { task ->
                    finishedVIewModel.deleteTaskById(task.id)
                }, { task ->
                    showDeleteConfirmationDialog(task)
                }, { task ->
                    showEditTaskDialog(task)
                })
                binding.recyclerView.adapter = taskAdapter
            }
        }

        binding.filterDate.dateInput.setOnClickListener {
            val datePicker = DatePickerFragment { selectedDate ->
                binding.filterDate.dateInput.setText(selectedDate)
                finishedVIewModel.filterTasksByDate(selectedDate)
            }
            datePicker.show(parentFragmentManager, "datePicker")
        }
        binding.filterDate.resetButton.setOnClickListener {
            binding.filterDate.dateInput.text.clear()
            finishedVIewModel.resetFilter()
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        finishedVIewModel.startUpdatingTaskStatus()
    }

    override fun onStop() {
        super.onStop()
        finishedVIewModel.stopUpdatingTaskStatus()
    }

    private fun showDeleteConfirmationDialog(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { dialog, _ ->
                finishedVIewModel.deleteTaskById(task.id)
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

    private fun showEditTaskDialog(task: Task) {
        val editModal = EditModal(task, finishedVIewModel.getTaskRepository())
        editModal.show(parentFragmentManager, "editTaskModal")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}