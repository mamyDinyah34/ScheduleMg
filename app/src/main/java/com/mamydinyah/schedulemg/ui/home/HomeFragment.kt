package com.mamydinyah.schedulemg.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mamydinyah.schedulemg.R
import com.mamydinyah.schedulemg.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels { HomeViewModelFactory(requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val calendarView: CalendarView = binding.calendarView
        val textClock: TextClock = binding.textClock
        val todayTaskCountTextView: TextView = binding.todayTaskCount
        val totalTaskCountTextView: TextView = binding.totalTaskCount

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(context, "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()
        }

        homeViewModel.todayTaskCount.observe(viewLifecycleOwner) { count ->
            todayTaskCountTextView.text = getString(R.string.today_task_count, count)
        }

        homeViewModel.totalTaskCount.observe(viewLifecycleOwner) { count ->
            totalTaskCountTextView.text = getString(R.string.total_task_count, count)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
