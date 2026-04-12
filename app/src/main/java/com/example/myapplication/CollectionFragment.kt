package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentCollectionBinding

class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter

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

        // Настройка RecyclerView
        setupRecyclerView()
        bookAdapter.updateBooks(getSampleBooks())

        // Обработка нажатия на FAB (Новая книга)
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_collection_to_add_book)
        }

        // Обработка клика по книге (переход к деталям)
        bookAdapter.onBookClick = { book ->
            val bundle = bundleOf(
                "bookId" to book.id,
                "title" to book.title,
                "author" to book.author,
                "genre" to book.genre,
                "status" to book.status,
                "progress" to book.progress,
                "coverRes" to book.coverRes
            )
            findNavController().navigate(R.id.action_collection_to_detail, bundle)
        }

        // Настройка фильтров (табов)
        setupFilters()
    }

    private fun getSampleBooks(): List<Book> {
        return listOf(
            Book("1", "Мастер и Маргарита", "Михаил Булгаков", "Роман", "Читаю", 63, R.drawable.cover_master),
            Book("2", "1984", "Джордж Оруэлл", "Антиутопия", "В планах", 0, R.drawable.cover_1984),
            Book("3", "Преступление и наказание", "Фёдор Достоевский", "Роман", "Прочитано", 100, R.drawable.cover_master)
        )
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter()
        binding.booksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun setupFilters() {
        binding.tabAll.setOnClickListener { updateFilter(it) }
        binding.tabReading.setOnClickListener { updateFilter(it) }
        binding.tabRead.setOnClickListener { updateFilter(it) }
        binding.tabPlanned.setOnClickListener { updateFilter(it) }
    }

    private fun updateFilter(selectedView: View) {
        // Сброс цвета всех кнопок
        listOf(binding.tabAll, binding.tabReading, binding.tabRead, binding.tabPlanned).forEach {
            it.setBackgroundColor(requireContext().getColor(R.color.surface))
        }
        // Выделение выбранной
        selectedView.setBackgroundColor(requireContext().getColor(R.color.primary))
        // TODO: фильтрация списка
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}