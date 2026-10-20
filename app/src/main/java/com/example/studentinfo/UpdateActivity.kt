package com.example.studentinfo

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class UpdateActivity : AppCompatActivity() {

    private lateinit var db: StudentDatabaseHelper

    private lateinit var etName: EditText
    private lateinit var etEnrollment: EditText
    private lateinit var etDob: EditText
    private lateinit var etContact: EditText
    private lateinit var spCourse: Spinner
    private lateinit var spSemester: Spinner

    private var studentId: Int = -1

    private val coursePlaceholder = "Select Course"
    private val semesterPlaceholder = "Select Semester"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        db = StudentDatabaseHelper(this)

        etName = findViewById(R.id.etName)
        etEnrollment = findViewById(R.id.etEnrollment)
        etDob = findViewById(R.id.etDob)
        etContact = findViewById(R.id.etContact)
        spCourse = findViewById(R.id.spCourse)
        spSemester = findViewById(R.id.spSemester)
        val updateBtn = findViewById<Button>(R.id.updateBtn)

        studentId = intent.getIntExtra("student_id", -1)
        if (studentId == -1) {
            finish()
            return
        }

        val student = db.getStudentById(studentId)

        etName.setText(student.name)
        etEnrollment.setText(student.enrollmentNo)
        etDob.setText(student.dob)
        etContact.setText(student.contact)

        setupCourseSpinner(preselectedCourse = student.course, preselectedSemester = student.semester)

        etDob.setOnClickListener { showDatePicker() }

        updateBtn.setOnClickListener {
            if (validateInputs()) {
                val updatedStudent = Student(
                    id = studentId,
                    name = etName.text.toString().trim(),
                    enrollmentNo = etEnrollment.text.toString().trim(),
                    dob = etDob.text.toString().trim(),
                    contact = etContact.text.toString().trim(),
                    course = spCourse.selectedItem.toString(),
                    semester = spSemester.selectedItem.toString()
                )
                db.updateStudent(updatedStudent)
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupCourseSpinner(preselectedCourse: String, preselectedSemester: String) {
        val courses = listOf(coursePlaceholder, "BCA", "MCA")
        val courseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courses)
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCourse.adapter = courseAdapter
        spCourse.setSelection(courses.indexOf(preselectedCourse).coerceAtLeast(0))

        spCourse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedCourse = courses[position]
                val semesters = when (selectedCourse) {
                    "BCA" -> listOf(semesterPlaceholder, "1", "2", "3", "4", "5", "6")
                    "MCA" -> listOf(semesterPlaceholder, "1", "2", "3", "4")
                    else -> listOf(semesterPlaceholder)
                }
                val semesterAdapter = ArrayAdapter(this@UpdateActivity, android.R.layout.simple_spinner_item, semesters)
                semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spSemester.adapter = semesterAdapter

                // Re-select the student's existing semester the first time this runs
                val semIndex = semesters.indexOf(preselectedSemester)
                if (semIndex >= 0) spSemester.setSelection(semIndex)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                etDob.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = System.currentTimeMillis() - 1
        dialog.show()
    }

    private fun validateInputs(): Boolean {
        val name = etName.text.toString().trim()
        val enrollment = etEnrollment.text.toString().trim()
        val dob = etDob.text.toString().trim()
        val contact = etContact.text.toString().trim()

        if (name.isEmpty() || !name.matches(Regex("^[a-zA-Z ]+$"))) {
            etName.error = "Name must contain only alphabets"
            return false
        }
        if (!enrollment.matches(Regex("^\\d{15}$"))) {
            etEnrollment.error = "Enrolment No. must be exactly 15 digits"
            return false
        }
        if (dob.isEmpty()) {
            Toast.makeText(this, "Please select date of birth", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!contact.matches(Regex("^\\d{10}$"))) {
            etContact.error = "Contact No. must be exactly 10 digits"
            return false
        }
        if (spCourse.selectedItem.toString() == coursePlaceholder) {
            Toast.makeText(this, "Please select a course", Toast.LENGTH_SHORT).show()
            return false
        }
        if (spSemester.selectedItem.toString() == semesterPlaceholder) {
            Toast.makeText(this, "Please select a semester", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}