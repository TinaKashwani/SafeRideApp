package ca.unb.mobiledev.saferideapp.utils


import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import ca.unb.mobiledev.saferideapp.R
import ca.unb.mobiledev.saferideapp.sampledata.Schedule
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.Executors

class LoadScheduleData(private val activity: AppCompatActivity, private val studentId: String?) {
    private val appContext: Context = activity.applicationContext
    private var tableLayout: TableLayout? = null

    fun setTableLayout(tableLayout: TableLayout?): LoadScheduleData {
        this.tableLayout = tableLayout
        return this
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun execute() {
        Executors.newSingleThreadExecutor()
            .execute {
                val mainHandler = Handler(Looper.getMainLooper())
                val dbHelper = ScheduleDatabaseHelper(appContext, "head_hall")
                val scheduleList = dbHelper.getDriverSchedule(studentId!!)
                mainHandler.post {
                    updateScheduleDisplay(scheduleList)
                }

            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateScheduleDisplay(scheduleList: ArrayList<Schedule>) {

        for (i in 0 until scheduleList.size) {

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(scheduleList[i].date, formatter)
            val dayOfWeek = date.dayOfWeek

            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val startTimeString = scheduleList[i].startTime?.let { inputFormat.parse(it) }
            val startTime = startTimeString?.let { outputFormat.format(it) }

            val endTimeString = scheduleList[i].endTime?.let { inputFormat.parse(it) }
            val endTime = endTimeString?.let { outputFormat.format(it) }

            val row = TableRow(appContext)

            val dayTextView = TextView(appContext)
            dayTextView.text = dayOfWeek.toString()
            dayTextView.setPadding(15, 15, 15, 15)
            dayTextView.setTextColor(ContextCompat.getColor(appContext, R.color.thumbColor))

            val dateTextView = TextView(appContext)
            dateTextView.text = scheduleList[i].date
            dateTextView.setPadding(12, 12, 12, 12)
            dateTextView.setTextColor(ContextCompat.getColor(appContext, R.color.thumbColor))

            val timingsTextView = TextView(appContext)
            timingsTextView.text = "$startTime-$endTime"
            timingsTextView.setPadding(15, 15, 15, 15)
            timingsTextView.setTextColor(ContextCompat.getColor(appContext, R.color.thumbColor))

            val placeTextView = TextView(appContext)
            placeTextView.text = scheduleList[i].location
            placeTextView.setPadding(40, 40, 40, 40)
            placeTextView.setTextColor(ContextCompat.getColor(appContext, R.color.thumbColor))


            row.addView(dayTextView)
            row.addView(dateTextView)
            row.addView(timingsTextView)
            row.addView(placeTextView)

            row.visibility = View.VISIBLE
            tableLayout?.addView(row)

        }

        Toast.makeText(appContext, "Schedule loaded successfully", Toast.LENGTH_SHORT).show()

    }
}