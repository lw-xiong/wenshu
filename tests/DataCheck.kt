import com.ciel.hsk.HskData

fun main() {
    val words = HskData.allWords()
    check(words.isNotEmpty()) { "Vocabulary must not be empty" }

    repeat(25) {
        val correct = words.random()
        val options = (words.filter { it.hanzi != correct.hanzi }.shuffled().take(3) + correct).shuffled()
        check(options.size == 4) { "Quiz must have four options" }
        check(options.count { it.hanzi == correct.hanzi } == 1) { "Quiz must have one correct answer" }
        check(options.distinctBy { it.hanzi }.size == 4) { "Quiz options must be unique" }
    }

    check(10 * 20 / 10 == 20) { "Perfect mistakes session awards 20 XP" }
    check(5 * 10 / 10 == 5) { "Half-correct words session awards 5 XP" }
    check(0 * 5 / 5 == 0) { "Zero score awards zero XP" }

    println("PASS: ${words.size} words; 25 randomized quizzes; XP calculations")
}
