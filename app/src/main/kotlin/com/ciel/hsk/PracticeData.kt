package com.ciel.hsk

enum class PracticeDifficulty(val title: String, val count: Int, val baseXp: Int) {
    EASY("Easy", 10, 10),
    MEDIUM("Medium", 15, 20),
    HARD("Hard", 20, 30),
    HSK("HSK Reading", 25, 30)
}

enum class QuestionType {
    MEANING, HANZI, PINYIN, LISTENING, TRUE_FALSE, FILL_BLANK, SENTENCE_ORDER
}

data class PracticeQuestion(
    val type: QuestionType,
    val prompt: String,
    val instruction: String,
    val answer: String,
    val options: List<String> = emptyList(),
    val tokens: List<String> = emptyList(),
    val speech: String? = null,
    val wordKey: String? = null,
    val solution: String = answer
)

data class PracticeSentence(
    val hanzi: String,
    val pinyin: String,
    val english: String,
    val tokens: List<String>,
    val blank: String
)

data class PlayerStats(
    val lessonsDone: Int,
    val xp: Int,
    val coins: Int,
    val correctAnswers: Int,
    val perfectSessions: Int
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val progress: Int,
    val target: Int
) {
    val unlocked: Boolean get() = progress >= target
}

object PracticeData {
    // Sentences transcribed from HSK Standard Course 2, lessons 1–5.
    val sentences = listOf(
        PracticeSentence("我要去北京旅游", "Wǒ yào qù Běijīng lǚyóu", "I want to travel to Beijing", listOf("我", "要", "去", "北京", "旅游"), "旅游"),
        PracticeSentence("九月去北京旅游最好", "Jiǔyuè qù Běijīng lǚyóu zuì hǎo", "September is the best time to visit Beijing", listOf("九月", "去", "北京", "旅游", "最好"), "最好"),
        PracticeSentence("我最喜欢踢足球", "Wǒ zuì xǐhuan tī zúqiú", "I like playing football the most", listOf("我", "最", "喜欢", "踢足球"), "踢足球"),
        PracticeSentence("下午我们一起去踢足球吧", "Xiàwǔ wǒmen yìqǐ qù tī zúqiú ba", "Let's play football together this afternoon", listOf("下午", "我们", "一起", "去", "踢足球", "吧"), "一起"),
        PracticeSentence("我每天六点起床", "Wǒ měitiān liù diǎn qǐchuáng", "I get up at six every day", listOf("我", "每天", "六点", "起床"), "起床"),
        PracticeSentence("我每天早上都要出去跑步", "Wǒ měitiān zǎoshang dōu yào chūqù pǎobù", "I go out for a run every morning", listOf("我", "每天", "早上", "都", "要", "出去", "跑步"), "跑步"),
        PracticeSentence("吃药了吗", "Chī yào le ma", "Did you take your medicine?", listOf("吃", "药", "了", "吗"), "药"),
        PracticeSentence("我每天回来都很累", "Wǒ měitiān huílái dōu hěn lèi", "Every day I come home exhausted", listOf("我", "每天", "回来", "都", "很", "累"), "累"),
        PracticeSentence("这块手表是你的吗", "Zhè kuài shǒubiǎo shì nǐ de ma", "Is this watch yours?", listOf("这块", "手表", "是", "你", "的", "吗"), "手表"),
        PracticeSentence("左边那个红色的是我的", "Zuǒbian nàge hóngsè de shì wǒ de", "The red one on the left is mine", listOf("左边", "那个", "红色", "的", "是", "我", "的"), "红色"),
        PracticeSentence("这本书是我写的", "Zhè běn shū shì wǒ xiě de", "I wrote this book", listOf("这本", "书", "是", "我", "写", "的"), "书"),
        PracticeSentence("晚上我们去饭馆吃饭", "Wǎnshang wǒmen qù fànguǎn chīfàn", "We'll eat at a restaurant tonight", listOf("晚上", "我们", "去", "饭馆", "吃饭"), "饭馆"),
        PracticeSentence("就做你爱吃的鱼吧", "Jiù zuò nǐ ài chī de yú ba", "Let's cook the fish you like", listOf("就", "做", "你", "爱吃", "的", "鱼", "吧"), "鱼"),
        PracticeSentence("这件不错就买这件吧", "Zhè jiàn búcuò, jiù mǎi zhè jiàn ba", "This one is good; take this one", listOf("这件", "不错", "就", "买", "这件", "吧"), "不错"),
        PracticeSentence("以后我少喝一点儿每天喝一杯", "Yǐhòu wǒ shǎo hē yìdiǎnr, měitiān hē yì bēi", "I'll drink less and have one cup a day", listOf("以后", "我", "少喝", "一点儿", "每天", "喝", "一杯"), "一杯")
    )

