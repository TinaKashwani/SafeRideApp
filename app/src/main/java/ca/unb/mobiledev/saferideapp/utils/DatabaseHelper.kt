package ca.unb.mobiledev.saferideapp.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ca.unb.mobiledev.saferideapp.sampledata.WaitList

class DatabaseHelper(context: Context, private val t: String) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val table: String = t.lowercase().replace(" ", "_")

    override fun onCreate(db: SQLiteDatabase) {
        var createTableQuery = ""

        createTableQuery = "CREATE TABLE $table (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_STUDENT_ID TEXT, " +
                "$COL_NAME TEXT, " +
                "$COL_LOCATION TEXT)"

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        db.execSQL("DROP TABLE IF EXISTS $table")
        onCreate(db)
    }

    fun createNewTable( tableName: String ): Long{
        val db = this.writableDatabase
        var createTableQuery = ""

        createTableQuery = "CREATE TABLE ${tableName.lowercase().replace(" ", "_")} (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_STUDENT_ID TEXT, " +
                "$COL_NAME TEXT, " +
                "$COL_LOCATION TEXT)"

        db.execSQL(createTableQuery)
        return 1
    }

    fun insertRider(studentID: String, name: String, location: String, tableName: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_STUDENT_ID, studentID)
        contentValues.put(COL_NAME, name)
        contentValues.put(COL_LOCATION, location)
        return db.insert(tableName.lowercase().replace(" ", "_"), null, contentValues)
    }

    fun getRiders(tableName: String): ArrayList<WaitList> {
        val riders = ArrayList<WaitList>()
        val db = this.readableDatabase
        val query = "SELECT * FROM ${tableName.lowercase().replace(" ", "_")}"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val stuIdVal = cursor.getColumnIndex(COL_STUDENT_ID)
                val nameVal = cursor.getColumnIndex(COL_NAME)
                val locationVal = cursor.getColumnIndex(COL_LOCATION)

                val rider = WaitList(
                    cursor.getString(stuIdVal),
                    cursor.getString(nameVal),
                    cursor.getString(locationVal)
                )
                riders.add(rider)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return riders
    }


    fun removeRider(studentID: String, tableName: String): Int {
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
        private const val DATABASE_NAME = "safeRideDB"
        private const val COL_ID = "id"
        private const val COL_STUDENT_ID = "student_id"
        private const val COL_NAME = "name"
        private const val COL_LOCATION = "location"
    }
}

