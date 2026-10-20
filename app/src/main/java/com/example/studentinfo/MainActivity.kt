package com.example.studentinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: StudentDatabaseHelper
    private lateinit var listView: ListView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: Studentadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = StudentDatabaseHelper(this)
        listView = findViewById(R.id.studentListView)
        tvEmpty = findViewById(R.id.tvEmpty)
        val addBtn = findViewById<Button>(R.id.addBtn)

        // Edit icon on a row opens UpdateActivity for that student
        adapter = Studentadapter(this, db.getAllStudents()) { student ->
            val intent = Intent(this, UpdateActivity::class.java)
            intent.putExtra("student_id", student.id)
            startActivity(intent)
        }
        listView.adapter = adapter

        addBtn.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        refreshEmptyState()
    }

    // Reload the list every time we return to this screen (after add/update/delete)
    override fun onResume() {
        super.onResume()
        adapter.updateData(db.getAllStudents())
        refreshEmptyState()
    }

    private fun refreshEmptyState() {
        val isEmpty = db.getAllStudents().isEmpty()
        tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}