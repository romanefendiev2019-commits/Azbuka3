package com.example.azbuka

import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/** Экран 1: сетка букв (GridView). */
class MainActivity : AppCompatActivity() {

    private lateinit var progress: Progress
    private lateinit var adapter: LetterAdapter
    private lateinit var tvProgress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress = Progress(this)
        tvProgress = findViewById(R.id.tvProgress)

        adapter = LetterAdapter(this, Alphabet.letters)
        val grid = findViewById<GridView>(R.id.gridLetters)
        grid.adapter = adapter
        grid.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, LetterActivity::class.java)
                .putExtra(LetterActivity.EXTRA_INDEX, position)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnGame).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateProgress()
    }

    private fun updateProgress() {
        val learned = progress.learned()
        adapter.learnedKeys = learned
        adapter.notifyDataSetChanged()
        tvProgress.text = getString(R.string.progress_format, learned.size, Alphabet.letters.size)
    }
}
