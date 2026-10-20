package com.example.studentinfo

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class Studentadapter(
    private val context: Context,
    private var students: List<Student>,
    private val onEditClick: (Student) -> Unit
) : ArrayAdapter<Student>(context, R.layout.item_student, students) {

    private val db = StudentDatabaseHelper(context)

    // Called by MainActivity after an insert/update/delete to redraw the list
    fun updateData(newList: List<Student>) {
        students = newList
        clear()
        addAll(newList)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_student, parent, false)

        val student = students[position]

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvEnrollment = view.findViewById<TextView>(R.id.tvEnrollment)
        val tvCourseSem = view.findViewById<TextView>(R.id.tvCourseSem)
        val ivEdit = view.findViewById<ImageView>(R.id.ivEdit)
        val ivDelete = view.findViewById<ImageView>(R.id.ivDelete)

        tvName.text = student.name
        tvEnrollment.text = "Enrolment No: ${student.enrollmentNo}"
        tvCourseSem.text = "${student.course} - Semester ${student.semester}"

        // Edit icon opens UpdateActivity for this student
        ivEdit.setOnClickListener {
            onEditClick(student)
        }

        // Delete icon asks for confirmation before removing the record
        ivDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete ${student.name}'s record?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    db.deleteStudent(student.id)
                    Toast.makeText(context, "${student.name} deleted", Toast.LENGTH_SHORT).show()
                    updateData(db.getAllStudents())
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        return view
    }
}