import com.ciel.hsk.HskData
import com.ciel.hsk.PlayerStats
import com.ciel.hsk.PracticeData
import com.ciel.hsk.PracticeDifficulty
import com.ciel.hsk.QuestionType
import com.ciel.hsk.Srs
import com.ciel.hsk.WritingEvaluator
import kotlin.random.Random

fun main() {
    val words = HskData.allWords().filter { it.hskLevel <= 2 }
    check(words.size >= 4) { "Vocabulary must contain at least four words" }

    for (difficulty in PracticeDifficulty.values()) {
        val sessionVariants = mutableSetOf<String>()
        repeat(20) {
            val questions = PracticeData.generate(difficulty, words)
            sessionVariants += questions.joinToString("||") {
                listOf(it.type, it.prompt, it.instruction, it.answer, it.options, it.tokens).joinToString("|")
            }
            check(questions.size == difficulty.count) { "Wrong ${difficulty.title} question count" }
            val duplicates = questions.groupBy { listOf(it.type, it.prompt, it.instruction, it.answer, it.options, it.tokens) }
                .filterValues { it.size > 1 }.keys
            check(duplicates.isEmpty()) { "Duplicate question inside ${difficulty.title}: $duplicates" }
            val types = questions.map { it.type }.toSet()

            when (difficulty) {
                PracticeDifficulty.EASY -> check(types == setOf(QuestionType.MEANING, QuestionType.HANZI))
                PracticeDifficulty.MEDIUM -> check(types.containsAll(setOf(QuestionType.PINYIN, QuestionType.LISTENING, QuestionType.TRUE_FALSE, QuestionType.SENTENCE_TRANSLATION)))
                PracticeDifficulty.HARD -> check(types.containsAll(setOf(QuestionType.FILL_BLANK, QuestionType.SENTENCE_ORDER, QuestionType.LISTENING, QuestionType.SENTENCE_LISTENING)))
                PracticeDifficulty.HSK -> check(types.containsAll(setOf(QuestionType.TRUE_FALSE, QuestionType.FILL_BLANK, QuestionType.SENTENCE_ORDER, QuestionType.SENTENCE_TRANSLATION)))
            }

            for (question in questions) {
                when (question.type) {
                    QuestionType.SENTENCE_ORDER -> {
                        val sentence = PracticeData.sentences.first { it.hanzi == question.solution }
                        check(question.requiredTokens == sentence.tokens.size)
                        check(question.tokens.size in sentence.tokens.size..sentence.tokens.size + 1)
                        check(question.tokens.groupingBy { it }.eachCount().all { (token, count) ->
                            count >= sentence.tokens.count { it == token }
                        }) { "Sentence tokens missing" }
                        check(question.answer == sentence.tokens.joinToString("")) { "Sentence answer mismatch" }
                    }
                    QuestionType.TRUE_FALSE -> {
                        check(question.options == listOf("True", "False"))
                        check(question.answer in question.options)
                    }
                    else -> {
                        check(question.options.size == 4) { "Expected four options for ${question.type}, got ${question.options}" }
                        check(question.options.distinct().size == 4) { "Duplicate options for ${question.type}" }
                        check(question.answer in question.options) { "Missing correct option for ${question.type}" }
                    }
                }
                if (question.type == QuestionType.LISTENING || question.type == QuestionType.SENTENCE_LISTENING) {
                    val speech = requireNotNull(question.speech) { "Listening question needs speech" }
                    check(speech.isNotBlank()) { "Listening speech must not be blank" }
                    check(!question.prompt.contains(speech)) { "Listening prompt leaks text" }
                }
                if (question.type == QuestionType.FILL_BLANK) {
                    check(question.prompt.contains("_____")) { "Fill-blank prompt needs a blank" }
                }
            }
        }
        check(sessionVariants.size >= 18) { "${difficulty.title} sessions are repetitive: $sessionVariants" }
    }

    val firstSession = PracticeData.generate(PracticeDifficulty.MEDIUM, words, Random(1))
    val firstSignatures = firstSession.map { PracticeData.signature(it) }.toSet()
    val secondSession = PracticeData.generate(PracticeDifficulty.MEDIUM, words, Random(2), exclude = firstSignatures)
    check(secondSession.none { PracticeData.signature(it) in firstSignatures }) { "Recent question repeated" }

    repeat(20) {
        val board = PracticeData.wordMatchingBoard(words)
        check(board.size == 4)
        check(board.map { it.first }.distinct().size == 4)
        check(board.all { it.second.isNotBlank() })
    }
    check(PracticeData.sentenceMatchingBoard().size == 4)

    check(PracticeData.rewards("hard", 20, 20) == (30 to 25))
    check(PracticeData.rewards("easy", 0, 10) == (0 to 0))
    check(PracticeData.rewards("match_words", 10, 20) == (7 to 10))
    check(PracticeData.rewards("writing", 10, 10) == (20 to 15))
    check(PracticeData.achievements(PlayerStats(0, 0, 0, 0, 0)).none { it.unlocked })
    check(PracticeData.achievements(PlayerStats(5, 500, 100, 100, 1)).all { it.unlocked })

    val target = BooleanArray(25) { it in setOf(2, 7, 10, 11, 12, 13, 14, 17, 22) }
    check(WritingEvaluator.traceScore(target, target, 5, 5) == 100)
    check(WritingEvaluator.traceScore(target, BooleanArray(25), 5, 5) == 0)
    val fullInkScore = WritingEvaluator.traceScore(target, BooleanArray(25) { true }, 5, 5, tolerance = 0)
    check(fullInkScore < WritingEvaluator.PASS_SCORE) { "Full-canvas scribble passed: $fullInkScore" }
    val shifted = BooleanArray(25) { it in setOf(3, 8, 11, 12, 13, 14, 15, 18, 23) }
    check(WritingEvaluator.traceScore(target, shifted, 5, 5, tolerance = 1) >= 90)
    check(words.count { it.hanzi.length == 1 } >= 10)

    // SRS (Leitner) scheduling: box advances, caps at top, intervals strictly grow
    check(Srs.nextBox(0) == 1)
    check(Srs.nextBox(1) == 2)
    check(Srs.nextBox(Srs.INTERVALS_MIN.size) == Srs.INTERVALS_MIN.size) { "box must cap at top interval" }
    check(Srs.dueMillis(1_000L, 1) == 1_000L + 10 * 60_000L)
    check(Srs.dueMillis(0L, 3) == 1440 * 60_000L)
    check(Srs.isDue(Srs.dueMillis(1000L, 1), 1000L + 10 * 60_000L))
    check(!Srs.isDue(Srs.dueMillis(1000L, 1), 1000L))
    check(!Srs.isDue(Srs.dueMillis(1000L, 6), 1000L))
    check(Srs.INTERVALS_MIN.toList().zipWithNext().all { (a, b) -> a < b }) { "intervals must strictly increase" }

    println("Word counts: ${HskData.allWords().groupingBy { it.hskLevel }.eachCount()}")
    println("PASS: unique sessions; nine question formats; 25 sentences; writing scores; matching; rewards; srs")
}
