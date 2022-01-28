package com.dat.map_android.activities

import android.annotation.SuppressLint
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
import com.dat.map_android.DataMap
import kotlinx.android.synthetic.main.item_happy_place.view.*


/**
 * https://www.raywenderlich.com/230-introduction-to-google-maps-api-for-android-with-kotlin
 */
class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnPoiClickListener {

    private var mHappyPlaceDetails: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            mHappyPlaceDetails =
                intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }

        if (mHappyPlaceDetails != null) {

            setSupportActionBar(toolbar_map)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mHappyPlaceDetails!!.title
            toolbar_map.setNavigationOnClickListener {
                onBackPressed()
            }
            val supportMapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync(this)
        }
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
    override fun onMarkerClick(marker: Marker?): Boolean {
        Log.d("TAG", "onMarkerClick: ")
        val position = marker?.tag
        val model = DataMap.list[position as Int]
        Glide.with(this).load(model.image).into(iv_place_image1)
        tvTitle1.text = model.title
        tvDescription1.text = model.description
        supportActionBar!!.title = model.title
        if (model.distance != null) {
            tv_distance1.text = "Khoảng cách : ${model.distance} m"
        }
        return false
    }

    @SuppressLint("CheckResult")
    override fun onMapReady(googleMap: GoogleMap) {

        /**
         * Add a marker on the location using the latitude and longitude and move the camera to it.
         */
        val position = LatLng(
            mHappyPlaceDetails!!.latitude,
            mHappyPlaceDetails!!.longitude
        )
        Glide.with(this).load(mHappyPlaceDetails!!.image).into(iv_place_image1)
        tvTitle1.text = mHappyPlaceDetails!!.title
        tvDescription1.text = mHappyPlaceDetails!!.description
        if (mHappyPlaceDetails!!.distance != null) {
            tv_distance1.text = "Khoảng cách : ${mHappyPlaceDetails!!.distance} m"
        }
        DataMap.list.forEachIndexed { index, it ->
            val temp = LatLng(
                it.latitude,
                it.longitude
            )
            Glide.with(this)
                .asBitmap()
                .load(it.image)
                .dontTransform()
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        val scale: Float =
                            resources.displayMetrics.density
                        val pixels = (50 * scale + 0.5f).toInt()
                        val bitmap = Bitmap.createScaledBitmap(resource!!, pixels, pixels, true)
                        val ic = BitmapDescriptorFactory.fromBitmap(bitmap)
                        val marker = googleMap.addMarker(
                            MarkerOptions().apply {
                                position(temp)
                                title(it.location)
                                icon(ic)
                            }
                        )
                        marker.tag = index
                    }
                })
        }

        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnPoiClickListener(this)
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
        googleMap.animateCamera(newLatLngZoom)
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
    }
}