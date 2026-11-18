import com.ciel.hsk.HskData
import com.ciel.hsk.PlayerStats
import com.ciel.hsk.PracticeData
import com.ciel.hsk.PracticeDifficulty
import com.ciel.hsk.QuestionType

fun main() {
    val words = HskData.allWords().filter { it.hskLevel <= 2 }
    check(words.size >= 4) { "Vocabulary must contain at least four words" }

    for (difficulty in PracticeDifficulty.values()) {
        repeat(10) {
            val questions = PracticeData.generate(difficulty, words.shuffled())
            check(questions.size == difficulty.count) { "Wrong ${difficulty.title} question count" }
            val types = questions.map { it.type }.toSet()

            when (difficulty) {
                PracticeDifficulty.EASY -> check(types == setOf(QuestionType.MEANING, QuestionType.HANZI))
                PracticeDifficulty.MEDIUM -> check(types.containsAll(setOf(QuestionType.PINYIN, QuestionType.LISTENING, QuestionType.TRUE_FALSE)))
                PracticeDifficulty.HARD -> check(types.containsAll(setOf(QuestionType.FILL_BLANK, QuestionType.SENTENCE_ORDER, QuestionType.LISTENING)))
                PracticeDifficulty.HSK -> check(types.containsAll(setOf(QuestionType.TRUE_FALSE, QuestionType.FILL_BLANK, QuestionType.SENTENCE_ORDER)))
            }

            for (question in questions) {
                when (question.type) {
                    QuestionType.SENTENCE_ORDER -> {
                        val sentence = PracticeData.sentences.first { it.hanzi == question.solution }
                        check(question.tokens.sorted() == sentence.tokens.sorted()) { "Sentence tokens changed" }
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
                if (question.type == QuestionType.LISTENING) {
                    val speech = requireNotNull(question.speech) { "Listening question needs speech" }
                    check(speech.isNotBlank()) { "Listening speech must not be blank" }
                    check(!question.prompt.contains(speech)) { "Listening prompt leaks hanzi" }
                }
                if (question.type == QuestionType.FILL_BLANK) {
                    check(question.prompt.contains("_____")) { "Fill-blank prompt needs a blank" }
                }
            }
        }
    }

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

    check(PracticeData.achievements(PlayerStats(0, 0, 0, 0, 0)).none { it.unlocked })
    check(PracticeData.achievements(PlayerStats(5, 500, 100, 100, 1)).all { it.unlocked })

    println("Word counts: ${HskData.allWords().groupingBy { it.hskLevel }.eachCount()}")
    println("PASS: ${words.size} words; all difficulties and question types; matching; rewards; achievements")
}
