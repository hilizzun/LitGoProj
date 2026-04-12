package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNotesRecyclerView()

        val fabAdd = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAdd)
        fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_createEntryFragment)
        }
    }

    private fun setupNotesRecyclerView() {
        notesAdapter = NotesAdapter(
            onNoteClick = { note ->
                // TODO: Обработка клика
            },
            onDeleteClick = { note ->
                // TODO: Обработка удаления
            }
        )

        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notesAdapter
        }

        notesAdapter.submitList(getSampleNotes())
    }

    private fun getSampleNotes() = listOf(
        Note("1","7.09.2023 в 12:03", "Мастер и Маргарита", "20 страниц", R.drawable.cover_master),
        Note("2","7.09.2023 в 12:03", "Мастер и Маргарита", "20 страниц", R.drawable.cover_master),
        Note("3","7.09.2023 в 12:03", "Мастер и Маргарита", "20 страниц", R.drawable.cover_master)
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}