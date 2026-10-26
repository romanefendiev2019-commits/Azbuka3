package com.example.azbuka

/**
 * Одна буква алфавита.
 * key      – латинский идентификатор (используется для имён звуковых файлов letter_<key>, word_<key> и для хранения прогресса)
 * spoken   – как произносится название буквы
 * inGame   – участвует ли буква в игре (у Ъ, Ы, Ь нет слов, начинающихся на них)
 */
data class Letter(
    val key: String,
    val upper: String,
    val lower: String,
    val spoken: String,
    val word: String,
    val emoji: String,
    val inGame: Boolean = true
) {
    val title: String get() = "$upper$lower"
}

object Alphabet {
    val letters: List<Letter> = listOf(
        Letter("a", "А", "а", "а", "Арбуз", "🍉"),
        Letter("b", "Б", "б", "бэ", "Банан", "🍌"),
        Letter("v", "В", "в", "вэ", "Виноград", "🍇"),
        Letter("g", "Г", "г", "гэ", "Гриб", "🍄"),
        Letter("d", "Д", "д", "дэ", "Дом", "🏠"),
        Letter("e", "Е", "е", "е", "Ель", "🎄"),
        Letter("yo", "Ё", "ё", "ё", "Ёжик", "🦔"),
        Letter("zh", "Ж", "ж", "жэ", "Жираф", "🦒"),
        Letter("z", "З", "з", "зэ", "Зебра", "🦓"),
        Letter("i", "И", "и", "и", "Индюк", "🦃"),
        Letter("j", "Й", "й", "и краткое", "Йогурт", "🥛"),
        Letter("k", "К", "к", "ка", "Кот", "🐱"),
        Letter("l", "Л", "л", "эль", "Лиса", "🦊"),
        Letter("m", "М", "м", "эм", "Мишка", "🐻"),
        Letter("n", "Н", "н", "эн", "Носорог", "🦏"),
        Letter("o", "О", "о", "о", "Обезьяна", "🐒"),
        Letter("p", "П", "п", "пэ", "Попугай", "🦜"),
        Letter("r", "Р", "р", "эр", "Ракета", "🚀"),
        Letter("s", "С", "с", "эс", "Солнце", "☀️"),
        Letter("t", "Т", "т", "тэ", "Тигр", "🐯"),
        Letter("u", "У", "у", "у", "Утка", "🦆"),
        Letter("f", "Ф", "ф", "эф", "Флаг", "🚩"),
        Letter("h", "Х", "х", "ха", "Хлеб", "🍞"),
        Letter("c", "Ц", "ц", "цэ", "Цветок", "🌸"),
        Letter("ch", "Ч", "ч", "чэ", "Часы", "⏰"),
        Letter("sh", "Ш", "ш", "ша", "Шарик", "🎈"),
        Letter("sch", "Щ", "щ", "ща", "Щенок", "🐶"),
        Letter("hard", "Ъ", "ъ", "твёрдый знак", "Подъезд", "🏢", inGame = false),
        Letter("yery", "Ы", "ы", "ы", "Рыба", "🐟", inGame = false),
        Letter("soft", "Ь", "ь", "мягкий знак", "Конь", "🐴", inGame = false),
        Letter("ee", "Э", "э", "э", "Эскимо", "🍦"),
        Letter("yu", "Ю", "ю", "ю", "Юбка", "👗"),
        Letter("ya", "Я", "я", "я", "Яблоко", "🍎")
    )

    /** Буквы, которые используются в режиме «Найди букву». */
    val gameLetters: List<Letter> = letters.filter { it.inGame }
}
