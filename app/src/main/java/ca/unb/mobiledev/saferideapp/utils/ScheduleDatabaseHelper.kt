package ca.unb.mobiledev.saferideapp.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ca.unb.mobiledev.saferideapp.sampledata.Schedule

class ScheduleDatabaseHelper(context: Context, private val t: String) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val table: String = t.lowercase().replace(" ", "_")

    override fun onCreate(db: SQLiteDatabase) {
        var createTableQuery = ""

        createTableQuery = "CREATE TABLE $table (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_STUDENT_ID TEXT, " +
                "$COL_NAME TEXT, " +
                "$COL_LOCATION TEXT, " +
                "$COL_DATE TEXT, " +
                "$COL_START_TIME TEXT, " +
                "$COL_END_TIME TEXT)"


        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        db.execSQL("DROP TABLE IF EXISTS $table")
        onCreate(db)
    }

    fun createNewTable( tableName: String ): Long{
        val db = this.writableDatabase

        val createTableQuery = "CREATE TABLE ${tableName.lowercase().replace(" ", "_")} (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_STUDENT_ID TEXT, " +
                "$COL_NAME TEXT, " +
                "$COL_LOCATION TEXT, " +
                "$COL_DATE TEXT, " +
                "$COL_START_TIME TEXT, " +
                "$COL_END_TIME TEXT)"

        db.execSQL(createTableQuery)
        return 1
    }


    fun insertSchedule(values: ArrayList<String>): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_STUDENT_ID, values[0])
        contentValues.put(COL_NAME, values[1])
        contentValues.put(COL_LOCATION, values[2])
        contentValues.put(COL_DATE, values[3])
        contentValues.put(COL_START_TIME, values[4])
        contentValues.put(COL_END_TIME, values[5])

        return db.insert(values[2].lowercase().replace(" ", "_"), null, contentValues)
    }

    fun getSchedule(tableName: String): ArrayList<Schedule> {
        val driverSchedule = ArrayList<Schedule>()
        val db = this.readableDatabase
        val query = "SELECT * FROM ${tableName.lowercase().replace(" ", "_")}"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val stuIdVal = cursor.getColumnIndex(COL_STUDENT_ID)

                val nameVal = cursor.getColumnIndex(COL_NAME)
                val locationVal = cursor.getColumnIndex(COL_LOCATION)
                val dateVal = cursor.getColumnIndex(COL_DATE)
                val startVal = cursor.getColumnIndex(COL_START_TIME)
                val endVal = cursor.getColumnIndex(COL_END_TIME)

                val d = Schedule(
                    cursor.getString(stuIdVal),
                    cursor.getString(nameVal),
                    cursor.getString(locationVal),
                    cursor.getString(dateVal),
                    cursor.getString(startVal),
                    cursor.getString(endVal)
                )
                driverSchedule.add(d)


            } while (cursor.moveToNext())
        }
        cursor.close()
        return driverSchedule
    }

    fun getDriverSchedule(studentID: String): ArrayList<Schedule> {

        var driverSchedule = ArrayList<Schedule>()
        val query1 = "SELECT * FROM head_hall WHERE $COL_STUDENT_ID = $studentID"
        val query2 = "SELECT * FROM sub WHERE $COL_STUDENT_ID = $studentID"
        val query3 = "SELECT * FROM stu WHERE $COL_STUDENT_ID = $studentID"

        driverSchedule = runLoop(query1,studentID, driverSchedule)
        driverSchedule = runLoop(query2,studentID, driverSchedule)
        driverSchedule = runLoop(query3,studentID, driverSchedule)

        return driverSchedule
    }
    private fun runLoop(query: String, studentID: String, driverSchedule: ArrayList<Schedule>): ArrayList<Schedule>{
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val stuIdVal = cursor.getColumnIndex(COL_STUDENT_ID)
                val nameVal = cursor.getColumnIndex(COL_NAME)
                val locationVal = cursor.getColumnIndex(COL_LOCATION)
                val dateVal = cursor.getColumnIndex(COL_DATE)
                val startVal = cursor.getColumnIndex(COL_START_TIME)
                val endVal = cursor.getColumnIndex(COL_END_TIME)

                val d = Schedule(
                    cursor.getString(stuIdVal),
                    cursor.getString(nameVal),
                    cursor.getString(locationVal),
                    cursor.getString(dateVal),
                    cursor.getString(startVal),
                    cursor.getString(endVal)
                )

                driverSchedule.add(d)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return driverSchedule
    }

    fun removeSchedule(studentID: String, tableName: String): Int {
        val db = this.writableDatabase
        val tN = tableName.lowercase().replace(" ", "_")
        db.execSQL("CREATE TABLE temp_$tN AS SELECT * FROM $tN WHERE $COL_STUDENT_ID != ?", arrayOf(studentID))

        db.execSQL("DROP TABLE $tN")

        db.execSQL("ALTER TABLE temp_$tN RENAME TO $tN")
        return 1
    }

    fun isTableExists(tableName: String): Boolean {
        val db = this.readableDatabase
        val tN = tableName.lowercase().replace(" ", "_")
        val query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?"
        val cursor = db.rawQuery(query, arrayOf(tN))

        val tableExists = cursor.moveToFirst()

        cursor.close()

        return tableExists
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "scheduleDB"
        private const val COL_ID = "id"
        private const val COL_STUDENT_ID = "student_id"
        private const val COL_NAME = "name"
        private const val COL_LOCATION = "location"
        private const val COL_DATE = "date"
        private const val COL_START_TIME = "startTime"
        private const val COL_END_TIME = "endTime"

    }
}

