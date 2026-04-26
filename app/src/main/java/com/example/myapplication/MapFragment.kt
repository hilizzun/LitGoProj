package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.myapplication.api.BookCatalogRepository
import com.example.myapplication.api.BookstorePoint
import com.example.myapplication.databinding.FragmentMapBinding
import kotlinx.coroutines.launch

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val repository = BookCatalogRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapSearchButton.setOnClickListener {
            val city = binding.mapSearchEditText.text?.toString().orEmpty().trim().ifBlank { "Москва" }
            loadBookstores(city)
        }

        if (binding.mapSearchEditText.text.isNullOrBlank()) {
            binding.mapSearchEditText.setText("Москва")
        }

        loadBookstores("Москва")
    }

    private fun loadBookstores(city: String) {
        if (BuildConfig.YANDEX_PLACES_API_KEY.isBlank()) {
            Toast.makeText(requireContext(), "Добавьте YANDEX_PLACES_API_KEY в local.properties", Toast.LENGTH_LONG).show()
            return
        }

        binding.mapSearchButton.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                repository.findBookstoresByCity(city, BuildConfig.YANDEX_PLACES_API_KEY)
            }.onSuccess { stores ->
                if (stores.isEmpty()) {
                    Toast.makeText(requireContext(), "Магазины не найдены", Toast.LENGTH_LONG).show()
                } else {
                    loadStaticMap(stores)
                    Toast.makeText(requireContext(), "Найдено: ${stores.size}", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(requireContext(), "Ошибка загрузки точек на карте", Toast.LENGTH_SHORT).show()
            }

            binding.mapSearchButton.isEnabled = true
        }
    }

    private fun loadStaticMap(stores: List<BookstorePoint>) {
        val staticKey = if (BuildConfig.YANDEX_STATIC_API_KEY.isNotBlank()) {
            BuildConfig.YANDEX_STATIC_API_KEY
        } else {
            BuildConfig.YANDEX_PLACES_API_KEY
        }

        if (staticKey.isBlank()) return

        val center = stores.first()
        val points = stores
            .take(80)
            .joinToString("~") { "${it.longitude},${it.latitude},pm2rdm" }

        val mapUrl = Uri.Builder()
            .scheme("https")
            .authority("static-maps.yandex.ru")
            .appendPath("v1")
            .appendQueryParameter("apikey", staticKey)
            .appendQueryParameter("ll", "${center.longitude},${center.latitude}")
            .appendQueryParameter("z", "12")
            .appendQueryParameter("size", "650,450")
            .appendQueryParameter("lang", "ru_RU")
            .appendQueryParameter("pt", points)
            .build()
            .toString()

        binding.mapImageView.load(mapUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
