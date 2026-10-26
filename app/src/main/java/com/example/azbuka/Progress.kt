package com.example.azbuka

import android.content.Context

/** Хранение прогресса изучения и рекорда игры в SharedPreferences. */
class Progress(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Ключи букв, которые ребёнок уже открыл (нажал на букву). */
    fun learned(): Set<String> =
        HashSet(prefs.getStringSet(KEY_LEARNED, emptySet()) ?: emptySet())

    fun markLearned(letterKey: String) {
        val set = learned().toMutableSet()
        if (set.add(letterKey)) {
            prefs.edit().putStringSet(KEY_LEARNED, set).apply()
        }
    }

    fun bestScore(): Int = prefs.getInt(KEY_BEST, 0)

    /** Сохраняет результат, если он лучше рекорда. */
    fun saveScore(score: Int) {
        if (score > bestScore()) {
            prefs.edit().putInt(KEY_BEST, score).apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "azbuka_prefs"
        private const val KEY_LEARNED = "learned_letters"
        private const val KEY_BEST = "best_score"
    }
}
