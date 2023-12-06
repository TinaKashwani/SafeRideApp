package ca.unb.mobiledev.saferideapp.utils


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ca.unb.mobiledev.saferideapp.ListAdapter
import ca.unb.mobiledev.saferideapp.sampledata.WaitList
import java.util.concurrent.Executors

class LoadDataTask(private val activity: AppCompatActivity,  private val locationPickUp: String?) {
    private val appContext: Context = activity.applicationContext
    private var recyclerView: RecyclerView? = null

    fun setRecyclerView(recyclerView: RecyclerView?): LoadDataTask {
        this.recyclerView = recyclerView
        return this
    }

    fun execute() {
        Executors.newSingleThreadExecutor()
            .execute {
                val mainHandler = Handler(Looper.getMainLooper())

                val dbHelper = DatabaseHelper(appContext, locationPickUp!!)
                val riderList = dbHelper.getRiders(locationPickUp)
                mainHandler.post {
                    updateDisplay(riderList!!)
                }
            }
    }

    private fun updateDisplay(riderList: ArrayList<WaitList>) {

        val layoutManager = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = ListAdapter(activity, riderList, locationPickUp!!)

        Toast.makeText(appContext, "$locationPickUp Database loaded successfully", Toast.LENGTH_SHORT).show()
    }

}
