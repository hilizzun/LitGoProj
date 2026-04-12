package com.example.myapplication

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentAddBookBinding

class AddBookFragment : Fragment() {

    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!

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

        val genres = arrayOf("Роман", "Детектив", "Фантастика", "Научная литература", "Поэзия")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, genres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genreSpinner.adapter = adapter

        // Обработка кнопки "Добавить книгу"
        binding.submitButton.setOnClickListener {
            // TODO: Сохранить данные книги
            // Вернуться назад
            findNavController().popBackStack()
        }

        // Обработка кнопки загрузки обложки
        binding.uploadCoverButton.setOnClickListener {
            // TODO: Открыть файловый менеджер для выбора обложки
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}