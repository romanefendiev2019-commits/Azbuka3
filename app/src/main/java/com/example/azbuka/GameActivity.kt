package com.example.azbuka

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Экран 3: игра «Найди букву».
 * Режим 0: показана буква – выбери картинку.
 * Режим 1: показана картинка – выбери букву.
 */
class GameActivity : AppCompatActivity() {

    private lateinit var sound: SoundHelper
    private lateinit var progress: Progress

    private lateinit var tvRound: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvBest: TextView
    private lateinit var tvHint: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var optionViews: List<TextView>

    private val pool = Alphabet.gameLetters
    private val handler = Handler(Looper.getMainLooper())
    private val nextRunnable = Runnable { goNext() }
    private var resultDialog: AlertDialog? = null

    // ---- состояние игры (сохраняется при повороте) ----
    private var mode = MODE_LETTER_TO_PICTURE
    private var round = 1
    private var score = 0
    private var correctIdx = 0
    private var options = IntArray(OPTIONS_COUNT)
    private var selected = -1
    private var finished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        sound = SoundHelper(this)
        progress = Progress(this)

        tvRound = findViewById(R.id.tvRound)
        tvScore = findViewById(R.id.tvScore)
        tvBest = findViewById(R.id.tvBest)
        tvHint = findViewById(R.id.tvHint)
        tvQuestion = findViewById(R.id.tvQuestion)
        toggleGroup = findViewById(R.id.toggleMode)
        optionViews = listOf(
            findViewById(R.id.opt0), findViewById(R.id.opt1),
            findViewById(R.id.opt2), findViewById(R.id.opt3)
        )

        if (savedInstanceState != null) {
            mode = savedInstanceState.getInt(KEY_MODE)
            round = savedInstanceState.getInt(KEY_ROUND)
            score = savedInstanceState.getInt(KEY_SCORE)
            correctIdx = savedInstanceState.getInt(KEY_CORRECT)
            options = savedInstanceState.getIntArray(KEY_OPTIONS) ?: IntArray(OPTIONS_COUNT)
            selected = savedInstanceState.getInt(KEY_SELECTED)
            finished = savedInstanceState.getBoolean(KEY_FINISHED)
        } else {
            newQuestion()
        }

        toggleGroup.check(
            if (mode == MODE_LETTER_TO_PICTURE) R.id.btnModeLetter else R.id.btnModePicture
        )
        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val newMode =
                    if (checkedId == R.id.btnModeLetter) MODE_LETTER_TO_PICTURE else MODE_PICTURE_TO_LETTER
                if (newMode != mode) {
                    mode = newMode
                    restartGame()
                }
            }
        }

        optionViews.forEachIndexed { i, view -> view.setOnClickListener { onAnswer(i) } }
        findViewById<MaterialButton>(R.id.btnBackGame).setOnClickListener { finish() }

        render()
    }

    // ---------------- логика игры ----------------

    private fun newQuestion() {
        val chosen = LinkedHashSet<Int>()
        correctIdx = pool.indices.random()
        chosen.add(correctIdx)
        while (chosen.size < OPTIONS_COUNT) chosen.add(pool.indices.random())
        options = chosen.shuffled().toIntArray()
        selected = -1
    }

    private fun restartGame() {
        handler.removeCallbacks(nextRunnable)
        resultDialog?.dismiss()
        round = 1
        score = 0
        finished = false
        newQuestion()
        render()
    }

    private fun onAnswer(position: Int) {
        if (selected != -1 || finished) return
        selected = position
        val isCorrect = options[position] == correctIdx
        if (isCorrect) {
            score++
            sound.speak(pool[correctIdx])
            animateQuestion()
        }
        tvScore.text = getString(R.string.score_format, score)
        showFeedback()
        handler.postDelayed(nextRunnable, NEXT_DELAY_MS)
    }

    private fun goNext() {
        if (round >= TOTAL_ROUNDS) {
            finishGame()
        } else {
            round++
            newQuestion()
            render()
        }
    }

    private fun finishGame() {
        finished = true
        progress.saveScore(score)
        render()
    }

    // ---------------- отрисовка ----------------

    private fun render() {
        val question = pool[correctIdx]

        tvRound.text = getString(R.string.round_format, round, TOTAL_ROUNDS)
        tvScore.text = getString(R.string.score_format, score)
        tvBest.text = getString(R.string.best_format, progress.bestScore(), TOTAL_ROUNDS)

        if (mode == MODE_LETTER_TO_PICTURE) {
            tvHint.setText(R.string.hint_pick_picture)
            tvQuestion.text = question.title
        } else {
            tvHint.setText(R.string.hint_pick_letter)
            tvQuestion.text = question.emoji
        }

        optionViews.forEachIndexed { i, view ->
            val letter = pool[options[i]]
            view.text = if (mode == MODE_LETTER_TO_PICTURE) letter.emoji else letter.title
            view.setBackgroundResource(R.drawable.bg_option)
        }

        if (finished) {
            showResult()
        } else if (selected != -1) {
            // поворот экрана во время показа результата ответа
            showFeedback()
            handler.removeCallbacks(nextRunnable)
            handler.postDelayed(nextRunnable, NEXT_DELAY_MS)
        }
    }

    private fun showFeedback() {
        optionViews.forEachIndexed { i, view ->
            val bg = when {
                options[i] == correctIdx -> R.drawable.bg_option_correct
                i == selected -> R.drawable.bg_option_wrong
                else -> R.drawable.bg_option
            }
            view.setBackgroundResource(bg)
        }
    }

    private fun animateQuestion() {
        val anim = ScaleAnimation(
            1f, 1.3f, 1f, 1.3f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 400
            repeatCount = 1
            repeatMode = Animation.REVERSE
            interpolator = OvershootInterpolator()
        }
        tvQuestion.startAnimation(anim)
    }

    private fun showResult() {
        resultDialog?.dismiss()
        resultDialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.game_over)
            .setMessage(getString(R.string.result_format, score, TOTAL_ROUNDS, progress.bestScore()))
            .setCancelable(false)
            .setPositiveButton(R.string.play_again) { _, _ -> restartGame() }
            .setNegativeButton(R.string.to_list) { _, _ -> finish() }
            .show()
    }

    // ---------------- жизненный цикл ----------------

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_MODE, mode)
        outState.putInt(KEY_ROUND, round)
        outState.putInt(KEY_SCORE, score)
        outState.putInt(KEY_CORRECT, correctIdx)
        outState.putIntArray(KEY_OPTIONS, options)
        outState.putInt(KEY_SELECTED, selected)
        outState.putBoolean(KEY_FINISHED, finished)
    }

    override fun onDestroy() {
        handler.removeCallbacks(nextRunnable)
        resultDialog?.dismiss()
        sound.release()
        super.onDestroy()
    }

    companion object {
        private const val MODE_LETTER_TO_PICTURE = 0
        private const val MODE_PICTURE_TO_LETTER = 1
        private const val TOTAL_ROUNDS = 10
        private const val OPTIONS_COUNT = 4
        private const val NEXT_DELAY_MS = 1500L

        private const val KEY_MODE = "key_mode"
        private const val KEY_ROUND = "key_round"
        private const val KEY_SCORE = "key_score"
        private const val KEY_CORRECT = "key_correct"
        private const val KEY_OPTIONS = "key_options"
        private const val KEY_SELECTED = "key_selected"
        private const val KEY_FINISHED = "key_finished"
    }
}
