package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.api.BookCatalogRepository
import com.example.myapplication.databinding.FragmentMapBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val repository = BookCatalogRepository()
    private var mapObjects: MapObjectCollection? = null

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

        if (BuildConfig.MAPKIT_API_KEY.isBlank()) {
            Toast.makeText(requireContext(), "Добавьте MAPKIT_API_KEY в local.properties", Toast.LENGTH_LONG).show()
            return
        }

        binding.mapView.mapWindow.map.move(
            CameraPosition(Point(55.751244, 37.618423), 11f, 0f, 0f)
        )
        mapObjects = binding.mapView.mapWindow.map.mapObjects.addCollection()

        binding.mapSearchButton.setOnClickListener {
            val city = binding.mapSearchEditText.text?.toString().orEmpty().trim().ifBlank { "Москва" }
            loadBookstores(city)
        }

        if (binding.mapSearchEditText.text.isNullOrBlank()) {
            binding.mapSearchEditText.setText("Москва")
        }

        loadBookstores("Москва")
    }

    override fun onStart() {
        super.onStart()
        if (BuildConfig.MAPKIT_API_KEY.isNotBlank()) {
            MapKitFactory.getInstance().onStart()
            _binding?.mapView?.onStart()
        }
    }

    override fun onStop() {
        _binding?.mapView?.onStop()
        if (BuildConfig.MAPKIT_API_KEY.isNotBlank()) {
            MapKitFactory.getInstance().onStop()
        }
        super.onStop()
    }

    private fun loadBookstores(city: String) {
        binding.mapSearchButton.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                repository.findBookstoresByCity(city)
            }.onSuccess { stores ->
                if (stores.isEmpty()) {
                    Toast.makeText(requireContext(), "Магазины не найдены в OSM", Toast.LENGTH_LONG).show()
                }

                val collection = mapObjects ?: return@onSuccess
                collection.clear()

                stores.forEach { store ->
                    val point = Point(store.latitude, store.longitude)
                    val placemark = collection.addPlacemark(point)
                    placemark.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.ic_map))
                }

                stores.firstOrNull()?.let { first ->
                    binding.mapView.mapWindow.map.move(
                        CameraPosition(Point(first.latitude, first.longitude), 12f, 0f, 0f)
                    )
                }

                Toast.makeText(requireContext(), "Найдено магазинов: ${stores.size}", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Ошибка загрузки точек", Toast.LENGTH_SHORT).show()
            }

            binding.mapSearchButton.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapObjects = null
        _binding = null
    }
}
