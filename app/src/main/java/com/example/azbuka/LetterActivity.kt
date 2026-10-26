package com.example.azbuka

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/** Экран 2: подробная информация о букве. */
class LetterActivity : AppCompatActivity() {

    private lateinit var sound: SoundHelper
    private lateinit var progress: Progress

    private lateinit var tvLetter: TextView
    private lateinit var tvEmoji: TextView
    private lateinit var tvWord: TextView

    private var index = 0
    private var pictureShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter)

        sound = SoundHelper(this)
        progress = Progress(this)

        // Сохранение состояния при повороте: текущая буква и показана ли картинка
        index = savedInstanceState?.getInt(KEY_INDEX) ?: intent.getIntExtra(EXTRA_INDEX, 0)
        pictureShown = savedInstanceState?.getBoolean(KEY_SHOWN) ?: false

        tvLetter = findViewById(R.id.tvLetter)
        tvEmoji = findViewById(R.id.tvEmoji)
        tvWord = findViewById(R.id.tvWord)

        tvLetter.setOnClickListener { revealPicture(animate = true, playSound = true) }
        findViewById<ImageButton>(R.id.btnSound).setOnClickListener {
            sound.speak(Alphabet.letters[index])
        }
        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnPrev).setOnClickListener { moveTo(index - 1) }
        findViewById<MaterialButton>(R.id.btnNext).setOnClickListener { moveTo(index + 1) }

        bind()
    }

    private fun moveTo(newIndex: Int) {
        val size = Alphabet.letters.size
        index = (newIndex + size) % size
        pictureShown = false
        sound.stop()
        bind()
    }

    private fun bind() {
        val letter = Alphabet.letters[index]
        tvLetter.text = letter.title
        tvEmoji.text = letter.emoji
        tvWord.text = letter.word
        if (pictureShown) {
            revealPicture(animate = false, playSound = false)
        } else {
            tvEmoji.visibility = View.INVISIBLE
            tvWord.visibility = View.INVISIBLE
        }
    }

    private fun revealPicture(animate: Boolean, playSound: Boolean) {
        val letter = Alphabet.letters[index]
        pictureShown = true
        tvEmoji.visibility = View.VISIBLE
        tvWord.visibility = View.VISIBLE

        if (animate) {
            val anim = ScaleAnimation(
                0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 600
                interpolator = OvershootInterpolator()
            }
            tvEmoji.startAnimation(anim)
        }
        if (playSound) sound.speak(letter)
        progress.markLearned(letter.key)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, index)
        outState.putBoolean(KEY_SHOWN, pictureShown)
    }

    override fun onDestroy() {
        sound.release()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_INDEX = "extra_index"
        private const val KEY_INDEX = "key_index"
        private const val KEY_SHOWN = "key_shown"
    }
}
