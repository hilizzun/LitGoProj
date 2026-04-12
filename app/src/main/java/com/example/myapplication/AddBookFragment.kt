package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentAddBookBinding

class AddBookFragment : Fragment() {

    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: AppDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = AppDatabaseHelper.getInstance(requireContext())

        val genres = arrayOf("Роман", "Детектив", "Фантастика", "Научная литература", "Поэзия")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genreSpinner.adapter = adapter

        binding.submitButton.setOnClickListener {
            saveBook()
        }

        binding.uploadCoverButton.setOnClickListener {
            Toast.makeText(requireContext(), "Используется стандартная обложка", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveBook() {
        val title = binding.titleEditText.text?.toString().orEmpty().trim()
        val author = binding.authorEditText.text?.toString().orEmpty().trim()
        val genre = binding.genreSpinner.selectedItem?.toString().orEmpty().ifBlank { "Роман" }

        if (title.isBlank() || author.isBlank()) {
            Toast.makeText(requireContext(), "Введите название и автора", Toast.LENGTH_SHORT).show()
            return
        }

        val book = Book(
            id = "0",
            title = title,
            author = author,
            genre = genre,
            status = "В планах",
            progress = 0,
            coverRes = R.drawable.cover_master
        )

        dbHelper.createBook(book)
        Toast.makeText(requireContext(), "Книга добавлена", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
