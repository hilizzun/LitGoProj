package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.api.BookCatalogRepository
import com.example.myapplication.databinding.FragmentAddBookBinding
import kotlinx.coroutines.launch

class AddBookFragment : Fragment() {

    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: AppDatabaseHelper
    private val repository = BookCatalogRepository()
    private lateinit var genres: Array<String>
    private var selectedCoverUri: Uri? = null

    private val pickCoverLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@registerForActivityResult

        runCatching {
            requireContext().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        selectedCoverUri = uri
        binding.uploadCoverButton.text = "Обложка выбрана"
        Toast.makeText(requireContext(), "Обложка сохранена", Toast.LENGTH_SHORT).show()
    }

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

        genres = arrayOf("Роман", "Детектив", "Фантастика", "Научная литература", "Поэзия")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genreSpinner.adapter = adapter

        binding.apiSearchButton.setOnClickListener {
            loadBookFromApi()
        }

        binding.submitButton.setOnClickListener {
            saveBook()
        }

        binding.uploadCoverButton.setOnClickListener {
            pickCoverLauncher.launch(arrayOf("image/*"))
        }
    }

    private fun loadBookFromApi() {
        val query = binding.apiSearchEditText.text?.toString().orEmpty().trim()
        if (query.isBlank()) {
            Toast.makeText(requireContext(), "Введите название книги для поиска", Toast.LENGTH_SHORT).show()
            return
        }

        binding.apiSearchButton.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { repository.searchBookByTitle(query) }
                .onSuccess { draft ->
                    if (draft == null) {
                        Toast.makeText(requireContext(), "Книга не найдена", Toast.LENGTH_SHORT).show()
                    } else {
                        if (draft.title.isNotBlank()) binding.titleEditText.setText(draft.title)
                        if (draft.author.isNotBlank()) binding.authorEditText.setText(draft.author)
                        if (draft.year.isNotBlank()) binding.yearEditText.setText(draft.year)
                        if (draft.description.isNotBlank()) binding.descriptionEditText.setText(draft.description)
                        if (draft.pages.isNotBlank()) binding.pagesEditText.setText(draft.pages)
                        if (draft.genre.isNotBlank()) setGenreFromApi(draft.genre)
                        Toast.makeText(requireContext(), "Поля заполнены из API", Toast.LENGTH_SHORT).show()
                    }
                }
                .onFailure {
                    Toast.makeText(requireContext(), "Ошибка запроса к API", Toast.LENGTH_SHORT).show()
                }

            binding.apiSearchButton.isEnabled = true
        }
    }

    private fun setGenreFromApi(genre: String) {
        val index = genres.indexOfFirst { it.equals(genre, ignoreCase = true) }
        if (index >= 0) {
            binding.genreSpinner.setSelection(index)
        }
    }

    private fun saveBook() {
        val title = binding.titleEditText.text?.toString().orEmpty().trim()
        val author = binding.authorEditText.text?.toString().orEmpty().trim()
        val genre = binding.genreSpinner.selectedItem?.toString().orEmpty().ifBlank { "Роман" }
        val year = binding.yearEditText.text?.toString().orEmpty().toIntOrNull() ?: 0
        val description = binding.descriptionEditText.text?.toString().orEmpty().trim()

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
            coverRes = R.drawable.cover_master,
            coverUri = selectedCoverUri?.toString(),
            year = year,
            description = description
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
