from pathlib import Path

root = Path(__file__).parents[1]
text = (root / "app/src/main/kotlin/com/ciel/hsk/MainActivity.kt").read_text()
practice = (root / "app/src/main/kotlin/com/ciel/hsk/PracticeData.kt").read_text()

assert 'arrayOf("Home", "Learn", "Vocab", "Practice", "Profile")' in text
assert 'TAB_PRACTICE -> container.addView(buildPracticeHub(this))' in text
assert 'item.contentDescription = "tab_${tabLabels[i].lowercase()}"' in text
assert 'ViewGroup.LayoutParams.MATCH_PARENT,\n            0\n        ).apply { weight = 1f }' in text
assert 'openLesson(0, 0)' not in text

assert 'window.statusBarColor = C_BG()' in text
assert 'window.navigationBarColor = C_BG()' in text
assert 'View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR' in text
assert 'Color.parseColor("#0A0A0A")' in text
assert 'val darkSwitch = android.widget.Switch(context)' in text
assert 'darkSwitch.setOnCheckedChangeListener' in text
assert 'darkRow.gravity = Gravity.CENTER_VERTICAL' in text

assert 'val btnPrev = Button(context)' in text
assert 'val btnShuffle = Button(context)' in text
assert text.count('dp(context, 52)') >= 4

for mode in ("easy", "medium", "hard", "hsk", "match_words", "match_sentences"):
    assert f'Triple("{mode}"' not in text  # mode is the third tuple item, not the title
    assert f'"{mode}")' in text

assert 'private fun openMatchingPractice(mode: String)' in text
assert 'private fun openPracticeQuiz(mode: String, questions: List<PracticeQuestion>)' in text
assert 'PracticeData.generate(difficulty, source.shuffled())' in text
assert 'PracticeData.achievements(currentStats())' in text
assert 'coins_total' in text and 'practice_correct_total' in text and 'perfect_sessions' in text

assert 'HSK("HSK Reading", 25, 30)' in practice
assert 'QuestionType.LISTENING' in practice
assert 'QuestionType.FILL_BLANK' in practice
assert 'QuestionType.SENTENCE_ORDER' in practice
assert 'fun rewards(mode: String, score: Int, total: Int)' in practice

assert 'bar.weightSum = tabLabels.size.toFloat()' in text
assert 'icon.textAlignment = View.TEXT_ALIGNMENT_CENTER' in text
assert 'label.textAlignment = View.TEXT_ALIGNMENT_CENTER' in text
assert 'val tab = if (currentTab in 0..4) currentTab else TAB_PROFILE' in text
assert 'val scrollY = currentScrollView()?.scrollY ?: 0' in text
assert 'recreate()' not in text

print("PASS: equal nav columns; in-place theme switch with scroll; all practice and reward checks")
