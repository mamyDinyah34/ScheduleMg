package com.mamydinyah.schedulemg.ui.inprogress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mamydinyah.schedulemg.data.Task
import com.mamydinyah.schedulemg.data.TaskAdapter
import com.mamydinyah.schedulemg.databinding.FragmentInprogressBinding

class InprogressFragment : Fragment() {

    private lateinit var inProgressViewModel: InprogressViewModel
    private var _binding: FragmentInprogressBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = InProgressVIewModelFactory(requireActivity().application)
        inProgressViewModel = ViewModelProvider(this, factory).get(InprogressViewModel::class.java)

        _binding = FragmentInprogressBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe tasks
        inProgressViewModel.tasksByStatusInProgress.observe(viewLifecycleOwner) { tasks ->
            if (tasks.isNullOrEmpty()) {
                binding.textNoTasks.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.textNoTasks.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                val taskAdapter = TaskAdapter(tasks, { task ->
                    inProgressViewModel.deleteTaskById(task.id)
                }, { task ->
                    showDeleteConfirmationDialog(task)
                })
                binding.recyclerView.adapter = taskAdapter
            }
        }

        return root
    }

    private fun showDeleteConfirmationDialog(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { dialog, _ ->
                inProgressViewModel.deleteTaskById(task.id)
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
}