package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentCreateEntryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateEntryFragment : Fragment() {

    private var _binding: FragmentCreateEntryBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: AppDatabaseHelper
    private var selectedDateMillis: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = AppDatabaseHelper.getInstance(requireContext())

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = java.util.Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDateMillis = calendar.timeInMillis
        }

        binding.submitButton.setOnClickListener {
            saveEntry()
        }
    }

    private fun saveEntry() {
        val bookTitle = binding.bookSearchEditText.text?.toString().orEmpty().trim()
        val pagesValue = binding.pagesReadEditText.text?.toString().orEmpty().trim()

        if (bookTitle.isBlank() || pagesValue.isBlank()) {
            Toast.makeText(requireContext(), "Введите книгу и количество страниц", Toast.LENGTH_SHORT).show()
            return
        }

        val noteDate = formatDate(selectedDateMillis)
        val bookFromDb = dbHelper.findBookByTitle(bookTitle)

        val note = Note(
            id = "0",
            date = noteDate,
            bookTitle = bookTitle,
            pages = "$pagesValue стр.",
            coverRes = bookFromDb?.coverRes ?: R.drawable.cover_master
        )

        dbHelper.createNote(note)
        Toast.makeText(requireContext(), "Запись сохранена", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun formatDate(timeMillis: Long): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date(timeMillis))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
