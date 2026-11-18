# Verification matrix

| Criterion | Evidence | Status |
|---|---|---|
| Five tabs fit on screen | Container height `0` with weight `1`; tab bar height `64dp` | Source check + build |
| Practice tab exists | Fourth tab labelled `Practice`, description `tab_practice` | Source check + build |
| Practice tab opens hub | `TAB_PRACTICE -> buildPracticeHub` | Source check + build |
| Practice session opens quiz | `startPracticeSession -> openPracticeQuiz`; no level-0 lesson call | Source check + JVM data check |
| Four unique quiz choices | 25 randomized sessions | JVM data check |
| Version metadata | `versionCode=3`, `versionName=1.1.1` | APK badging |
| Runtime visibility and clicks | Requires physical device or emulator | Not exercised: no Android target connected |
