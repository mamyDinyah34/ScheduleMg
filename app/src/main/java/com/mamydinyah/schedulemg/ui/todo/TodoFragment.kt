package com.mamydinyah.schedulemg.ui.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mamydinyah.schedulemg.data.TaskAdapter
import com.mamydinyah.schedulemg.databinding.FragmentTodoBinding

class TodoFragment : Fragment() {

    private lateinit var todoViewModel: TodoViewModel
    private var _binding: FragmentTodoBinding? = null
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

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

        listView = binding.listItem
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mutableListOf()
        )
        listView.adapter = adapter

        // Observer les tâches
        todoViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            if (tasks.isNullOrEmpty()) {
                binding.textNoTasks.visibility = View.VISIBLE
                binding.listItem.visibility = View.GONE
            } else {
                binding.textNoTasks.visibility = View.GONE
                binding.listItem.visibility = View.VISIBLE
                val taskAdapter = TaskAdapter(requireContext(), tasks)
                binding.listItem.adapter = taskAdapter
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
