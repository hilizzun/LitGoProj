package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesAdapter: NotesAdapter
    private lateinit var dbHelper: AppDatabaseHelper

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

        dbHelper = AppDatabaseHelper.getInstance(requireContext())
        setupNotesRecyclerView()
        loadNotes()

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_createEntryFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::dbHelper.isInitialized) {
            loadNotes()
        }
    }

    private fun setupNotesRecyclerView() {
        notesAdapter = NotesAdapter(
            onNoteClick = { note ->
                Toast.makeText(requireContext(), "Запись: ${note.bookTitle}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { note ->
                dbHelper.deleteNote(note.id)
                loadNotes()
                Toast.makeText(requireContext(), "Запись удалена", Toast.LENGTH_SHORT).show()
            }
        )

        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notesAdapter
        }
    }

    private fun loadNotes() {
        notesAdapter.submitList(dbHelper.getAllNotes())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
