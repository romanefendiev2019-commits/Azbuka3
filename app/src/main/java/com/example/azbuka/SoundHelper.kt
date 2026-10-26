package com.example.azbuka

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Озвучивание буквы и слова.
 *
 * 1) Если в res/raw лежат файлы letter_<key>.mp3 и word_<key>.mp3 (например letter_a.mp3, word_a.mp3),
 *    они воспроизводятся через MediaPlayer одна за другой.
 * 2) Если файлов нет – используется синтезатор речи TextToSpeech (русский язык).
 */
class SoundHelper(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var ttsReady = false
    private var player: MediaPlayer? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ru", "RU"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    fun speak(letter: Letter) {
        stop()
        val letterRes = rawId("letter_${letter.key}")
        val wordRes = rawId("word_${letter.key}")

        if (letterRes != 0) {
            play(letterRes) {
                if (wordRes != 0) play(wordRes)
            }
        } else if (ttsReady) {
            tts?.speak("${letter.spoken}. ${letter.word}", TextToSpeech.QUEUE_FLUSH, null, "letter_${letter.key}")
        }
    }

    private fun rawId(name: String): Int =
        context.resources.getIdentifier(name, "raw", context.packageName)

    private fun play(resId: Int, onDone: (() -> Unit)? = null) {
        val mp = MediaPlayer.create(context, resId) ?: return
        player = mp
        mp.setOnCompletionListener {
            it.release()
            if (player === it) player = null
            onDone?.invoke()
        }
        mp.start()
    }

    fun stop() {
        tts?.stop()
        player?.release()
        player = null
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
