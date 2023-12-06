package ca.unb.mobiledev.saferideapp

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TableLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.saferideapp.utils.LoadScheduleData

class ScheduleActivity: AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private val dbName = "scheduleDB"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        val returnBtn = findViewById<ImageButton>(R.id.return_button)
        returnBtn.setOnClickListener {
            onBackPressed()
        }

        tableLayout = findViewById(R.id.schedule_table)
        val id = intent.getStringExtra("id")
        val loadDataTask = LoadScheduleData(this,id.toString())
        loadDataTask.setTableLayout(tableLayout)
        loadDataTask.execute()
    }
}