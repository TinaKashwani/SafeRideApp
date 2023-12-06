package ca.unb.mobiledev.saferideapp.utils

import android.content.Context
import ca.unb.mobiledev.saferideapp.sampledata.WaitList
import org.json.JSONObject
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class RiderJsonUtils(context: Context) {
    private lateinit var riders: ArrayList<WaitList>


    private fun processJSON(context: Context) {
        riders = ArrayList()
        try {
            val jsonObject = JSONObject(Objects.requireNonNull(loadJSONFromAssets(context)))

            val jsonArray = jsonObject.getJSONArray(KEY_RIDERS)
            for (i in 0 until jsonArray.length()) {

                val riderObject = jsonArray.getJSONObject(i)
                val rider = WaitList(
                    riderObject.getString(KEY_STUDENT_ID),
                    riderObject.getString(KEY_NAME),
                    riderObject.getString(KEY_DROPOFF),
                    riderObject.getString(KEY_PICKUP)
                )
                riders.add(rider)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun loadJSONFromAssets(context: Context): String? {

        try {
            val assetManager = context.assets
            val inputStream: InputStream = assetManager.open(CS_JSON_FILE)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun getRiders(): ArrayList<WaitList> {
        return riders
    }

    companion object {
        private const val CS_JSON_FILE = "riders.json"
        private const val KEY_RIDERS = "riders"
        private const val KEY_STUDENT_ID = "studentID"
        private const val KEY_NAME = "name"
        private const val KEY_PICKUP = "pickup"
        private const val KEY_DROPOFF = "location"
    }

    init {
        processJSON(context)
    }
}