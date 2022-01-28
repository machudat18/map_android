package com.dat.map_android.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.dat.map_android.R
import com.dat.map_android.models.HappyPlaceModel
import com.dat.map_android.utils.GetFromAssets
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.item_happy_place.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast


import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.lang.Exception
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.MarkerOptions

import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.dat.map_android.DataMap
import kotlinx.android.synthetic.main.activity_map.toolbar_map
import kotlinx.android.synthetic.main.activity_map_select.*
import kotlinx.android.synthetic.main.item_happy_place.view.*


/**
 * https://www.raywenderlich.com/230-introduction-to-google-maps-api-for-android-with-kotlin
 */
class MapSelectActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnPoiClickListener {
    val currentPos = DataMap.currentPos
    var map: GoogleMap? = null
    var selectedMarker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_select)
        setSupportActionBar(toolbar_map)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Chọn địa điểm"
        toolbar_map.setNavigationOnClickListener {
            onBackPressed()
        }
        pick_location.setOnClickListener {
            setResult(RESULT_OK, intent);
            finish()
        }
        val supportMapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @SuppressLint("CheckResult")
    override fun onMapReady(googleMap: GoogleMap) {

        /**
         * Add a marker on the location using the latitude and longitude and move the camera to it.
         */
        val position = LatLng(
            currentPos!!.latitude,
            currentPos!!.longitude
        )
        val marker = googleMap.addMarker(
            MarkerOptions().apply {
                position(position)
                title(currentPos.location)
                icon(bitmapDescriptorFromVector(R.drawable.ic_baseline_location_on_24))

            }
        )
        marker.showInfoWindow()
        marker.alpha = 100f
        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.setOnPoiClickListener(this)
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
        googleMap.animateCamera(newLatLngZoom)
        map = googleMap
    }

    override fun onPoiClick(poi: PointOfInterest?) {
        Log.d(
            "TAG",
            "onPoiClick:${poi?.name}\n" +
                    "Lat : ${poi?.latLng?.latitude}\nLong :${poi?.latLng?.longitude}"
        )
        Toast.makeText(
            this, "onPoiClick:${poi?.name}\n" +
                    "Lat : ${poi?.latLng?.latitude}\nLong :${poi?.latLng?.longitude}",
            Toast.LENGTH_SHORT
        ).show()
        if (poi == null)
            return
        if (selectedMarker != null) {
            selectedMarker?.remove()
            selectedMarker = null
            DataMap.selectPos = null
        }
        DataMap.selectPos = HappyPlaceModel(
            0, "", "",
            "", poi.name, poi.latLng.latitude, poi.latLng.longitude
        )
        val position = LatLng(
            DataMap.selectPos!!.latitude,
            DataMap.selectPos!!.longitude
        )
        selectedMarker = map?.addMarker(
            MarkerOptions().apply {
                position(position)
                title(DataMap.selectPos?.location)
                icon(bitmapDescriptorFromVector(R.drawable.ic_baseline_add_location_alt_24))

            }
        )
    }

    override fun onDestroy() {

        super.onDestroy()

    }
    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(this, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}