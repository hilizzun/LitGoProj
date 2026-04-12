package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var currentPage = 205
    private var totalPages = 390

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

        arguments?.let {
            val title = it.getString("title", "")
            val author = it.getString("author", "")
            val genre = it.getString("genre", "")
            val status = it.getString("status", "")
            val progress = it.getInt("progress", 0)
            val coverRes = it.getInt("coverRes", R.drawable.cover_master)

            binding.bookTitleTextView.text = title
            binding.authorTextView.text = author
            binding.genreTextView.text = genre
            binding.statusTextView.text = status
            binding.bookCoverImageView.setImageResource(coverRes)

            // Пример для страниц (в реальности нужно знать общее количество страниц)
            val totalPages = 390 // или получить из аргументов
            binding.pageCounterTextView.text = "Текущая страница: $progress / $totalPages"
        }

        // Обработка кнопки "Назад"
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Обработка кнопок +/- для страниц
        binding.incrementButton.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                updatePageCounter()
            }
        }

        binding.decrementButton.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                updatePageCounter()
            }
        }
    }

    private fun updatePageCounter() {
        val progress = if (totalPages > 0) (currentPage * 100 / totalPages) else 0
        binding.pageCounterTextView.text = "Текущая страница: $currentPage / $totalPages"
        // TODO: обновить прогресс в книге (пока просто текст)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}