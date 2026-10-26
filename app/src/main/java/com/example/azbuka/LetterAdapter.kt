package com.example.azbuka

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/** Адаптер для GridView со всеми буквами. Изученные буквы подсвечиваются зелёным. */
class LetterAdapter(
    context: Context,
    private val letters: List<Letter>
) : BaseAdapter() {

    var learnedKeys: Set<String> = emptySet()

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = letters.size

    override fun getItem(position: Int): Letter = letters[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = (convertView as? TextView)
            ?: inflater.inflate(R.layout.item_letter, parent, false) as TextView
        val letter = letters[position]
        view.text = letter.title
        view.setBackgroundResource(
            if (letter.key in learnedKeys) R.drawable.bg_letter_learned else R.drawable.bg_letter_card
        )
        return view
    }
}
