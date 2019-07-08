package com.example.travelbook

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

var namesArray = ArrayList<String>()
var locArray = ArrayList<LatLng>()

class MainActivity : AppCompatActivity() {

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try{
            val database = openOrCreateDatabase("Places", Context.MODE_PRIVATE, null)
            val cursor = database.rawQuery("SELECT * FROM  places", null)
            val nameIndex = cursor.getColumnIndex("name")
            val latIndex = cursor.getColumnIndex("lat")
            val longIndex = cursor.getColumnIndex("long")
            cursor.moveToFirst()

            namesArray.clear()
            locArray.clear()

            do {
                val latFromDB = cursor.getString(latIndex).toDouble()
                val longFromDB = cursor.getString(longIndex).toDouble()
                namesArray.add(cursor.getString(nameIndex))
                locArray.add(LatLng(latFromDB, longFromDB))
            }while (cursor.moveToNext())

        }catch (e : Exception){
            e.printStackTrace()
        }

        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, namesArray)

        listView.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("info", "old")
            intent.putExtra("name", namesArray[i])
            intent.putExtra("lat", locArray[i].latitude)
            intent.putExtra("long", locArray[i].longitude)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_place, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_place){
            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("Recycle")
    override fun onResume() {
        try{
            val database = openOrCreateDatabase("Places", Context.MODE_PRIVATE, null)
            val cursor = database.rawQuery("SELECT * FROM  places", null)
            val nameIndex = cursor.getColumnIndex("name")
            val latIndex = cursor.getColumnIndex("lat")
            val longIndex = cursor.getColumnIndex("long")
            cursor.moveToFirst()

            namesArray.clear()
            locArray.clear()

            do {
                val latFromDB = cursor.getString(latIndex).toDouble()
                val longFromDB = cursor.getString(longIndex).toDouble()
                namesArray.add(cursor.getString(nameIndex))
                locArray.add(LatLng(latFromDB, longFromDB))
            }while (cursor.moveToNext())

        }catch (e : Exception){
            e.printStackTrace()
        }

        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, namesArray)

        listView.setOnItemClickListener { _, _, i, _ ->
            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("info", "old")
            intent.putExtra("name", namesArray[i])
            intent.putExtra("lat", locArray[i].latitude)
            intent.putExtra("long", locArray[i].longitude)
            startActivity(intent)
        }

        super.onResume()
    }
}
