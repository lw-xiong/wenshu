package com.ciel.hsk

import kotlin.random.Random

enum class PracticeDifficulty(val title: String, val count: Int, val baseXp: Int) {
    EASY("Easy", 10, 10),
    MEDIUM("Medium", 15, 20),
    HARD("Hard", 20, 30),
    HSK("HSK Reading", 25, 30)
}

enum class QuestionType {
    MEANING, HANZI, PINYIN, LISTENING, TRUE_FALSE, FILL_BLANK, SENTENCE_ORDER,
    SENTENCE_TRANSLATION, SENTENCE_LISTENING
}

data class PracticeQuestion(
    val type: QuestionType,
    val prompt: String,
    val instruction: String,
    val answer: String,
    val options: List<String> = emptyList(),
    val tokens: List<String> = emptyList(),
    val requiredTokens: Int = tokens.size,
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
    // HSK Standard Course 2 lesson titles and dialogues.
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
        PracticeSentence("以后我少喝一点儿每天喝一杯", "Yǐhòu wǒ shǎo hē yìdiǎnr, měitiān hē yì bēi", "I'll drink less and have one cup a day", listOf("以后", "我", "少喝", "一点儿", "每天", "喝", "一杯"), "一杯"),
        PracticeSentence("你怎么不吃了", "Nǐ zěnme bù chī le", "Why don't you eat more?", listOf("你", "怎么", "不", "吃", "了"), "吃"),
        PracticeSentence("你家离公司远吗", "Nǐ jiā lí gōngsī yuǎn ma", "Do you live far from your company?", listOf("你", "家", "离", "公司", "远", "吗"), "远"),
        PracticeSentence("让我想想再告诉你", "Ràng wǒ xiǎngxiang zài gàosu nǐ", "Let me think and I'll tell you later", listOf("让", "我", "想想", "再", "告诉", "你"), "告诉"),
        PracticeSentence("题太多我没做完", "Tí tài duō, wǒ méi zuòwán", "There were too many questions; I didn't finish", listOf("题", "太多", "我", "没", "做", "完"), "完"),
        PracticeSentence("别找了手机在桌子上呢", "Bié zhǎo le, shǒujī zài zhuōzi shàng ne", "Stop looking for your phone; it's on the desk", listOf("别", "找", "了", "手机", "在", "桌子", "上", "呢"), "手机"),
        PracticeSentence("他比我大三岁", "Tā bǐ wǒ dà sān suì", "He is three years older than me", listOf("他", "比", "我", "大", "三岁"), "三岁"),
        PracticeSentence("你穿得太少了", "Nǐ chuān de tài shǎo le", "You wear too little", listOf("你", "穿", "得", "太", "少", "了"), "少"),
        PracticeSentence("门开着呢", "Mén kāizhe ne", "The door is open", listOf("门", "开", "着", "呢"), "门"),
        PracticeSentence("新年就要到了", "Xīnnián jiù yào dào le", "The New Year is coming", listOf("新年", "就", "要", "到", "了"), "新年"),
        PracticeSentence("你看过那个电影吗", "Nǐ kàn guo nàge diànyǐng ma", "Have you seen that movie?", listOf("你", "看", "过", "那个", "电影", "吗"), "电影")
    )

    fun generate(
        difficulty: PracticeDifficulty,
        inputWords: List<HskData.WordEntry>,
        random: Random = Random.Default,
        exclude: Set<String> = emptySet()
    ): List<PracticeQuestion> {
        val words = inputWords.distinctBy { it.hanzi }.shuffled(random)
        require(words.size >= 4) { "At least four words are required" }
        val types = when (difficulty) {
            PracticeDifficulty.EASY -> listOf(QuestionType.MEANING, QuestionType.HANZI)
            PracticeDifficulty.MEDIUM -> listOf(QuestionType.MEANING, QuestionType.HANZI, QuestionType.PINYIN, QuestionType.LISTENING, QuestionType.TRUE_FALSE, QuestionType.SENTENCE_TRANSLATION)
            PracticeDifficulty.HARD -> listOf(QuestionType.FILL_BLANK, QuestionType.SENTENCE_ORDER, QuestionType.LISTENING, QuestionType.PINYIN, QuestionType.TRUE_FALSE, QuestionType.HANZI, QuestionType.SENTENCE_TRANSLATION, QuestionType.SENTENCE_LISTENING)
            PracticeDifficulty.HSK -> listOf(QuestionType.TRUE_FALSE, QuestionType.FILL_BLANK, QuestionType.SENTENCE_ORDER, QuestionType.MEANING, QuestionType.SENTENCE_TRANSLATION)
        }
        var bag = mutableListOf<QuestionType>()
        var previous: QuestionType? = null
        fun nextType(): QuestionType {
            if (bag.isEmpty()) {
                bag = types.shuffled(random).toMutableList()
                if (types.size > 1 && bag.first() == previous) {
                    val other = bag.indexOfFirst { it != previous }
                    val temporary = bag[0]
                    bag[0] = bag[other]
                    bag[other] = temporary
                }
            }
            return bag.removeAt(0).also { previous = it }
        }

        val used = exclude.toMutableSet()
        return List(difficulty.count) { index ->
            var question: PracticeQuestion
            do {
                question = createQuestion(nextType(), words[index % words.size], words, difficulty, random)
            } while (!used.add(signature(question)))
            question
        }
    }

    private fun createQuestion(
        type: QuestionType,
        word: HskData.WordEntry,
        words: List<HskData.WordEntry>,
        difficulty: PracticeDifficulty,
        random: Random
    ): PracticeQuestion {
        val sentence = sentences.random(random)
        return when (type) {
            QuestionType.MEANING -> {
                val showHanzi = difficulty == PracticeDifficulty.EASY || random.nextBoolean()
                PracticeQuestion(type, if (showHanzi) word.hanzi else word.pinyin, "${if (showHanzi) word.pinyin else "Listen to the reading in your head"}\nChoose the correct meaning", word.meaning, wordOptions(words, word, random) { it.meaning }, wordKey = word.hanzi)
            }
            QuestionType.HANZI -> {
                val usePinyin = random.nextBoolean()
                PracticeQuestion(type, if (usePinyin) word.pinyin else word.meaning, "${if (usePinyin) word.meaning else "Read the meaning"}\nChoose the correct hanzi", word.hanzi, wordOptions(words, word, random) { it.hanzi }, wordKey = word.hanzi)
            }
            QuestionType.PINYIN -> PracticeQuestion(type, word.hanzi, "${word.meaning}\nChoose the correct pinyin", word.pinyin, wordOptions(words, word, random) { it.pinyin }, wordKey = word.hanzi)
            QuestionType.LISTENING -> {
                when (random.nextInt(3)) {
                    0 -> PracticeQuestion(type, "Listen and choose the meaning", "Tap Play, then choose the correct meaning", word.meaning, wordOptions(words, word, random) { it.meaning }, speech = word.hanzi, wordKey = word.hanzi)
                    1 -> PracticeQuestion(type, "Listen and choose the hanzi", "Tap Play, then choose what you hear", word.hanzi, wordOptions(words, word, random) { it.hanzi }, speech = word.hanzi, wordKey = word.hanzi)
                    else -> PracticeQuestion(type, "Listen and choose the pinyin", "Tap Play, then choose the correct reading", word.pinyin, wordOptions(words, word, random) { it.pinyin }, speech = word.hanzi, wordKey = word.hanzi)
                }
            }
            QuestionType.TRUE_FALSE -> {
                if (difficulty == PracticeDifficulty.HSK) {
                    val correct = random.nextBoolean()
                    val proposed = if (correct) sentence.english else sentences.filter { it.english != sentence.english }.random(random).english
                    PracticeQuestion(type, sentence.hanzi, "${sentence.pinyin}\nDoes this mean: $proposed", if (correct) "True" else "False", listOf("True", "False"), solution = "${if (correct) "True" else "False"} — ${sentence.english}")
                } else {
                    val correct = random.nextBoolean()
                    val proposed = if (correct) word.meaning else words.filter { it.meaning != word.meaning }.random(random).meaning
                    PracticeQuestion(type, "${word.hanzi} means “$proposed”", "${word.pinyin}\nTrue or false?", if (correct) "True" else "False", listOf("True", "False"), wordKey = word.hanzi, solution = word.meaning)
                }
            }
            QuestionType.FILL_BLANK -> {
                val candidates = sentence.tokens.filter { it.length > 1 || it == sentence.blank }
                val target = candidates.random(random)
                val distractors = sentences.flatMap { it.tokens }.distinct().filter { it != target }.shuffled(random).take(3)
                val options = (distractors + target).shuffled(random)
                val hint = if (difficulty == PracticeDifficulty.HSK) sentence.pinyin else sentence.english
                PracticeQuestion(type, sentence.hanzi.replaceFirst(target, "_____"), "$hint\nChoose the missing word", target, options, wordKey = target, solution = sentence.hanzi)
            }
            QuestionType.SENTENCE_ORDER -> {
                val extras = if (difficulty == PracticeDifficulty.HARD) {
                    sentences.flatMap { it.tokens }.filter { it !in sentence.tokens }.distinct().shuffled(random).take(1)
                } else emptyList()
                val prompt = if (random.nextBoolean()) sentence.english else sentence.pinyin
                val extraHint = if (extras.isEmpty()) "" else " One word is extra."
                PracticeQuestion(type, prompt, "Tap the words in order to build the Chinese sentence.$extraHint", sentence.tokens.joinToString(""), tokens = (sentence.tokens + extras).shuffled(random), requiredTokens = sentence.tokens.size, solution = sentence.hanzi)
            }
            QuestionType.SENTENCE_TRANSLATION -> {
                val toEnglish = random.nextBoolean()
                val answer = if (toEnglish) sentence.english else sentence.hanzi
                PracticeQuestion(type, if (toEnglish) sentence.hanzi else sentence.english, "Choose the correct translation", answer, sentenceOptions(sentence, random) { if (toEnglish) it.english else it.hanzi }, solution = "${sentence.hanzi} · ${sentence.english}")
            }
            QuestionType.SENTENCE_LISTENING -> {
                val toEnglish = random.nextBoolean()
                val answer = if (toEnglish) sentence.english else sentence.hanzi
                PracticeQuestion(type, "Listen to the sentence", "Tap Play, then choose what you hear", answer, sentenceOptions(sentence, random) { if (toEnglish) it.english else it.hanzi }, speech = sentence.hanzi, solution = "${sentence.hanzi} · ${sentence.english}")
            }
        }
    }

    private fun wordOptions(words: List<HskData.WordEntry>, correct: HskData.WordEntry, random: Random, text: (HskData.WordEntry) -> String): List<String> {
        val correctText = text(correct)
        val distractors = words.map(text).filter { it != correctText }.distinct().shuffled(random).take(3)
        return (distractors + correctText).shuffled(random)
    }

    private fun sentenceOptions(correct: PracticeSentence, random: Random, text: (PracticeSentence) -> String): List<String> {
        val correctText = text(correct)
        val distractors = sentences.map(text).filter { it != correctText }.distinct().shuffled(random).take(3)
        return (distractors + correctText).shuffled(random)
    }

    fun signature(question: PracticeQuestion): String =
        listOf(question.type, question.prompt, question.instruction, question.answer, question.options, question.tokens, question.speech ?: "").joinToString("|")

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
            "writing" -> 20
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
