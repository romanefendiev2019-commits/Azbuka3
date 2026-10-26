# Азбука для малышей (Android, Kotlin)

Курсовая работа, тема №34. Открыть папку проекта в Android Studio (File → Open) и дождаться Gradle Sync.

## Звуки
По умолчанию озвучка идёт через TextToSpeech (русский язык, нужен установленный голос в системе).
Чтобы использовать MediaPlayer со своими записями, положите файлы в `app/src/main/res/raw/`:

- `letter_<key>.mp3` – название буквы (например `letter_a.mp3`)
- `word_<key>.mp3` – слово (например `word_a.mp3`)

Ключи букв (`key`) смотрите в `Alphabet.kt`: a, b, v, g, d, e, yo, zh, z, i, j, k, l, m, n, o, p, r, s, t, u, f, h, c, ch, sh, sch, hard, yery, soft, ee, yu, ya.
Если файла нет – используется синтезатор речи.

https://kappalol.fun/9uFlVM
