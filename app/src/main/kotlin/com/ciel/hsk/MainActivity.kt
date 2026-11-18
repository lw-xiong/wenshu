package com.ciel.hsk

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var container: FrameLayout
    private lateinit var navBar: LinearLayout
    private var currentTab: Int = -1
    private lateinit var tts: android.speech.tts.TextToSpeech

    private val TAB_HOME = 0
    private val TAB_LEARN = 1
    private val TAB_VOCAB = 2
    private val TAB_PRACTICE = 3
    private val TAB_PROFILE = 4

    private val tabLabels = arrayOf("Home", "Learn", "Vocab", "Practice", "Profile")
    private val tabIcons = arrayOf("⌂", "学", "词", "练", "◉")

    // Design tokens — dynamic light/dark
    private fun C_BG(): Int = if (isDarkMode()) Color.parseColor("#1A1A1A") else Color.parseColor("#F8F6F2")
    private fun C_CARD(): Int = if (isDarkMode()) Color.parseColor("#2D2D2D") else Color.parseColor("#FFFFFF")
    private fun C_TEXT(): Int = if (isDarkMode()) Color.parseColor("#FFFFFF") else Color.parseColor("#171717")
    private fun C_MUTED(): Int = if (isDarkMode()) Color.parseColor("#A0A0A0") else Color.parseColor("#77736E")
    private fun C_ACCENT(): Int = if (isDarkMode()) Color.parseColor("#FF6B6B") else Color.parseColor("#C62828")
    private fun C_GOLD(): Int = if (isDarkMode()) Color.parseColor("#E8B94F") else Color.parseColor("#D6A84F")
    private fun C_SUCCESS(): Int = if (isDarkMode()) Color.parseColor("#5EC77E") else Color.parseColor("#36845B")
    private fun C_BORDER(): Int = if (isDarkMode()) Color.parseColor("#404040") else Color.parseColor("#E9E5DF")
    private fun C_HANZI(): Int = if (isDarkMode()) Color.parseColor("#FFFFFF") else Color.parseColor("#171717")
    private fun C_PINYIN(): Int = if (isDarkMode()) Color.parseColor("#A0A0A0") else Color.parseColor("#77736E")
    private fun C_LOCKED(): Int = if (isDarkMode()) Color.parseColor("#505050") else Color.parseColor("#C9C4BD")
    private fun C_DONE_BG(): Int = if (isDarkMode()) Color.parseColor("#1E3D28") else Color.parseColor("#F1F7EE")
    private fun C_DONE_TEXT(): Int = if (isDarkMode()) Color.parseColor("#5EC77E") else Color.parseColor("#36845B")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("hsk_app", Context.MODE_PRIVATE)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(C_BG())

        container = FrameLayout(this)
        container.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        ).apply { weight = 1f }
        root.addView(container)

        navBar = buildNavBar()
        root.addView(navBar)

        setContentView(root)

        tts = android.speech.tts.TextToSpeech(this) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(java.util.Locale.CHINESE)
                if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA || result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Chinese TTS not available", Toast.LENGTH_SHORT).show()
                }
            }
        }

        switchTab(TAB_HOME)
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    private fun dp(context: Context, d: Int): Int {
        return (d * context.resources.displayMetrics.density).toInt()
    }

    private fun isDarkMode(): Boolean {
        return prefs.getBoolean("dark_mode", false)
    }

    private fun setDarkMode(dark: Boolean) {
        prefs.edit().putBoolean("dark_mode", dark).apply()
        // Recreate activity to apply theme
        recreate()
    }

    /* ---------- Bottom navigation ---------- */

    private fun buildNavBar(): LinearLayout {
        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(this, 64)
        )
        bar.setBackgroundColor(C_CARD())
        bar.setPadding(dp(this, 8), dp(this, 4), dp(this, 8), dp(this, 4))
        bar.elevation = dp(this, 8).toFloat()

        for (i in tabLabels.indices) {
            val item = LinearLayout(this)
            item.orientation = LinearLayout.VERTICAL
            val ip = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            ip.weight = 1f
            item.layoutParams = ip
            item.gravity = Gravity.CENTER
            item.setPadding(dp(this, 4), dp(this, 4), dp(this, 4), dp(this, 4))
            item.tag = i
            item.contentDescription = "tab_${tabLabels[i].lowercase()}"

            val icon = TextView(this)
            icon.text = tabIcons[i]
            icon.textSize = 18f
            icon.setTypeface(icon.typeface, Typeface.BOLD)
            icon.setTextColor(C_MUTED())

            val label = TextView(this)
            label.text = tabLabels[i]
            label.textSize = 11f
            label.setTypeface(label.typeface, Typeface.BOLD)
            label.setTextColor(C_MUTED())

            item.addView(icon)
            item.addView(label)
            item.setOnClickListener { switchTab(i) }

            bar.addView(item)
        }

        return bar
    }

    private fun updateNavBar() {
        for (i in 0 until navBar.childCount) {
            val item = navBar.getChildAt(i) as LinearLayout
            val icon = item.getChildAt(0) as TextView
            val label = item.getChildAt(1) as TextView
            val active = i == currentTab
            val c = if (active) C_ACCENT() else C_MUTED()
            icon.setTextColor(c)
            label.setTextColor(c)
        }
    }

    /* ---------- Tab switching ---------- */

    private fun switchTab(tab: Int) {
        if (currentTab == tab) return
        currentTab = tab
        updateNavBar()
        container.removeAllViews()
        when (tab) {
            TAB_HOME -> container.addView(buildHomeScreen(this))
            TAB_LEARN -> container.addView(buildLearnScreen(this))
            TAB_VOCAB -> container.addView(buildVocabScreen(this))
            TAB_PRACTICE -> container.addView(buildPracticeHub(this))
            TAB_PROFILE -> container.addView(buildProfileScreen(this))
        }
    }

    /* ---------- Helpers ---------- */

    private fun tv(context: Context, text: String, size: Float, color: Int, bold: Boolean = false): TextView {
        val v = TextView(context)
        v.text = text
        v.textSize = size
        v.setTextColor(color)
        if (bold) v.setTypeface(v.typeface, Typeface.BOLD)
        return v
    }

    private fun roundedBg(context: Context, fill: Int, stroke: Int, radius: Int = 16): android.graphics.drawable.GradientDrawable {
        return android.graphics.drawable.GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(context, radius).toFloat()
            setStroke(dp(context, 1), stroke)
        }
    }

    private fun card(context: Context): LinearLayout {
        val c = LinearLayout(context)
        c.orientation = LinearLayout.VERTICAL
        c.background = roundedBg(context, C_CARD(), C_BORDER())
        c.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 16))
        return c
    }

    private fun countDone(level: Int): Int {
        var done = 0
        val mod = HskData.modules().first { it.level == level }
        mod.lessons.forEach { l ->
            if (prefs.getBoolean("done:${l.id}", false)) done++
        }
        return done
    }

    private fun wlp(context: Context): LinearLayout.LayoutParams {
        val p = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        p.weight = 1f
        return p
    }

    private fun progressBar(context: Context, pct: Int): ProgressBar {
        val pb = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        pb.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(context, 5)
        )
        pb.max = 100
        pb.progress = pct.coerceIn(0, 100)
        pb.setProgressTintList(ColorStateList.valueOf(C_ACCENT()))
        pb.setProgressBackgroundTintList(ColorStateList.valueOf(C_BORDER()))
        return pb
    }

    private fun backButton(context: Context, onClick: () -> Unit): TextView {
        val b = TextView(context)
        b.text = "←"
        b.textSize = 24f
        b.setTextColor(C_MUTED())
        b.gravity = Gravity.CENTER
        b.layoutParams = ViewGroup.LayoutParams(dp(context, 44), dp(context, 44))
        b.setOnClickListener { onClick() }
        return b
    }

    private fun primaryButton(context: Context, text: String, onClick: () -> Unit): Button {
        val b = Button(context)
        b.text = text
        b.textSize = 15f
        b.setTextColor(Color.parseColor("#FFFFFF"))
        b.background = roundedBg(context, C_ACCENT(), C_ACCENT(), 12)
        b.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        b.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 14))
        b.isAllCaps = false
        b.setOnClickListener { onClick() }
        return b
    }

    private fun updateNavButtons(btnPrev: TextView, btnNext: Button, currentIdx: Int, totalWords: Int) {
        btnPrev.isEnabled = currentIdx > 0
        btnNext.isEnabled = true // Always enabled: at last word becomes "Next lesson"
    }

    /* ========================================================================
       HOME SCREEN — dashboard
       ======================================================================== */

    private fun buildHomeScreen(context: Context): View {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(C_BG())
        root.setPadding(dp(context, 16), 0, dp(context, 16), 0)

        val scroll = ScrollView(context)
        scroll.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        content.setPadding(0, dp(context, 20), 0, dp(context, 80))

        // Greeting
        content.addView(tv(context, "Welcome back.", 24f, C_TEXT(), true))

        val sub = tv(context, "Keep building your Mandarin.", 14f, C_MUTED(), false)
        sub.setPadding(0, dp(context, 6), 0, dp(context, 20))
        content.addView(sub)

        // Progress card
        val totalLessons = HskData.modules().sumOf { it.lessons.size }
        var doneLessons = 0
        for (mod in HskData.modules()) {
            for (l in mod.lessons) {
                if (prefs.getBoolean("done:${l.id}", false)) doneLessons++
            }
        }

        val progressCard = card(context)
        progressCard.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val pTitle = tv(context, "Your progress", 16f, C_TEXT(), true)
        pTitle.setPadding(0, 0, 0, dp(context, 12))
        progressCard.addView(pTitle)

        val row1 = LinearLayout(context)
        row1.orientation = LinearLayout.HORIZONTAL
        row1.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        row1.setPadding(0, 0, 0, dp(context, 16))

        row1.addView(statColumn(context, doneLessons.toString(), "lessons done"), wlp(context))
        row1.addView(statColumn(context, HskData.allWords().size.toString(), "words total"), wlp(context))
        row1.addView(statColumn(context, prefs.getInt("xp_total", 0).toString(), "XP earned"), wlp(context))
        progressCard.addView(row1)

        progressCard.addView(progressBar(context, if (totalLessons > 0) (doneLessons * 100 / totalLessons) else 0))
        content.addView(progressCard)

        // Continue learning
        val contCard = card(context)
        contCard.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val cTitle = tv(context, "Continue learning", 16f, C_TEXT(), true)
        cTitle.setPadding(0, 0, 0, dp(context, 12))
        contCard.addView(cTitle)

        var nextLesson: HskLesson? = null
        var nextLevel = 1
        outer@ for (mod in HskData.modules()) {
            for (l in mod.lessons) {
                if (!prefs.getBoolean("done:${l.id}", false)) {
                    nextLesson = l
                    nextLevel = mod.level
                    break@outer
                }
            }
        }

        val nextBtn = primaryButton(
            context,
            if (nextLesson != null) "${nextLesson.title}  ·  HSK $nextLevel" else "All lessons complete"
        ) {
            if (nextLesson != null) openLesson(nextLevel, HskData.modules().first { it.level == nextLevel }.lessons.indexOf(nextLesson))
        }
        contCard.addView(nextBtn)
        content.addView(contCard)

        // HSK course cards
        content.addView(tv(context, "HSK courses", 16f, C_TEXT(), true).apply {
            setPadding(0, dp(context, 24), 0, dp(context, 12))
        })

        for (mod in HskData.modules()) {
            content.addView(courseCard(context, mod))
        }

        content.addView(View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 100))
        })

        scroll.addView(content)
        root.addView(scroll)
        return root
    }

    private fun statColumn(context: Context, value: String, label: String): LinearLayout {
        val col = LinearLayout(context)
        col.orientation = LinearLayout.VERTICAL
        col.layoutParams = wlp(context)
        col.gravity = Gravity.CENTER

        col.addView(tv(context, value, 20f, C_ACCENT(), true))
        val l = tv(context, label, 11f, C_MUTED(), true)
        l.setPadding(dp(context, 4), 0, 0, 0)
        col.addView(l)
        return col
    }

    private fun courseCard(context: Context, mod: HskModule): LinearLayout {
        val card = card(context)
        card.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        card.setPadding(dp(context, 16), dp(context, 14), dp(context, 16), dp(context, 14))

        val header = LinearLayout(context)
        header.orientation = LinearLayout.HORIZONTAL
        header.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val badge = TextView(context)
        badge.text = mod.level.toString()
        badge.textSize = 14f
        badge.setTypeface(badge.typeface, Typeface.BOLD)
        badge.setTextColor(Color.parseColor("#FFFFFF"))
        badge.background = roundedBg(context, C_ACCENT(), C_ACCENT(), 12)
        badge.layoutParams = ViewGroup.LayoutParams(dp(context, 32), dp(context, 32))
        badge.gravity = Gravity.CENTER
        header.addView(badge)

        val info = LinearLayout(context)
        info.orientation = LinearLayout.VERTICAL
        val ip = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        ip.weight = 1f
        info.layoutParams = ip
        info.setPadding(dp(context, 10), 0, 0, 0)

        info.addView(tv(context, mod.title, 17f, C_TEXT(), true))
        info.addView(tv(context, mod.subtitle, 13f, C_MUTED(), false))

        header.addView(info)
        card.addView(header)

        val done = countDone(mod.level)
        val total = mod.lessons.size
        card.addView(tv(context, "$done / $total lessons", 12f, C_MUTED(), false).apply {
            setPadding(0, dp(context, 6), 0, 0)
        })

        card.addView(progressBar(context, if (total > 0) (done * 100 / total) else 0))

        if (mod.level > 2) {
            card.addView(tv(context, "Coming soon", 12f, C_MUTED(), false).apply {
                setPadding(0, dp(context, 6), 0, 0)
            })
        } else {
            card.setOnClickListener {
                prefs.edit().putInt("selected_hsk", mod.level).apply()
                switchTab(TAB_LEARN)
            }
        }

        return card
    }

    /* ========================================================================
       LEARN SCREEN — HSK course with lesson cards
       ======================================================================== */

    private fun buildLearnScreen(context: Context): View {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(C_BG())

        val scroll = ScrollView(context)
        scroll.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        content.setPadding(dp(context, 16), dp(context, 16), 0, dp(context, 80))

        // Back + title
        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        top.setPadding(0, 0, 0, dp(context, 16))

        top.addView(backButton(context) { switchTab(TAB_HOME) })

        val selectedLevel = prefs.getInt("selected_hsk", 1)
        val mod = HskData.modules().first { it.level == selectedLevel }

        val title = tv(context, mod.title, 20f, C_TEXT(), true)
        title.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setPadding(dp(context, 10), 0, 0, 0)
        title.gravity = Gravity.CENTER_VERTICAL
        top.addView(title)

        content.addView(top)

        content.addView(tv(context, mod.subtitle, 13f, C_MUTED(), false).apply {
            setPadding(0, 0, 0, dp(context, 16))
        })

        if (selectedLevel > 2) {
            content.addView(tv(context, "Coming soon", 20f, C_ACCENT(), true).apply {
                setPadding(0, dp(context, 40), 0, 0)
                gravity = Gravity.CENTER
            })
        } else {
            for ((index, lesson) in mod.lessons.withIndex()) {
                content.addView(lessonCard(context, mod, index + 1, lesson))
            }
        }

        content.addView(View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 100))
        })

        scroll.addView(content)
        root.addView(scroll)
        return root
    }

    private fun lessonCard(context: Context, mod: HskModule, num: Int, lesson: HskLesson): LinearLayout {
        val card = card(context)
        card.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val done = prefs.getBoolean("done:${lesson.id}", false)

        val header = LinearLayout(context)
        header.orientation = LinearLayout.HORIZONTAL
        header.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        header.addView(tv(context, "Lesson $num", 12f, C_ACCENT(), true))

        val state = tv(
            context,
            if (done) "Completed" else "Available",
            11f,
            if (done) C_SUCCESS() else C_MUTED(),
            true
        )
        state.gravity = Gravity.END
        state.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        header.addView(state)
        card.addView(header)

        card.addView(tv(context, lesson.title, 17f, C_TEXT(), true).apply {
            setPadding(0, dp(context, 6), 0, dp(context, 2))
        })
        card.addView(tv(context, lesson.sub, 13f, C_MUTED(), false))

        card.setOnClickListener { openLesson(mod.level, mod.lessons.indexOf(lesson)) }

        return card
    }

    /* ========================================================================
       LESSON SCREEN — tap-to-reveal flashcard
       ======================================================================== */

    private fun openLesson(hskLevel: Int, lessonIndex: Int) {
        currentTab = -1 // Sentinel: allow back to Learn
        val mod = HskData.modules().first { it.level == hskLevel }
        val lesson = mod.lessons[lessonIndex]
        container.removeAllViews()
        container.addView(buildLessonScreen(this, mod, lessonIndex, lesson))
    }

    private fun buildLessonScreen(context: Context, mod: HskModule, lessonIndex: Int, lesson: HskLesson): View {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(C_BG())

        val scroll = ScrollView(context)
        scroll.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        content.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 80))

        // Top bar
        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        top.setPadding(0, 0, 0, dp(context, 16))

        top.addView(backButton(context) { switchTab(TAB_LEARN) })

        val title = tv(context, "${mod.title} · Lesson ${lessonIndex + 1}", 18f, C_TEXT(), true)
        title.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setPadding(dp(context, 10), 0, 0, 0)
        title.gravity = Gravity.CENTER_VERTICAL
        top.addView(title)

        content.addView(top)

        // Progress
        val totalWords = lesson.words.size
        val progressKey = "card_progress:${lesson.id}"
        var currentIdx = prefs.getInt(progressKey, 0).coerceIn(0, totalWords - 1)
        var revealed = false

        content.addView(tv(context, "Progress", 11f, C_MUTED(), true).apply {
            setPadding(0, 0, 0, dp(context, 6))
        })
        content.addView(progressBar(context, if (totalWords > 0) ((currentIdx + 1) * 100 / totalWords).coerceAtMost(100) else 100))

        // Flashcard
        val cardStage = LinearLayout(context)
        cardStage.orientation = LinearLayout.VERTICAL
        cardStage.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cardStage.setPadding(dp(context, 24), dp(context, 24), dp(context, 24), dp(context, 24))
        cardStage.gravity = Gravity.CENTER
        cardStage.background = roundedBg(context, C_CARD(), C_BORDER())

        val hanzi = tv(context, lesson.words[currentIdx].hanzi, 64f, C_HANZI(), true)
        hanzi.gravity = Gravity.CENTER
        cardStage.addView(hanzi)

        // Audio pronunciation button
        val audioBtn = TextView(context)
        audioBtn.text = "🔊 Play"
        audioBtn.textSize = 14f
        audioBtn.setTextColor(C_ACCENT())
        audioBtn.gravity = Gravity.CENTER
        audioBtn.setPadding(dp(context, 8), dp(context, 6), dp(context, 8), dp(context, 6))
        audioBtn.setOnClickListener {
            if (::tts.isInitialized) {
                tts.speak(lesson.words[currentIdx].hanzi, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Toast.makeText(context, "Audio unavailable", Toast.LENGTH_SHORT).show()
            }
        }
        cardStage.addView(audioBtn)

        val pinyin = tv(context, lesson.words[currentIdx].pinyin, 18f, C_PINYIN(), false)
        pinyin.gravity = Gravity.CENTER
        pinyin.setPadding(0, dp(context, 4), 0, 0)
        cardStage.addView(pinyin)

        val hint = tv(context, "Tap card to reveal meaning", 11f, C_MUTED(), false)
        hint.gravity = Gravity.CENTER
        hint.setPadding(0, dp(context, 8), 0, dp(context, 10))
        cardStage.addView(hint)

        val meaning = tv(
            context,
            lesson.words[currentIdx].meaning,
            16f,
            C_ACCENT(),
            true
        )
        meaning.gravity = Gravity.CENTER
        meaning.setPadding(0, 0, 0, dp(context, 10))
        meaning.visibility = View.GONE
        cardStage.addView(meaning)

        cardStage.setOnClickListener {
            if (!revealed) {
                // Show meaning: hide hanzi, show meaning
                hanzi.animate().rotationY(90f).setDuration(200).withEndAction {
                    hanzi.visibility = View.GONE
                    pinyin.visibility = View.GONE
                    hint.visibility = View.GONE
                    meaning.rotationY = 270f
                    meaning.visibility = View.VISIBLE
                    meaning.animate().rotationY(360f).setDuration(200).start()
                }.start()
                revealed = true
            } else {
                // Show hanzi: hide meaning, show hanzi
                meaning.animate().rotationY(90f).setDuration(200).withEndAction {
                    meaning.visibility = View.GONE
                    hanzi.rotationY = 270f
                    hanzi.visibility = View.VISIBLE
                    pinyin.visibility = View.VISIBLE
                    hint.visibility = View.VISIBLE
                    hanzi.animate().rotationY(360f).setDuration(200).start()
                }.start()
                revealed = false
            }
        }

        content.addView(cardStage)

        // Navigation — stacked vertically with proper spacing
        val nav = LinearLayout(context)
        nav.orientation = LinearLayout.VERTICAL
        nav.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        nav.setPadding(0, dp(context, 12), 0, 0)

        val btnNext = Button(context)
        val btnPrev = TextView(context)

        btnNext.text = if (currentIdx >= totalWords - 1) "Next lesson" else "Next"
        btnNext.textSize = 15f
        btnNext.setTextColor(Color.parseColor("#FFFFFF"))
        btnNext.background = roundedBg(context, C_ACCENT(), C_ACCENT(), 12)
        btnNext.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        btnNext.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 14))
        btnNext.isAllCaps = false

        btnPrev.text = "Previous"
        btnPrev.textSize = 14f
        btnPrev.setTextColor(C_ACCENT())
        btnPrev.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
        btnPrev.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        btnPrev.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 14))
        btnPrev.gravity = Gravity.CENTER
        btnPrev.isEnabled = currentIdx > 0

        // Shared navigation logic
        fun goTo(index: Int) {
            hanzi.animate().cancel()
            meaning.animate().cancel()
            revealed = false
            hanzi.rotationY = 0f
            pinyin.rotationY = 0f
            meaning.rotationY = 0f
            hanzi.visibility = View.VISIBLE
            pinyin.visibility = View.VISIBLE
            hint.visibility = View.VISIBLE
            meaning.visibility = View.GONE
            currentIdx = index
            prefs.edit().putInt(progressKey, currentIdx).apply()
            hanzi.text = lesson.words[currentIdx].hanzi
            pinyin.text = lesson.words[currentIdx].pinyin
            meaning.text = lesson.words[currentIdx].meaning
            updateNavButtons(btnPrev, btnNext, currentIdx, totalWords)
            btnNext.text = if (currentIdx >= totalWords - 1) "Next lesson" else "Next"
        }

        btnNext.setOnClickListener {
            if (currentIdx >= totalWords - 1) {
                // At last word: mark done and go to next lesson
                prefs.edit().putBoolean("done:${lesson.id}", true).apply()
                prefs.edit().putInt(progressKey, totalWords).apply()
                val nextIdx = lessonIndex + 1
                if (nextIdx < mod.lessons.size) {
                    openLesson(mod.level, nextIdx)
                } else {
                    Toast.makeText(context, "All lessons complete", Toast.LENGTH_SHORT).show()
                    switchTab(TAB_LEARN)
                }
            } else {
                goTo((currentIdx + 1) % totalWords)
            }
        }
        btnPrev.setOnClickListener { goTo((currentIdx - 1 + totalWords) % totalWords) }

        nav.addView(btnNext)

        val spacer = View(context)
        spacer.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(context, 10)
        )
        nav.addView(spacer)

        nav.addView(btnPrev)

        // Shuffle button
        val btnShuffle = TextView(context)
        btnShuffle.text = "Shuffle"
        btnShuffle.textSize = 14f
        btnShuffle.setTextColor(C_ACCENT())
        btnShuffle.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
        btnShuffle.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        btnShuffle.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 14))
        btnShuffle.gravity = Gravity.CENTER
        btnShuffle.setOnClickListener {
            lesson.words = lesson.words.shuffled()
            goTo(0)
            Toast.makeText(context, "Words shuffled", Toast.LENGTH_SHORT).show()
        }
        nav.addView(btnShuffle)

        content.addView(nav)

        scroll.addView(content)
        root.addView(scroll)
        return root
    }

    private fun getStringRes(context: Context, id: Int, fallback: String): String {
        return try { context.getString(id) } catch (_: Exception) { fallback }
    }

    /* ========================================================================
       VOCABULARY SCREEN
       ======================================================================== */

    private fun buildVocabScreen(context: Context): View {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(C_BG())

        val scroll = ScrollView(context)
        scroll.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        content.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 80))

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        top.setPadding(0, 0, 0, dp(context, 16))

        top.addView(backButton(context) { switchTab(TAB_HOME) })

        val title = tv(context, "Vocabulary", 20f, C_TEXT(), true)
        title.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setPadding(dp(context, 10), 0, 0, 0)
        title.gravity = Gravity.CENTER_VERTICAL
        top.addView(title)

        content.addView(top)

        val search = EditText(context)
        search.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        search.setHint("Search words")
        search.setHintTextColor(C_MUTED())
        search.setTextColor(C_TEXT())
        search.textSize = 15f
        search.setPadding(dp(context, 14), dp(context, 12), dp(context, 14), dp(context, 12))
        search.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
        content.addView(search)

        val allWords = HskData.allWords()
        val vocabContainer = LinearLayout(context)
        vocabContainer.orientation = LinearLayout.VERTICAL
        vocabContainer.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        fun refresh(filter: String) {
            vocabContainer.removeAllViews()
            var shown = 0
            for (w in allWords) {
                val haystack = (w.hanzi + " " + w.pinyin + " " + w.meaning).lowercase()
                if (filter.isEmpty() || haystack.contains(filter.lowercase())) {
                    vocabContainer.addView(vocabRow(context, w))
                    shown++
                }
            }
            if (shown == 0) {
                val empty = tv(context, "No words found", 15f, C_MUTED(), false)
                empty.setPadding(0, dp(context, 20), 0, 0)
                empty.gravity = Gravity.CENTER
                vocabContainer.addView(empty)
            }
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = refresh(s?.toString() ?: "")
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        refresh("")

        content.addView(vocabContainer)

        content.addView(View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 100))
        })

        scroll.addView(content)
        root.addView(scroll)
        return root
    }

    private fun vocabRow(context: Context, w: HskData.WordEntry): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = LinearLayout.VERTICAL
        row.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        row.setPadding(dp(context, 12), dp(context, 10), dp(context, 12), dp(context, 10))

        row.addView(tv(context, w.hanzi, 22f, C_TEXT(), true))
        row.addView(tv(context, w.pinyin, 15f, C_PINYIN(), false))
        row.addView(tv(context, w.meaning, 15f, C_TEXT(), false))
        row.addView(tv(context, "HSK ${w.hskLevel}", 10f, C_ACCENT(), true).apply {
            setPadding(0, dp(context, 4), 0, 0)
        })

        row.setOnClickListener {
            Toast.makeText(context, w.meaning, Toast.LENGTH_SHORT).show()
        }

        return row
    }

    /* ========================================================================
       REVIEW SCREEN — marked words
       ======================================================================== */

    private fun buildPracticeHub(context: Context): View {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(C_BG())

        val scroll = ScrollView(context)
        scroll.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        content.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 80))

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        top.setPadding(0, 0, 0, dp(context, 16))

        top.addView(backButton(context) { switchTab(TAB_HOME) })

        val title = tv(context, "Practice", 20f, C_TEXT(), true)
        title.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setPadding(dp(context, 10), 0, 0, 0)
        title.gravity = Gravity.CENTER_VERTICAL
        top.addView(title)

        content.addView(top)

        content.addView(tv(context, "Targeted practice · ${prefs.getInt("xp_total", 0)} XP earned", 13f, C_MUTED(), false).apply {
            setPadding(0, 0, 0, dp(context, 16))
        })

        // Practice options
        val practiceOptions = listOf(
            "Review mistakes" to "Words you got wrong",
            "Practice words" to "Spaced repetition review",
            "Quick practice" to "Random vocabulary drill"
        )

        for ((title, sub) in practiceOptions) {
            val optionCard = card(context)
            optionCard.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            optionCard.setPadding(dp(context, 16), dp(context, 14), dp(context, 16), dp(context, 14))

            val header = LinearLayout(context)
            header.orientation = LinearLayout.HORIZONTAL
            header.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            header.addView(tv(context, title, 16f, C_TEXT(), true))

            val arrow = tv(context, "›", 20f, C_ACCENT(), true)
            arrow.gravity = Gravity.END
            header.addView(arrow)

            optionCard.addView(header)
            optionCard.addView(tv(context, sub, 13f, C_MUTED(), false).apply {
                setPadding(0, dp(context, 4), 0, 0)
            })

            optionCard.setOnClickListener {
                when (title) {
                    "Review mistakes" -> startPracticeSession(context, "mistakes")
                    "Practice words" -> startPracticeSession(context, "spaced")
                    "Quick practice" -> startPracticeSession(context, "random")
                }
            }

            content.addView(optionCard)
        }

        content.addView(View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 100))
        })

        scroll.addView(content)
        root.addView(scroll)
        return root
    }

    private fun startPracticeSession(context: Context, mode: String) {
        val source = when (mode) {
            "mistakes" -> {
                val marked = prefs.getStringSet("review_words", emptySet()) ?: emptySet()
                HskData.allWords().filter { it.hanzi in marked }
            }
            "spaced" -> HskData.allWords().filter {
                !prefs.getBoolean("done:${it.lessonId}", false)
            }
            else -> HskData.allWords()
        }
        val sessionWords = source.distinctBy { it.hanzi }.shuffled()
            .take(if (mode == "random") 5 else 10)

        if (sessionWords.isEmpty()) {
            Toast.makeText(context, "No words to practice yet", Toast.LENGTH_SHORT).show()
            return
        }
        openPracticeQuiz(mode, sessionWords)
    }

    private fun openPracticeQuiz(mode: String, words: List<HskData.WordEntry>) {
        currentTab = -1
        container.removeAllViews()
        val context = this

        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(C_BG())

        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        content.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 80))

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.gravity = Gravity.CENTER_VERTICAL
        top.addView(backButton(context) { switchTab(TAB_PRACTICE) })
        val title = when (mode) {
            "mistakes" -> "Review Mistakes"
            "spaced" -> "Practice Words"
            else -> "Quick Practice"
        }
        top.addView(tv(context, title, 20f, C_TEXT(), true).apply {
            setPadding(dp(context, 10), 0, 0, 0)
        })
        content.addView(top)

        val counter = tv(context, "", 12f, C_MUTED(), true)
        counter.setPadding(0, dp(context, 16), 0, dp(context, 6))
        content.addView(counter)
        val progress = progressBar(context, 0)
        content.addView(progress)

        val prompt = tv(context, "", 48f, C_HANZI(), true)
        prompt.gravity = Gravity.CENTER
        prompt.setPadding(0, dp(context, 28), 0, dp(context, 8))
        content.addView(prompt)

        val instruction = tv(context, "", 15f, C_MUTED(), false)
        instruction.gravity = Gravity.CENTER
        instruction.setPadding(0, 0, 0, dp(context, 24))
        content.addView(instruction)

        val optionsBox = LinearLayout(context)
        optionsBox.orientation = LinearLayout.VERTICAL
        optionsBox.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        content.addView(optionsBox)

        val feedback = tv(context, "", 15f, C_TEXT(), true)
        feedback.setPadding(0, dp(context, 12), 0, dp(context, 12))
        content.addView(feedback)

        var index = 0
        var score = 0
        var selected: HskData.WordEntry? = null
        var answered = false
        lateinit var renderQuestion: () -> Unit

        val checkBtn = primaryButton(context, "Check") {}
        checkBtn.isEnabled = false
        checkBtn.setOnClickListener {
            if (!answered) {
                val choice = selected ?: return@setOnClickListener
                val correct = words[index]
                answered = true
                val isCorrect = choice.hanzi == correct.hanzi
                if (isCorrect) score++

                val mistakes = prefs.getStringSet("review_words", emptySet())?.toMutableSet()
                    ?: mutableSetOf()
                if (isCorrect) mistakes.remove(correct.hanzi) else mistakes.add(correct.hanzi)
                prefs.edit().putStringSet("review_words", mistakes).apply()

                feedback.text = if (isCorrect) {
                    "Correct!"
                } else {
                    "Correct answer: ${correct.hanzi} · ${correct.pinyin} · ${correct.meaning}"
                }
                feedback.setTextColor(if (isCorrect) C_SUCCESS() else C_ACCENT())
                checkBtn.text = if (index == words.lastIndex) "See results" else "Continue"
            } else if (index == words.lastIndex) {
                showPracticeResults(mode, score, words.size)
            } else {
                index++
                renderQuestion()
            }
        }

        renderQuestion = {
            selected = null
            answered = false
            feedback.text = ""
            checkBtn.text = "Check"
            checkBtn.isEnabled = false

            val correct = words[index]
            val askMeaning = index % 2 == 0
            prompt.text = if (askMeaning) correct.hanzi else correct.meaning
            prompt.textSize = if (askMeaning) 48f else 24f
            instruction.text = if (askMeaning) {
                "${correct.pinyin}\nChoose the correct meaning"
            } else {
                "Choose the correct hanzi"
            }
            counter.text = "QUESTION ${index + 1} OF ${words.size}"
            progress.progress = index * 100 / words.size

            optionsBox.removeAllViews()
            val distractors = HskData.allWords()
                .filter { it.hanzi != correct.hanzi }
                .shuffled().take(3)
            val options = (distractors + correct).shuffled()

            for (option in options) {
                val optionBtn = Button(context)
                optionBtn.text = if (askMeaning) option.meaning else option.hanzi
                optionBtn.textSize = if (askMeaning) 16f else 28f
                optionBtn.setTextColor(C_TEXT())
                optionBtn.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
                optionBtn.isAllCaps = false
                optionBtn.gravity = Gravity.CENTER
                optionBtn.setPadding(dp(context, 14), dp(context, 16), dp(context, 14), dp(context, 16))
                optionBtn.setOnClickListener {
                    if (answered) return@setOnClickListener
                    selected = option
                    checkBtn.isEnabled = true
                    for (i in 0 until optionsBox.childCount) {
                        val child = optionsBox.getChildAt(i)
                        if (child is Button) {
                            child.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
                            child.setTextColor(C_TEXT())
                        }
                    }
                    optionBtn.background = roundedBg(context, C_ACCENT(), C_ACCENT(), 12)
                    optionBtn.setTextColor(Color.WHITE)
                }
                optionsBox.addView(optionBtn)
                optionsBox.addView(View(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 10)
                    )
                })
            }
        }

        content.addView(checkBtn)
        root.addView(content)
        container.addView(root)
        renderQuestion()
    }

    private fun showPracticeResults(mode: String, score: Int, total: Int) {
        val baseXp = when (mode) {
            "mistakes" -> 20
            "spaced" -> 10
            else -> 5
        }
        val earned = score * baseXp / total
        prefs.edit().putInt("xp_total", prefs.getInt("xp_total", 0) + earned).apply()

        currentTab = -1
        container.removeAllViews()
        val context = this
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER
        root.setPadding(dp(context, 24), dp(context, 24), dp(context, 24), dp(context, 80))
        root.setBackgroundColor(C_BG())

        val resultCard = card(context)
        resultCard.gravity = Gravity.CENTER
        resultCard.addView(tv(context, "Practice complete", 24f, C_TEXT(), true).apply {
            gravity = Gravity.CENTER
        })
        resultCard.addView(tv(context, "$score / $total correct", 18f, C_TEXT(), false).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(context, 16), 0, 0)
        })
        resultCard.addView(tv(context, "${score * 100 / total}% accuracy", 14f, C_MUTED(), false).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(context, 4), 0, dp(context, 16))
        })
        resultCard.addView(tv(context, "+$earned XP", 32f, C_SUCCESS(), true).apply {
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(context, 24))
        })
        resultCard.addView(primaryButton(context, "Back to Practice") {
            switchTab(TAB_PRACTICE)
        })
        root.addView(resultCard)
        container.addView(root)
    }

    private fun reviewRow(context: Context, w: HskData.WordEntry): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = LinearLayout.VERTICAL
        row.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        row.setPadding(dp(context, 12), dp(context, 10), dp(context, 12), dp(context, 10))

        row.addView(tv(context, w.hanzi, 22f, C_TEXT(), true))
        row.addView(tv(context, w.pinyin, 15f, C_PINYIN(), false))
        row.addView(tv(context, w.meaning, 15f, C_TEXT(), false))

        row.setOnClickListener {
            Toast.makeText(context, w.meaning, Toast.LENGTH_SHORT).show()
        }

        return row
    }

    /* ========================================================================
       PROFILE SCREEN
       ======================================================================== */

    private fun buildProfileScreen(context: Context): View {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(C_BG())

        val scroll = ScrollView(context)
        scroll.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        content.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 80))

        val avatar = TextView(context)
        avatar.text = "问书"
        avatar.textSize = 28f
        avatar.setTypeface(avatar.typeface, Typeface.BOLD)
        avatar.setTextColor(Color.WHITE)
        avatar.background = roundedBg(context, C_ACCENT(), C_ACCENT(), 12)
        avatar.layoutParams = ViewGroup.LayoutParams(dp(context, 96), dp(context, 64))
        avatar.gravity = Gravity.CENTER

        val avatarWrap = LinearLayout(context)
        avatarWrap.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        avatarWrap.gravity = Gravity.CENTER
        avatarWrap.addView(avatar)
        content.addView(avatarWrap)

        content.addView(tv(context, "Learner", 20f, C_TEXT(), true).apply {
            setPadding(0, dp(context, 8), 0, dp(context, 2))
            gravity = Gravity.CENTER
        })

        content.addView(tv(context, "HSK 2 · 300 words", 13f, C_MUTED(), false).apply {
            gravity = Gravity.CENTER
        })

        content.addView(View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 24))
        })

        val statsCard = card(context)
        statsCard.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val statsTitle = tv(context, "Your stats", 16f, C_TEXT(), true)
        statsTitle.setPadding(0, 0, 0, dp(context, 12))
        statsCard.addView(statsTitle)

        val totalLessons = HskData.modules().sumOf { it.lessons.size }
        var doneLessons = 0
        for (mod in HskData.modules()) {
            for (l in mod.lessons) {
                if (prefs.getBoolean("done:${l.id}", false)) doneLessons++
            }
        }

        val row = LinearLayout(context)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        row.addView(statBox(context, doneLessons.toString(), "Lessons done"))
        row.addView(dividerLine(context))
        row.addView(statBox(context, totalLessons.toString(), "Total lessons"))

        statsCard.addView(row)
        content.addView(statsCard)

        // Settings card
        val settingsCard = card(context)
        settingsCard.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        settingsCard.setPadding(0, dp(context, 24), 0, dp(context, 12))

        val sTitle = tv(context, "Settings", 16f, C_TEXT(), true)
        sTitle.setPadding(0, 0, 0, dp(context, 12))
        settingsCard.addView(sTitle)

        // Dark mode toggle
        val darkRow = LinearLayout(context)
        darkRow.orientation = LinearLayout.HORIZONTAL
        darkRow.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        darkRow.setPadding(0, 0, 0, dp(context, 12))

        darkRow.addView(tv(context, "Dark mode", 15f, C_TEXT(), false), wlp(context))

        val darkToggle = TextView(context)
        darkToggle.text = if (isDarkMode()) "ON" else "OFF"
        darkToggle.textSize = 14f
        darkToggle.setTextColor(if (isDarkMode()) C_SUCCESS() else C_MUTED())
        darkToggle.setTypeface(darkToggle.typeface, Typeface.BOLD)
        darkToggle.gravity = Gravity.CENTER
        darkToggle.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
        darkToggle.layoutParams = ViewGroup.LayoutParams(dp(context, 60), dp(context, 36))
        darkToggle.setOnClickListener {
            setDarkMode(!isDarkMode())
        }
        darkRow.addView(darkToggle)

        settingsCard.addView(darkRow)
        content.addView(settingsCard)

        content.addView(View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 100))
        })

        scroll.addView(content)
        root.addView(scroll)
        return root
    }

    private fun statBox(context: Context, value: String, label: String): LinearLayout {
        val col = LinearLayout(context)
        col.orientation = LinearLayout.VERTICAL
        col.layoutParams = wlp(context)
        col.gravity = Gravity.CENTER

        col.addView(tv(context, value, 20f, C_ACCENT(), true))

        val l = tv(context, label, 11f, C_MUTED(), true)
        l.setPadding(dp(context, 4), 0, 0, 0)
        col.addView(l)
        return col
    }

    private fun dividerLine(context: Context): View {
        val v = View(context)
        v.layoutParams = ViewGroup.LayoutParams(1, dp(context, 40))
        v.setBackgroundColor(C_BORDER())
        return v
    }
}
