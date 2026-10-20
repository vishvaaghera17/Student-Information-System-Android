package com.example.studentinfo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StudentDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "studentinfo.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "students"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_ENROLLMENT = "enrollment_no"
        private const val COLUMN_DOB = "dob"
        private const val COLUMN_CONTACT = "contact"
        private const val COLUMN_COURSE = "course"
        private const val COLUMN_SEMESTER = "semester"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_ENROLLMENT TEXT,
                $COLUMN_DOB TEXT,
                $COLUMN_CONTACT TEXT,
                $COLUMN_COURSE TEXT,
                $COLUMN_SEMESTER TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    // Insert
    fun insertStudent(student: Student) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_ENROLLMENT, student.enrollmentNo)
            put(COLUMN_DOB, student.dob)
            put(COLUMN_CONTACT, student.contact)
            put(COLUMN_COURSE, student.course)
            put(COLUMN_SEMESTER, student.semester)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // Read / View all
    fun getAllStudents(): List<Student> {
        val studentList = mutableListOf<Student>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val enrollment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENROLLMENT))
            val dob = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB))
            val contact = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT))
            val course = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE))
            val semester = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEMESTER))

            studentList.add(Student(id, name, enrollment, dob, contact, course, semester))
        }
        cursor.close()
        db.close()
        return studentList
    }

    // Read one, by id (used to pre-fill the Update screen)
    fun getStudentById(studentId: Int): Student {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $studentId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        val enrollment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENROLLMENT))
        val dob = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB))
        val contact = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT))
        val course = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE))
        val semester = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEMESTER))

        cursor.close()
        db.close()
        return Student(id, name, enrollment, dob, contact, course, semester)
    }

    // Update
    fun updateStudent(student: Student) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_ENROLLMENT, student.enrollmentNo)
            put(COLUMN_DOB, student.dob)
            put(COLUMN_CONTACT, student.contact)
            put(COLUMN_COURSE, student.course)
            put(COLUMN_SEMESTER, student.semester)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(student.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    // Delete
    fun deleteStudent(studentId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(studentId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}