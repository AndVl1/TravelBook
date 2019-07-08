package com.example.travelbook

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var locationManager : LocationManager? = null
    var locationListener : LocationListener? = null

    private val clickListener = GoogleMap.OnMapLongClickListener { p0 ->
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        var adress = ""
        try {
            val adressList = geocoder.getFromLocation(p0!!.latitude, p0.longitude, 1)
            if (!adressList.isNullOrEmpty() && adressList[0].thoroughfare != null) {
                adress += adressList[0].thoroughfare
                if (adressList[0].subThoroughfare != null){
                    adress += " " + adressList[0].subThoroughfare
                }
            } else
                adress = "New Place"
        }catch (e : Exception){
            e.printStackTrace()
        }
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0!!).title(adress))

        namesArray.add(adress)
        locArray.add(LatLng(p0.latitude, p0.longitude))

        Toast.makeText(applicationContext, "new place created", Toast.LENGTH_LONG).show()

        try {
            val database = openOrCreateDatabase("Places", Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS places (name VARCHAR, lat VARCHAR, long VARCHAR)")
            val toCompile = "INSERT INTO places (name, lat, long) VALUES (?, ?, ?)"
            val sqlStatement = database.compileStatement(toCompile)
            sqlStatement.bindString(1, adress)
            sqlStatement.bindString(2, p0.latitude.toString())
            sqlStatement.bindString(3, p0.longitude.toString())
            sqlStatement.execute()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(clickListener)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location?) {
                if (p0 != null) {
                    val userLocation = LatLng(p0.latitude, p0.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Your location"))
                }
            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

            override fun onProviderEnabled(p0: String?) {}

            override fun onProviderDisabled(p0: String?) {}

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this ,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }else{
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2f, locationListener!!)
            val intent = intent
            if (intent.getStringExtra("info") == "new"){
                mMap.clear()
                val lastLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val lastUserLocation = LatLng(lastLocation!!.latitude, lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                mMap.addMarker(MarkerOptions().position(lastUserLocation))
            }else {
                mMap.clear()
                val lat = intent.getDoubleExtra("lat", 0.0)
                val long = intent.getDoubleExtra("long", 0.0)
                val name = intent.getStringExtra("name")
                mMap.addMarker(MarkerOptions().position(LatLng(lat, long)).title(name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, long), 15f))
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty()){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2f, locationListener!!)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
