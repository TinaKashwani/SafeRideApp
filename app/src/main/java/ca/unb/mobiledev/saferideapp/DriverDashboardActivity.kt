package ca.unb.mobiledev.saferideapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import ca.unb.mobiledev.saferideapp.utils.LoadDataTask
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.saferideapp.utils.DatabaseHelper
import ca.unb.mobiledev.saferideapp.utils.RiderJsonUtils
import ca.unb.mobiledev.saferideapp.utils.ScheduleDatabaseHelper
import ca.unb.mobiledev.saferideapp.utils.ScheduleJsonUtils
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DriverDashboardActivity : AppCompatActivity() {
    private var weather_url1 = ""
    private var counter: Int = 0
    private var num: Int = 0
    private lateinit var placesClient: PlacesClient


    private lateinit var recyclerView: RecyclerView
    private lateinit var myRadioGroup: RadioGroup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_dashboard)
        val name = intent.getStringExtra("Name")
        val id = intent.getStringExtra("Id")
        val descTextView = findViewById<TextView>(R.id.driverName_textview)
        descTextView.text = "Hi $name! \n"
        val logOutBtn = findViewById<Button>(R.id.log_out_button)
        logOutBtn.setOnClickListener {
            showConfirmationDialog()
        }
        recyclerView = findViewById(R.id.recycler_view)


        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, MAPS_API)
        }

        placesClient = Places.createClient(this)

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
        val viewBtn = findViewById<Button>(R.id.view_schedule_button)
        viewBtn.setOnClickListener {
            val i1 = Intent(this@DriverDashboardActivity, ScheduleActivity::class.java)
            i1.putExtra("id", id.toString())
            this.startActivity(i1)
        }
        val layoutParams = viewBtn.layoutParams as RelativeLayout.LayoutParams
        layoutParams.topMargin = 0
        viewBtn.layoutParams = layoutParams

        val weatherButton = findViewById<ImageButton>(R.id.weather_icon_button)
        val weatherLayout = findViewById<LinearLayout>(R.id.weather_layout)
        weatherButton.setOnClickListener{

            if(num%2 == 0){
                weatherButton.isEnabled = false
                getAddressCoordinates("Fredericton, NB, CA") { loc ->
                    weatherButton.isEnabled = true
                    layoutParams.topMargin = 400
                }

            }
            else{
                weatherLayout.visibility = View.GONE
                layoutParams.topMargin = 0
            }
            viewBtn.layoutParams = layoutParams
            num++
        }

        val scheduleDB = ScheduleDatabaseHelper(this, "Head Hall")
        populateScheduleValues(scheduleDB)

        val driverS = scheduleDB.getDriverSchedule(id!!)

        val mySwitch = findViewById<SwitchCompat>(R.id.mySwitch)
        val nonactiveTextView = findViewById<TextView>(R.id.nonactive_textview)
        nonactiveTextView.text = "Toggle switch to start logging your hours!"
        nonactiveTextView.visibility = View.GONE
        mySwitch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

        val currentDate = Calendar.getInstance().time
        for(i in 0 until driverS.size){
            val date = driverS[i].date
            val sTime = driverS[i].startTime.toString()
            val targetStartTime = "$date $sTime"
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val targetDateStart = sdf.parse(targetStartTime)
            val eTime = driverS[i].endTime.toString()
            val targetEndTime =  "$date $eTime"
            val targetDateEnd = sdf.parse(targetEndTime)

            if (currentDate.before(targetDateEnd) && currentDate.after(targetDateStart)) {
                mySwitch.visibility = Switch.VISIBLE
                nonactiveTextView.visibility = View.VISIBLE
                mySwitch.isEnabled = true
            } else {
                mySwitch.visibility = Switch.VISIBLE
                nonactiveTextView.visibility = View.GONE
                mySwitch.isEnabled = false
            }
        }

        val labelTextView = findViewById<TextView>(R.id.label_textview)
        labelTextView.text = "Select your working location: \n"

        myRadioGroup = findViewById(R.id.myRadioGroup)

        mySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                counter++
                if(counter == 1){ populateValues() }
                nonactiveTextView.visibility = TextView.GONE
                labelTextView.visibility = TextView.VISIBLE
                myRadioGroup.visibility = RadioGroup.VISIBLE
                recyclerView.visibility = View.VISIBLE

            } else {
                labelTextView.visibility = TextView.GONE
                myRadioGroup.visibility = RadioGroup.GONE
                nonactiveTextView.visibility = TextView.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }
        myRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.location1 -> {
                    val message = "Head Hall selected"
                    val loc = findViewById<RadioButton>(R.id.location1)
                    Toast.makeText(this@DriverDashboardActivity,message, Toast.LENGTH_SHORT).show()
                    val loadDataTask = LoadDataTask(this,  loc.text.toString())
                    loadDataTask.setRecyclerView(recyclerView)
                    loadDataTask.execute()
                }
                R.id.location2 -> {
                    val message = "SUB selected"
                    Toast.makeText(this@DriverDashboardActivity, message, Toast.LENGTH_SHORT).show()
                    val loc = findViewById<RadioButton>(R.id.location2)
                    val loadDataTask = LoadDataTask(this,  loc.text.toString())
                    loadDataTask.setRecyclerView(recyclerView)
                    loadDataTask.execute()
                }
                R.id.location3 -> {
                    val message = "STU selected"
                    Toast.makeText(this@DriverDashboardActivity, message, Toast.LENGTH_SHORT).show()
                    val loc = findViewById<RadioButton>(R.id.location3)
                    val loadDataTask = LoadDataTask(this,  loc.text.toString())
                    loadDataTask.setRecyclerView(recyclerView)
                    loadDataTask.execute()
                }
            }
        }

    }

    private fun showConfirmationDialog(){
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Sign Out Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to log out?")

        alertDialogBuilder.setPositiveButton("Confirm") { _, _ ->
            val i1 = Intent(this@DriverDashboardActivity, MainActivity::class.java)
            Toast.makeText(this@DriverDashboardActivity, "Successfully signed out!", Toast.LENGTH_SHORT).show()
            startActivity(i1)
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun populateScheduleValues(dB1:ScheduleDatabaseHelper){
        val appContext: Context = this.applicationContext
        val data = ScheduleJsonUtils(appContext)
        val list = data.getSchedule()
        if(!dB1.isTableExists("Head Hall")){
            dB1.createNewTable("Head Hall")
        }
        if(!dB1.isTableExists("SUB")){
            dB1.createNewTable("SUB")

        }
        if(!dB1.isTableExists("STU")){
            dB1.createNewTable("STU")
        }
        for (i in 0 until list.size) {
            if (list[i].location.toString().isNotEmpty()) {

                if (list[i].idItem.isNotEmpty() && list[i].nameItem.isNotEmpty() && list[i].date!!.isNotEmpty() &&
                    list[i].startTime!!.isNotEmpty() && list[i].endTime!!.isNotEmpty()) {
                    val arr = ArrayList<String>()
                    arr.add(list[i].idItem)
                    arr.add(list[i].nameItem)
                    arr.add(list[i].location!!)
                    list[i].date?.let { arr.add(it) }
                    list[i].startTime?.let { arr.add(it) }
                    list[i].endTime?.let { arr.add(it) }
                    databaseScheduleHelper(dB1, arr)
                }
            }
        }
        val message = "Students Checked In"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun databaseScheduleHelper(db:ScheduleDatabaseHelper?, arr: ArrayList<String>){
        val list = db?.getSchedule(arr[2])
        if(list != null){
            for (i in 0 until list.size) {
                if (list[i].idItem == arr[0] && list[i].date == arr[3]) {
                    db?.removeSchedule(arr[0], arr[2])
                }
            }
        }
        db?.insertSchedule(arr)
    }
    private fun populateValues(){
        val appContext: Context = this.applicationContext
        val riderData = RiderJsonUtils(appContext)
        val riderList = riderData.getRiders()
        val dB2 = DatabaseHelper(this, "Head Hall")
        if(!dB2.isTableExists("Head Hall")){
            dB2.createNewTable("Head Hall")
        }
        if(!dB2.isTableExists("SUB")){
            dB2.createNewTable("SUB")
        }
        if(!dB2.isTableExists("STU")){
            dB2.createNewTable("STU")
        }
        for (i in 0 until riderList.size) {
            if (riderList[i].location.toString().isNotEmpty() && riderList[i].pickupLocation.toString().isNotEmpty()) {

                if (riderList[i].idItem.isNotEmpty() && riderList[i].nameItem.isNotEmpty()) {

                    databaseHelper(dB2, riderList[i].idItem, riderList[i].nameItem, riderList[i].location.toString(), riderList[i].pickupLocation.toString())
                }
            }
        }
        val message = "Students Checked In"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun databaseHelper(db:DatabaseHelper?, stdId: String, name: String, location: String, tName: String){
        val list = db?.getRiders(tName)
        if(list != null){
            for (i in 0 until list.size) {
                if (list[i].idItem == stdId) {
                    db?.removeRider(stdId, tName)
                }
            }
        }
        db?.insertRider(stdId,name,location, tName)
    }

    private fun getAddressCoordinates(address: String, callback: (LatLng) -> Unit ) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(address)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                val prediction = response.autocompletePredictions.firstOrNull()

                if (prediction != null) {
                    val placeId = prediction.placeId

                    val placeRequest = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG))
                        .build()

                    placesClient.fetchPlace(placeRequest)
                        .addOnSuccessListener { fetchPlaceResponse: FetchPlaceResponse ->
                            val location = fetchPlaceResponse.place.latLng
                            weather_url1 = "https://api.open-meteo.com/v1/forecast?latitude=${location!!.latitude}&longitude=${location!!.longitude}&current=temperature_2m,rain,snowfall,wind_speed_10m&timezone=auto&forecast_days=1"
                            getWeatherData()
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

    private fun displayWeatherUI(weatherJson: JSONObject) {
        try {
            val currentWeather = weatherJson.getJSONObject("current")

            // Extract weather information
            val temperature = currentWeather.getDouble("temperature_2m")
            val rain = currentWeather.getDouble("rain")
            val snowfall = currentWeather.getDouble("snowfall")
            val windSpeed = currentWeather.getDouble("wind_speed_10m")

            // Display weather information in UI
            val weatherLayout = findViewById<LinearLayout>(R.id.weather_layout)
            weatherLayout.removeAllViews()

            addWeatherInfoToUI("Temperature", "$temperature °C", weatherLayout)
            addWeatherInfoToUI("Rainfall", "$rain mm", weatherLayout)
            addWeatherInfoToUI("Snowfall", "$snowfall cm", weatherLayout)
            addWeatherInfoToUI("Wind Speed", "$windSpeed km/h", weatherLayout)

            weatherLayout.visibility = View.VISIBLE

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun addWeatherInfoToUI(label: String, value: String, parentLayout: LinearLayout) {
        val textView = TextView(this)
        textView.text = "$label: $value"
        textView.setTextColor(Color.BLACK)
        textView.textSize = 16f
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)
        textView.layoutParams = params
        parentLayout.addView(textView)
    }

    private fun getWeatherData() {
        val queue = Volley.newRequestQueue(this)
        val url: String = weather_url1
        val stringReq = StringRequest(Request.Method.GET, url,
            { response ->
                val obj = JSONObject(response)

                displayWeatherUI(obj)
            },
            { Toast.makeText(this, "Weather could not be fetched :(", Toast.LENGTH_SHORT).show()
            })
        queue.add(stringReq)
    }

    companion object {
        private const val MAPS_API = "AIzaSyCHSpCvADgv-_Qa5bnC6y2FlN8cV4qH4u4"
        private const val PERMISSION_REQUEST_LOCATION = 12
    }

}