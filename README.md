# WenShu · 问书

[![Release](https://img.shields.io/github/v/release/lw-xiong/wenshu)](https://github.com/lw-xiong/wenshu/releases)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.21-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-21%2B-3DDC84?logo=android)](https://developer.android.com)
[![License](https://img.shields.io/github/license/lw-xiong/wenshu)](https://github.com/lw-xiong/wenshu/blob/main/LICENSE)

**A Mandarin Chinese flashcard and practice app for HSK vocabulary learning.**
Built from the official HSK Standard Course 2 textbook with vocabulary, textbook sentences, and HSK-inspired exercises.
Tap-to-reveal flashcards, seven exercise formats, TTS pronunciation, rewards, and offline support.

---

## Features

- **HSK 1–5 modules** with lesson-based curriculum (HSK 2 complete from textbook)
- **Tap-to-reveal flashcards** — hanzi + pinyin front, meaning back, smooth flip animation
- **TTS pronunciation** — Android TextToSpeech for Chinese audio
- **Progress tracking** — lessons done, words learned, persists across restarts
- **Vocabulary library** — searchable word list with HSK level tags
- **Four practice difficulties** — Easy (10), Medium (15), Hard (20), and HSK Reading (25)
- **Seven exercise formats** — meaning, hanzi, pinyin, listening, true/false, fill-blank, and sentence order
- **Pair matching** — separate vocabulary and textbook-sentence boards with first-attempt scoring
- **Coins and achievements** — persistent balances, perfect-session bonuses, and real milestone progress
- **True dark mode** — persistent Switch control with matching app, status-bar, and navigation-bar colors
- **Offline-first** — no network dependency, all data bundled in APK
- **AntiSlopUI design** — cinnabar red accent, deep ink + rice paper neutrals, no purple, no gradient blobs

## Screenshots

| Home | Learn | Flashcard |
|------|-------|-----------|
| Dashboard with progress and course cards | Lesson list with completion states | Tap-to-reveal hanzi → pinyin → meaning |

## Build

No Gradle. No Android Studio. Pure Kotlin → signed APK via manual toolchain.

```bash
# Requirements: Android SDK (platforms/android-33, build-tools/34.0.0), kotlinc-jvm 2.4.21
./build.sh
```

The build script:
1. `aapt2 compile` — compile resources
2. `aapt2 link` — generate R.java + linked APK
3. `javac` — compile R.java
4. `kotlinc-jvm` — compile Kotlin source + stdlib
5. `d8` — convert to DEX (with kotlin-stdlib bundled)
6. `apksigner` — sign with keystore (v1/v2/v3)
7. `aapt2 dump badging` — verify package, label, activity

## Install

```bash
adb install -r build/hsk-app.apk
```

Or transfer the APK to your Android device and install manually.

## Architecture

Single-activity container with bottom navigation (Home, Learn, Vocabulary, Practice, Profile).
Programmatic UI built with framework views only — no androidx, no Gradle dependencies.
Kotlin stdlib bundled in DEX (~2.3MB) to prevent `NoClassDefFoundError` crashes.

## Data

HSK 2 lessons 1–15 extracted from the official HSK Standard Course 2 PDF (OCR'd to markdown).
Practice uses 181 verified HSK 1–2 entries and 15 textbook sentences from lessons 1–5.
Out-of-syllabus words (Level 3) are flagged in the source data.

Exercise research and its limits: [docs/practice-research.md](docs/practice-research.md).

## License

MIT
