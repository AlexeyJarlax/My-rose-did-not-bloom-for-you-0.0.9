package com.pavlovalexey.pleinair.calendar.ui.event

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pavlovalexey.pleinair.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EventMapFragment : Fragment(), OnMapReadyCallback {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var mMap: GoogleMap
    private var selectedLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_map, container, false)

        // Инициализация карты
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Обработка нажатия на кнопку подтверждения местоположения
        view.findViewById<Button>(R.id.btn_confirm_location).setOnClickListener {
            selectedLocation?.let {
                // Передаем координаты обратно в NewEventFragment через FragmentResultAPI
                val resultBundle = Bundle().apply {
                    saveToSharedPreferences("eventLatitude", it.latitude.toFloat())
                    putDouble("latitude", it.latitude)
                    saveToSharedPreferences("eventLongitude", it.longitude.toFloat())
                    putDouble("longitude", it.longitude)
                }
                parentFragmentManager.setFragmentResult("locationRequestKey", resultBundle)
                parentFragmentManager.popBackStack()
            } ?: run {
                Toast.makeText(requireContext(), "Выберите местоположение", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    fun saveToSharedPreferences(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Получаем координаты из аргументов, если переданы
        val latitude = arguments?.getDouble("latitude")
        val longitude = arguments?.getDouble("longitude")

        val defaultLatitude = 55.75
        val defaultLongitude = 37.61
        val initialPosition = if (latitude != null && longitude != null) {
            LatLng(latitude, longitude)
        } else {
            LatLng(defaultLatitude, defaultLongitude)
        }

        // Устанавливаем начальную позицию и уровень зума
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 12f))

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Выбрано местоположение"))
            selectedLocation = latLng
        }
    }
}