    fun generate(difficulty: PracticeDifficulty, inputWords: List<HskData.WordEntry>): List<PracticeQuestion> {
        val words = inputWords.distinctBy { it.hanzi }
        require(words.size >= 4) { "At least four words are required" }
        val types = when (difficulty) {
            PracticeDifficulty.EASY -> listOf(QuestionType.MEANING, QuestionType.HANZI)
            PracticeDifficulty.MEDIUM -> listOf(QuestionType.MEANING, QuestionType.HANZI, QuestionType.PINYIN, QuestionType.LISTENING, QuestionType.TRUE_FALSE)
            PracticeDifficulty.HARD -> listOf(QuestionType.FILL_BLANK, QuestionType.SENTENCE_ORDER, QuestionType.LISTENING, QuestionType.PINYIN, QuestionType.TRUE_FALSE, QuestionType.HANZI)
            PracticeDifficulty.HSK -> listOf(QuestionType.TRUE_FALSE, QuestionType.FILL_BLANK, QuestionType.SENTENCE_ORDER, QuestionType.MEANING)
        }
        return (0 until difficulty.count).map { index ->
            createQuestion(types[index % types.size], index, words, difficulty == PracticeDifficulty.HSK)
        }
    }

    private fun createQuestion(type: QuestionType, index: Int, words: List<HskData.WordEntry>, hsk: Boolean): PracticeQuestion {
        val word = words[index % words.size]
        val sentence = sentences[(index / 3) % sentences.size]
        return when (type) {
            QuestionType.MEANING -> PracticeQuestion(type, word.hanzi, "${word.pinyin}\nChoose the correct meaning", word.meaning, wordOptions(words, word) { it.meaning }, wordKey = word.hanzi)
            QuestionType.HANZI -> PracticeQuestion(type, word.meaning, "Choose the correct hanzi", word.hanzi, wordOptions(words, word) { it.hanzi }, wordKey = word.hanzi)
            QuestionType.PINYIN -> PracticeQuestion(type, word.hanzi, "${word.meaning}\nChoose the correct pinyin", word.pinyin, wordOptions(words, word) { it.pinyin }, wordKey = word.hanzi)
            QuestionType.LISTENING -> PracticeQuestion(type, "Listen and choose the meaning", "Tap Play, then choose the correct meaning", word.meaning, wordOptions(words, word) { it.meaning }, speech = word.hanzi, wordKey = word.hanzi)
            QuestionType.TRUE_FALSE -> {
                if (hsk) {
                    val proposed = if (index % 2 == 0) sentence.english else sentences[(index + 1) % sentences.size].english
                    PracticeQuestion(type, sentence.hanzi, "${sentence.pinyin}\nDoes this mean: $proposed", if (proposed == sentence.english) "True" else "False", listOf("True", "False"), solution = "${sentence.english} · $proposed")
                } else {
                    val proposed = if (index % 2 == 0) word.meaning else words[(index + 1) % words.size].meaning
                    PracticeQuestion(type, "${word.hanzi} means “$proposed”", "${word.pinyin}\nTrue or false?", if (proposed == word.meaning) "True" else "False", listOf("True", "False"), wordKey = word.hanzi, solution = word.meaning)
                }
            }
            QuestionType.FILL_BLANK -> {
                val distractors = sentences.flatMap { it.tokens }.distinct().filter { it != sentence.blank }.shuffled().take(3)
                val options = (distractors + sentence.blank).shuffled()
                PracticeQuestion(type, sentence.hanzi.replaceFirst(sentence.blank, "_____"), "${sentence.english}\nChoose the missing word", sentence.blank, options, wordKey = sentence.blank, solution = sentence.hanzi)
            }
            QuestionType.SENTENCE_ORDER -> PracticeQuestion(type, sentence.english, "Tap the words in order to build the Chinese sentence", sentence.tokens.joinToString(""), tokens = sentence.tokens.shuffled(), solution = sentence.hanzi)
        }
    }

    private fun wordOptions(words: List<HskData.WordEntry>, correct: HskData.WordEntry, text: (HskData.WordEntry) -> String): List<String> {
        val correctText = text(correct)
        val distractors = words.map(text).filter { it != correctText }.distinct().shuffled().take(3)
        return (distractors + correctText).shuffled()
    }

    fun wordMatchingBoard(input: List<HskData.WordEntry>, size: Int = 4): List<Pair<String, String>> =
        input.distinctBy { it.hanzi }.shuffled().take(size).map { it.hanzi to it.meaning }

    fun sentenceMatchingBoard(size: Int = 4): List<Pair<String, String>> =
        sentences.shuffled().take(size).map { it.hanzi to it.english }

    fun rewards(mode: String, score: Int, total: Int): Pair<Int, Int> {
        require(total > 0 && score in 0..total)
        val baseXp = when (mode) {
            "easy" -> 10
            "medium", "match_sentences", "mistakes" -> 20
            "hard", "hsk" -> 30
            "match_words" -> 15
            else -> 10
        }
        val xp = score * baseXp / total
        val coins = score + if (score == total) 5 else 0
        return xp to coins
    }

    fun achievements(stats: PlayerStats): List<Achievement> = listOf(
        Achievement("first_lesson", "First Steps", "Complete 1 lesson", stats.lessonsDone, 1),
        Achievement("five_lessons", "Bookworm", "Complete 5 lessons", stats.lessonsDone, 5),
        Achievement("perfect", "Perfectionist", "Finish a perfect practice session", stats.perfectSessions, 1),
        Achievement("coins", "Coin Collector", "Earn 100 coins", stats.coins, 100),
        Achievement("answers", "Quiz Master", "Answer 100 questions correctly", stats.correctAnswers, 100)
    )
}
