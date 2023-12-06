package ca.unb.mobiledev.saferideapp

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse


class DetailActivity : AppCompatActivity() {
    private lateinit var placesClient: PlacesClient

    private var mapsLink = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val returnBtn = findViewById<ImageButton>(R.id.return_button)
        returnBtn.setOnClickListener {
            onBackPressed()
        }
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, MAPS_API)
        }

        // Create a new Places client instance
        placesClient = Places.createClient(this)

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val location = intent.getStringExtra("dropOffLocation")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
        }
        val loadingIndicator = findViewById<ProgressBar>(R.id.loading_indicator)

        val openMapsButton = findViewById<Button>(R.id.map_button)
        openMapsButton.setOnClickListener {
            openMapsButton.isEnabled = false
            loadingIndicator.visibility = View.VISIBLE

            getAddressCoordinates(location!!) { loc ->
                val uri: Uri = Uri.parse(mapsLink)
                try {
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val message = "app not found"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } finally {
                    openMapsButton.isEnabled = true
                    loadingIndicator.visibility = View.GONE
                }
            }
        }

        val descTextView = findViewById<TextView>(R.id.detail_textview)
        descTextView.text = getString(R.string.detail_tag, id,name,location)
        descTextView.movementMethod = ScrollingMovementMethod()
        supportActionBar?.title = id
    }
    // Function to convert address to coordinates
    private fun getAddressCoordinates(address: String, callback: (LatLng) -> Unit) {
        // Set up the request
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(address)
            .build()

        // Perform the request
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                val prediction = response.autocompletePredictions.firstOrNull()

                if (prediction != null) {
                    val placeId = prediction.placeId

                    // Fetch the place details
                    val placeRequest = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG))
                        .build()

                    placesClient.fetchPlace(placeRequest)
                        .addOnSuccessListener { fetchPlaceResponse: FetchPlaceResponse ->
                            val location = fetchPlaceResponse.place.latLng
                            val latitude = location?.latitude
                            val longitude = location?.longitude

                            mapsLink = "https://www.google.com/maps?q=$latitude,$longitude"
                            callback.invoke(location!!)

                        }
                        .addOnFailureListener { exception: Exception ->
                            val message = "not able to get the coordinates"
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                } else {
                    val message = "not able to predict"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    callback.invoke(LatLng(0.0, 0.0))
                }

            }

            .addOnFailureListener { exception: Exception ->
                val message = "prediction is null"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                callback.invoke(LatLng(0.0, 0.0))
            }
    }

    companion object {
        private const val MAPS_API = "AIzaSyCHSpCvADgv-_Qa5bnC6y2FlN8cV4qH4u4"
        private const val PERMISSION_REQUEST_LOCATION = 123
    }

}