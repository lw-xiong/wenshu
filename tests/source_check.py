from pathlib import Path

source = Path(__file__).parents[1] / "app/src/main/kotlin/com/ciel/hsk/MainActivity.kt"
text = source.read_text()

assert 'arrayOf("Home", "Learn", "Vocab", "Practice", "Profile")' in text
assert 'private val TAB_PRACTICE = 3' in text
assert 'TAB_PRACTICE -> container.addView(buildPracticeHub(this))' in text
assert 'private fun buildPracticeHub(context: Context): View' in text
assert 'item.contentDescription = "tab_${tabLabels[i].lowercase()}"' in text
assert 'ViewGroup.LayoutParams.MATCH_PARENT,\n            0\n        ).apply { weight = 1f }' in text
assert 'dp(this, 64)' in text
assert 'openLesson(0, 0)' not in text

print("PASS: five-tab navigation; Practice route; visible weighted container; 64dp tab bar")
