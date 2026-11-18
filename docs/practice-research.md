# Practice design research

## Sources

- [Duolingo Practice tab](https://blog.duolingo.com/guide-to-duolingo-practice-hub/): targeted mistake review, vocabulary practice, listening, and speaking.
- [Duolingo interaction and instruction](https://medium.com/design-bootcamp/language-learning-with-duolingo-the-interaction-and-instruction-ed5ac0029332): translation, listening, speaking, image matching, pair matching, and word-block sentence construction.
- [Official HSK 2 structure](https://www.chinesetest.cn/HSK/2): 60 questions total, 35 listening and 25 reading.
- [Official HSK 2 sample paper](https://www.chinesetest.cn/userfiles/file/HSK/level2/H20901.pdf): listening and reading exam format.
- HSK Standard Course 2 textbook: source of the vocabulary and sentence exercises in `PracticeData.kt`.

## Implemented mapping

| App exercise | Mainstream/HSK inspiration |
|---|---|
| Hanzi ↔ meaning | Translation and word recognition |
| Hanzi → pinyin | Mandarin pronunciation and character recognition |
| TTS listening → meaning | Listening comprehension; uses device TTS, not exam recordings |
| True/false statements | HSK listening/reading true-false format |
| Fill in the blank | HSK reading Part 2 |
| Tap words in sentence order | Duolingo word-block sentence construction |
| Two-column word matching | Duolingo Tap the pairs |
| Two-column sentence matching | HSK pairing and Duolingo matching |
| 25-question HSK Reading session | Official HSK 2 reading-section length |

## Difficulty and rewards

- Easy: 10 recognition questions, up to 10 XP.
- Medium: 15 questions including pinyin and listening, up to 20 XP.
- Hard: 20 questions including sentence construction, up to 30 XP.
- HSK Reading: 25 reading questions, up to 30 XP.
- Matching: five word boards (20 pairs) or three sentence boards (12 pairs).
- Coins: one per first-attempt/correct answer, plus five for a perfect session.

## Deliberately not claimed

- Picture matching requires a reviewed image library; no generic images are substituted.
- Speaking score requires speech recognition and pronunciation evaluation; TTS playback alone is not speaking assessment.
- HSK Reading is exam-inspired practice, not an official mock exam or a full 60-question simulation.
