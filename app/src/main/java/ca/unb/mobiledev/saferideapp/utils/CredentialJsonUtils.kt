package ca.unb.mobiledev.saferideapp.utils

import android.content.Context
import ca.unb.mobiledev.saferideapp.sampledata.CredentialData
import org.json.JSONObject
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class CredentialJsonUtils(context: Context) {
    private lateinit var credentials: ArrayList<CredentialData>


    private fun processJSON(context: Context) {
        credentials = ArrayList()
        try {
            // Create a JSON Object from file contents String
            val jsonObject = JSONObject(Objects.requireNonNull(loadJSONFromAssets(context)))

            val jsonArray = jsonObject.getJSONArray(KEY_CREDENTIALS)
            for (i in 0 until jsonArray.length()) {

                val credObject = jsonArray.getJSONObject(i)
                val credential = CredentialData(
                    credObject.getString(KEY_STUDENT_ID),
                    credObject.getString(KEY_NAME),
                    credObject.getString(KEY_USERNAME),
                    credObject.getString(KEY_PASSWORD)
                )
                credentials.add(credential)
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

    // Getter method for CredentialData ArrayList
    fun getCredentials(): ArrayList<CredentialData> {
        return credentials
    }

    companion object {
        private const val CS_JSON_FILE = "credentials.json"
        private const val KEY_CREDENTIALS = "credentials"
        private const val KEY_STUDENT_ID = "studentID"
        private const val KEY_NAME = "name"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
    }

    // Initializer to read our data source (JSON file) into an array of credential objects
    init {
        processJSON(context)
    }
}