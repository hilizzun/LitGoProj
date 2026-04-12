package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: AppDatabaseHelper
    private var currentBook: Book? = null
    private var currentPage = 0
    private val totalPages = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = AppDatabaseHelper.getInstance(requireContext())

        val bookId = arguments?.getString("bookId")
        if (bookId.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Книга не найдена", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        loadBook(bookId)
        setupFeedbackPersistence()

        binding.backButton.setOnClickListener {
            persistCurrentBook()
            findNavController().popBackStack()
        }

        binding.incrementButton.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                saveProgress()
            }
        }

        binding.decrementButton.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                saveProgress()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        persistCurrentBook()
    }

    private fun loadBook(bookId: String) {
        val book = dbHelper.getBookById(bookId)
        if (book == null) {
            Toast.makeText(requireContext(), "Книга не найдена", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        currentBook = book
        currentPage = book.progress.coerceIn(0, totalPages)

        binding.bookTitleTextView.text = book.title
        binding.authorTextView.text = book.author
        binding.genreTextView.text = book.genre
        binding.statusTextView.text = book.status
        binding.bookCoverImageView.loadBookCover(book.coverUri, book.coverRes)
        binding.descriptionTextView.text = if (book.description.isBlank()) "Описание не добавлено" else book.description
        binding.yearTextView.text = if (book.year > 0) "${book.year} г" else "Год не указан"
        binding.reviewEditText.setText(book.review)
        binding.ratingBar.isIndicator = false
        binding.ratingBar.stepSize = 0.5f
        binding.ratingBar.rating = book.rating

        updatePageCounter()
    }

    private fun setupFeedbackPersistence() {
        binding.reviewEditText.doAfterTextChanged { editable ->
            val book = currentBook ?: return@doAfterTextChanged
            currentBook = book.copy(review = editable?.toString().orEmpty())
        }

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (!fromUser) return@setOnRatingBarChangeListener
            val book = currentBook ?: return@setOnRatingBarChangeListener
            currentBook = book.copy(rating = rating)
            persistCurrentBook()
        }
    }

    private fun saveProgress() {
        val book = currentBook ?: return
        val newStatus = when {
            currentPage == 0 -> "В планах"
            currentPage == 100 -> "Прочитано"
            else -> "Читаю"
        }

        val updated = book.copy(progress = currentPage, status = newStatus)
        currentBook = updated
        persistCurrentBook()
        binding.statusTextView.text = updated.status
        updatePageCounter()
    }

    private fun persistCurrentBook() {
        val book = currentBook ?: return
        dbHelper.updateBook(book)
    }

    private fun updatePageCounter() {
        binding.pageCounterTextView.text = "Текущая страница: $currentPage / $totalPages"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
