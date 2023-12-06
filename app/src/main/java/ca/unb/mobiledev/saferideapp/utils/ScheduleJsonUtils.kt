package ca.unb.mobiledev.saferideapp.utils

import android.content.Context
import ca.unb.mobiledev.saferideapp.sampledata.Schedule
import org.json.JSONObject
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class ScheduleJsonUtils(context: Context) {
    private lateinit var schedule: ArrayList<Schedule>


    private fun processJSON(context: Context) {
        schedule = ArrayList()
        try {
            val jsonObject = JSONObject(Objects.requireNonNull(loadJSONFromAssets(context)))

            val jsonArray = jsonObject.getJSONArray(KEY_SCHEDULE)
            for (i in 0 until jsonArray.length()) {

                val scheduleObject = jsonArray.getJSONObject(i)
                val obj = Schedule(
                    scheduleObject.getString(KEY_STUDENT_ID),
                    scheduleObject.getString(KEY_NAME),
                    scheduleObject.getString(KEY_LOCATION),
                    scheduleObject.getString(KEY_DATE),
                    scheduleObject.getString(KEY_START_TIME),
                    scheduleObject.getString(KEY_END_TIME)

                )


                schedule.add(obj)
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

    fun getSchedule(): ArrayList<Schedule> {
        return schedule
    }

    companion object {
        private const val CS_JSON_FILE = "schedule.json"
        private const val KEY_SCHEDULE = "schedule"
        private const val KEY_STUDENT_ID = "studentID"
        private const val KEY_NAME = "name"
        private const val KEY_DATE = "date"
        private const val KEY_START_TIME = "startTime"
        private const val KEY_END_TIME = "endTime"
        private const val KEY_LOCATION = "location"
    }

    init {
        processJSON(context)
    }
}