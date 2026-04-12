package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentCollectionBinding

class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter
    private lateinit var dbHelper: AppDatabaseHelper
    private var allBooks: List<Book> = emptyList()
    private var activeFilter: String = FILTER_ALL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = AppDatabaseHelper.getInstance(requireContext())
        setupRecyclerView()
        setupFilters()
        setupSearch()
        loadBooks()

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_collection_to_add_book)
        }

        bookAdapter.onBookClick = { book ->
            val bundle = bundleOf("bookId" to book.id)
            findNavController().navigate(R.id.action_collection_to_detail, bundle)
        }

        bookAdapter.onBookLongClick = { book ->
            showDeleteDialog(book)
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::dbHelper.isInitialized) {
            loadBooks()
        }
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter()
        binding.booksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun setupSearch() {
        binding.searchEditText.doAfterTextChanged {
            applyFilterAndSearch()
        }
    }

    private fun setupFilters() {
        binding.tabAll.setOnClickListener { setFilter(FILTER_ALL, it) }
        binding.tabReading.setOnClickListener { setFilter(FILTER_READING, it) }
        binding.tabRead.setOnClickListener { setFilter(FILTER_READ, it) }
        binding.tabPlanned.setOnClickListener { setFilter(FILTER_PLANNED, it) }
        setFilter(FILTER_ALL, binding.tabAll)
    }

    private fun setFilter(filter: String, selectedView: View) {
        activeFilter = filter
        listOf(binding.tabAll, binding.tabReading, binding.tabRead, binding.tabPlanned).forEach {
            it.setBackgroundColor(requireContext().getColor(R.color.surface))
        }
        selectedView.setBackgroundColor(requireContext().getColor(R.color.primary))
        applyFilterAndSearch()
    }

    private fun loadBooks() {
        allBooks = dbHelper.getAllBooks()
        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val query = binding.searchEditText.text?.toString().orEmpty().trim()
        val filtered = allBooks.filter { book ->
            val filterMatch = activeFilter == FILTER_ALL || book.status == activeFilter
            val searchMatch = query.isBlank() ||
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true)
            filterMatch && searchMatch
        }

        bookAdapter.updateBooks(filtered)
    }

    private fun showDeleteDialog(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить книгу")
            .setMessage("Книга \"${book.title}\" будет удалена из коллекции.")
            .setPositiveButton("Удалить") { _, _ ->
                dbHelper.deleteBook(book.id)
                loadBooks()
                Toast.makeText(requireContext(), "Книга удалена", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val FILTER_ALL = "ALL"
        private const val FILTER_READING = "Читаю"
        private const val FILTER_READ = "Прочитано"
        private const val FILTER_PLANNED = "В планах"
    }
}
