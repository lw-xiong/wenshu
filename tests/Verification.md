# Verification matrix

## Screenshot audit fixes

| Criterion | Evidence | Status |
|---|---|---|
| Dark-mode status bar is dark | `applySystemBars` sets status/navigation/window colors and icon contrast | Source check + build |
| Settings row is aligned | `CENTER_VERTICAL`, 56dp row, 48dp Switch, consistent card padding | Source check + build |
| ON/OFF text is a real toggle | Framework `Switch` with persistent checked state | Source check + build |
| Lesson buttons align | Previous/Next/Shuffle are all Buttons with fixed 52dp height | Source check + build |
| Five tabs fit on screen | Container height `0` with weight `1`; tab bar height `64dp` | Source check + build |

## Practice and rewards

| Criterion | Evidence | Status |
|---|---|---|
| Practice tab opens hub | `TAB_PRACTICE -> buildPracticeHub` | Source check + build |
| Four difficulty/session types | Easy 10, Medium 15, Hard 20, HSK Reading 25 | JVM checks, 10 randomized sessions per difficulty |
| Seven question formats | Meaning, hanzi, pinyin, listening, true/false, fill-blank, sentence order | JVM checks for presence and valid answers |
| Four unique MC choices | Every applicable generated question | JVM checks, four unique options and one answer |
| Listening prompts | Speech present; hanzi not leaked in prompt | JVM checks |
| Sentence construction | Token multiset and answer match textbook sentence | JVM checks |
| Word and sentence matching | Four pairs per board; separate interactive two-column UI | JVM data checks + source route check |
| XP and coins | Zero, partial, and perfect scoring | JVM reward checks |
| Achievements | All locked at zero; all unlocked at milestone values | JVM checks |
| Equal bottom-nav columns | Explicit `weightSum`, weighted full-height items, full-width centered icon/labels | Source check + build |
| Theme switch preserves view | No `recreate()`; active tab and scroll offset restored after in-place rebuild | Source check + build |
| Version metadata | `versionCode=5`, `versionName=1.2.1` | APK badging |
| Runtime visibility and clicks | Requires physical device or emulator | Not exercised: no Android target connected |

Research sources and explicit feature limits: [../docs/practice-research.md](../docs/practice-research.md).
