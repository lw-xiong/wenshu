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
    private lateinit var rootView: LinearLayout
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
    private fun C_BG(): Int = if (isDarkMode()) Color.parseColor("#0A0A0A") else Color.parseColor("#F8F6F2")
    private fun C_CARD(): Int = if (isDarkMode()) Color.parseColor("#171717") else Color.parseColor("#FFFFFF")
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
        applySystemBars()

        rootView = LinearLayout(this)
        val root = rootView
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

    private fun applySystemBars() {
        window.statusBarColor = C_BG()
        window.navigationBarColor = C_BG()
        window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(C_BG()))
        window.decorView.setBackgroundColor(C_BG())
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            var flags = window.decorView.systemUiVisibility
            flags = if (isDarkMode()) {
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                flags = if (isDarkMode()) {
                    flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                } else {
                    flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }
            window.decorView.systemUiVisibility = flags
        }
    }

    private fun setDarkMode(dark: Boolean) {
        val tab = if (currentTab in 0..4) currentTab else TAB_PROFILE
        val scrollY = currentScrollView()?.scrollY ?: 0
        prefs.edit().putBoolean("dark_mode", dark).apply()
        applySystemBars()
        rootView.setBackgroundColor(C_BG())

        val navIndex = rootView.indexOfChild(navBar)
        rootView.removeView(navBar)
        navBar = buildNavBar()
        rootView.addView(navBar, navIndex)

        currentTab = -1
        switchTab(tab)
        currentScrollView()?.let { scroll ->
            scroll.post { scroll.scrollTo(0, scrollY) }
        }
    }

    private fun currentScrollView(): ScrollView? {
        val screen = container.getChildAt(0) as? LinearLayout ?: return null
        return screen.getChildAt(0) as? ScrollView
    }

    /* ---------- Bottom navigation ---------- */

    private fun buildNavBar(): LinearLayout {
        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.weightSum = tabLabels.size.toFloat()
        bar.gravity = Gravity.CENTER
        bar.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(this, 64)
        )
        bar.setBackgroundColor(C_CARD())
        bar.setPadding(0, dp(this, 4), 0, dp(this, 4))
        bar.elevation = dp(this, 8).toFloat()

        for (i in tabLabels.indices) {
            val item = LinearLayout(this)
            item.orientation = LinearLayout.VERTICAL
            item.layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
            )
            item.gravity = Gravity.CENTER
            item.setPadding(0, dp(this, 4), 0, dp(this, 4))
            item.tag = i
            item.contentDescription = "tab_${tabLabels[i].lowercase()}"

            val icon = TextView(this)
            icon.text = tabIcons[i]
            icon.textSize = 18f
            icon.setTypeface(icon.typeface, Typeface.BOLD)
            icon.setTextColor(C_MUTED())
            icon.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            icon.gravity = Gravity.CENTER
            icon.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val label = TextView(this)
            label.text = tabLabels[i]
            label.textSize = 11f
            label.setTypeface(label.typeface, Typeface.BOLD)
            label.setTextColor(C_MUTED())
            label.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            label.gravity = Gravity.CENTER
            label.textAlignment = View.TEXT_ALIGNMENT_CENTER

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

    private fun completedWords(lessonId: String): MutableSet<Int> {
        return try {
            val array = org.json.JSONArray(prefs.getString("completed:${lessonId}", "[]"))
            val set = mutableSetOf<Int>()
            repeat(array.length()) { set.add(array.getInt(it)) }
            set
        } catch (_: Exception) {
            mutableSetOf()
        }
    }

    private fun saveCompleted(lessonId: String, completed: Set<Int>) {
        val arr = org.json.JSONArray()
        completed.sorted().forEach { arr.put(it) }
        prefs.edit().putString("completed:${lessonId}", arr.toString()).apply()
    }

    private fun restartLesson(mod: HskModule, lessonIndex: Int, lesson: HskLesson) {
        prefs.edit()
            .putBoolean("done:${lesson.id}", false)
            .putString("completed:${lesson.id}", "[]")
            .putInt("card_progress:${lesson.id}", 0)
            .apply()
        container.removeAllViews()
        container.addView(buildLessonScreen(this, mod, lessonIndex, lesson))
    }

    private fun currentStats(): PlayerStats {
        val lessonsDone = HskData.modules().sumOf { mod ->
            mod.lessons.count { prefs.getBoolean("done:${it.id}", false) }
        }
        return PlayerStats(
            lessonsDone = lessonsDone,
            xp = prefs.getInt("xp_total", 0),
            coins = prefs.getInt("coins_total", 0),
            correctAnswers = prefs.getInt("practice_correct_total", 0),
            perfectSessions = prefs.getInt("perfect_sessions", 0)
        )
    }

    private fun recentQuestionSignatures(): List<String> {
        return try {
            val array = org.json.JSONArray(prefs.getString("recent_question_queue", "[]"))
            List(array.length()) { array.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun rememberQuestions(questions: List<PracticeQuestion>) {
        val combined = recentQuestionSignatures() + questions.map { PracticeData.signature(it) }
        val start = (combined.size - 200).coerceAtLeast(0)
        val array = org.json.JSONArray()
        for (i in start until combined.size) array.put(combined[i])
        prefs.edit().putString("recent_question_queue", array.toString()).apply()
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
            dp(context, 52)
        )
        b.minHeight = 0
        b.minimumHeight = 0
        b.gravity = Gravity.CENTER
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
        row1.addView(statColumn(context, prefs.getInt("xp_total", 0).toString(), "XP earned"), wlp(context))
        row1.addView(statColumn(context, prefs.getInt("coins_total", 0).toString(), "coins"), wlp(context))
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

        if (mod.level > 3) {
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

        if (selectedLevel > 3) {
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

        // Lesson progress — how many of this lesson's words have been reviewed
        val totalWords = lesson.words.size
        val completed = if (done) totalWords else completedWords(lesson.id).size
        val pct = if (totalWords > 0) completed * 100 / totalWords else 0
        card.addView(tv(context, "Progress", 11f, C_MUTED(), true).apply {
            setPadding(0, dp(context, 8), 0, dp(context, 6))
        })
        card.addView(progressBar(context, pct))

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

        // Progress — based on how many words in this lesson have been reviewed
        val totalWords = lesson.words.size
        val progressKey = "card_progress:${lesson.id}"
        val done = prefs.getBoolean("done:${lesson.id}", false)
        var completed = completedWords(lesson.id)
        var currentIdx = prefs.getInt(progressKey, 0).coerceIn(0, totalWords - 1)
        var revealed = false

        val progressBarRef = progressBar(context, if (done) 100 else if (totalWords > 0) completed.size * 100 / totalWords else 0)
        content.addView(tv(context, "Progress", 11f, C_MUTED(), true).apply {
            setPadding(0, 0, 0, dp(context, 6))
        })
        content.addView(progressBarRef)

        fun refreshProgress() {
            progressBarRef.progress = if (done) 100 else if (totalWords > 0) completed.size * 100 / totalWords else 0
        }

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
                // Mark this word as reviewed and update progress
                if (completed.add(currentIdx)) {
                    saveCompleted(lesson.id, completed)
                    if (completed.size >= totalWords) {
                        prefs.edit().putBoolean("done:${lesson.id}", true).apply()
                    }
                    refreshProgress()
                }
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
        val btnPrev = Button(context)

        btnNext.text = if (done) "Restart lesson" else if (currentIdx >= totalWords - 1) "Next lesson" else "Next"
        btnNext.textSize = 15f
        btnNext.setTextColor(Color.parseColor("#FFFFFF"))
        btnNext.background = roundedBg(context, C_ACCENT(), C_ACCENT(), 12)
        btnNext.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(context, 52)
        )
        btnNext.minHeight = 0
        btnNext.minimumHeight = 0
        btnNext.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 14))
        btnNext.isAllCaps = false

        btnPrev.text = "Previous"
        btnPrev.textSize = 14f
        btnPrev.setTextColor(C_ACCENT())
        btnPrev.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
        btnPrev.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(context, 52)
        )
        btnPrev.minHeight = 0
        btnPrev.minimumHeight = 0
        btnPrev.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 14))
        btnPrev.gravity = Gravity.CENTER
        btnPrev.isAllCaps = false
        btnPrev.isEnabled = currentIdx > 0 && !done

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
            btnNext.text = if (done) "Restart lesson" else if (currentIdx >= totalWords - 1) "Next lesson" else "Next"
        }

        btnNext.setOnClickListener {
            if (done) {
                restartLesson(mod, lessonIndex, lesson)
            } else if (currentIdx >= totalWords - 1) {
                // At last word: mark this word reviewed, mark lesson done, go to next lesson
                completed.add(currentIdx)
                saveCompleted(lesson.id, completed)
                prefs.edit().putBoolean("done:${lesson.id}", true).putInt(progressKey, totalWords).apply()
                refreshProgress()
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
        val btnShuffle = Button(context)
        btnShuffle.text = "Shuffle"
        btnShuffle.textSize = 14f
        btnShuffle.setTextColor(C_ACCENT())
        btnShuffle.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
        btnShuffle.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(context, 52)
        )
        btnShuffle.minHeight = 0
        btnShuffle.minimumHeight = 0
        btnShuffle.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 14))
        btnShuffle.gravity = Gravity.CENTER
        btnShuffle.isAllCaps = false
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
        root.setBackgroundColor(C_BG())

        val scroll = ScrollView(context)
        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 24))

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.gravity = Gravity.CENTER_VERTICAL
        top.setPadding(0, 0, 0, dp(context, 16))
        top.addView(backButton(context) { switchTab(TAB_HOME) })
        top.addView(tv(context, "Practice", 20f, C_TEXT(), true).apply {
            setPadding(dp(context, 10), 0, 0, 0)
        })
        content.addView(top)

        content.addView(tv(context, "${prefs.getInt("xp_total", 0)} XP · ${prefs.getInt("coins_total", 0)} coins", 14f, C_GOLD(), true).apply {
            setPadding(0, 0, 0, dp(context, 16))
        })

        val options = listOf(
            Triple("Review mistakes", "15 mixed questions · words you missed · 20 XP", "mistakes"),
            Triple("Easy", "10 questions · meanings and hanzi · 10 XP", "easy"),
            Triple("Medium", "15 questions · listening and pinyin · 20 XP", "medium"),
            Triple("Hard", "20 questions · sentences and listening · 30 XP", "hard"),
            Triple("HSK Reading", "25 questions · official reading length · 30 XP", "hsk"),
            Triple("Match words", "5 boards · 20 vocabulary pairs · 15 XP", "match_words"),
            Triple("Match sentences", "3 boards · 12 textbook sentence pairs · 20 XP", "match_sentences"),
            Triple("Hanzi writing", "10 characters · guided finger tracing · 20 XP", "writing")
        )

        for ((title, subtitle, mode) in options) {
            val optionCard = card(context)
            optionCard.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, dp(context, 12)) }
            optionCard.setPadding(dp(context, 16), dp(context, 14), dp(context, 16), dp(context, 14))

            val header = LinearLayout(context)
            header.orientation = LinearLayout.HORIZONTAL
            header.gravity = Gravity.CENTER_VERTICAL
            header.addView(tv(context, title, 16f, C_TEXT(), true), wlp(context))
            header.addView(tv(context, "›", 20f, C_ACCENT(), true))
            optionCard.addView(header)
            optionCard.addView(tv(context, subtitle, 13f, C_MUTED(), false).apply {
                setPadding(0, dp(context, 4), 0, 0)
            })
            optionCard.contentDescription = "practice_$mode"
            optionCard.setOnClickListener { startPracticeSession(context, mode) }
            content.addView(optionCard)
        }

        scroll.addView(content)
        root.addView(scroll)
        return root
    }

    private fun startPracticeSession(context: Context, mode: String) {
        if (mode == "writing") {
            openWritingPractice()
            return
        }
        if (mode.startsWith("match_")) {
            openMatchingPractice(mode)
            return
        }
        val available = HskData.allWords().filter { it.hskLevel <= 2 }
        val source = if (mode == "mistakes") {
            val marked = prefs.getStringSet("review_words", emptySet()) ?: emptySet()
            available.filter { it.hanzi in marked }
        } else {
            available
        }
        val difficulty = when (mode) {
            "easy" -> PracticeDifficulty.EASY
            "hard" -> PracticeDifficulty.HARD
            "hsk" -> PracticeDifficulty.HSK
            else -> PracticeDifficulty.MEDIUM
        }
        if (source.distinctBy { it.hanzi }.size < 4) {
            Toast.makeText(context, "At least four practice words are needed", Toast.LENGTH_SHORT).show()
            return
        }
        val recent = recentQuestionSignatures().toSet()
        val questions = PracticeData.generate(difficulty, source, exclude = recent)
        rememberQuestions(questions)
        openPracticeQuiz(mode, questions)
    }

    private fun openPracticeQuiz(mode: String, questions: List<PracticeQuestion>) {
        currentTab = -1
        container.removeAllViews()
        val context = this
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(C_BG())

        val scroll = ScrollView(context)
        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 24))

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.gravity = Gravity.CENTER_VERTICAL
        top.addView(backButton(context) { switchTab(TAB_PRACTICE) })
        val title = when (mode) {
            "mistakes" -> "Review Mistakes"
            "easy" -> "Easy Practice"
            "hard" -> "Hard Practice"
            "hsk" -> "HSK Reading"
            else -> "Medium Practice"
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

        val prompt = tv(context, "", 42f, C_HANZI(), true)
        prompt.gravity = Gravity.CENTER
        prompt.setPadding(0, dp(context, 24), 0, dp(context, 8))
        content.addView(prompt)
        val instruction = tv(context, "", 15f, C_MUTED(), false)
        instruction.gravity = Gravity.CENTER
        instruction.setPadding(0, 0, 0, dp(context, 16))
        content.addView(instruction)

        val audioBtn = TextView(context)
        audioBtn.text = "🔊 Play audio"
        audioBtn.textSize = 16f
        audioBtn.setTextColor(C_ACCENT())
        audioBtn.gravity = Gravity.CENTER
        audioBtn.setPadding(dp(context, 12), dp(context, 12), dp(context, 12), dp(context, 12))
        content.addView(audioBtn)

        val tokenAnswer = tv(context, "", 24f, C_TEXT(), true)
        tokenAnswer.gravity = Gravity.CENTER
        tokenAnswer.setPadding(dp(context, 12), dp(context, 12), dp(context, 12), dp(context, 12))
        tokenAnswer.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
        content.addView(tokenAnswer)

        val optionsBox = LinearLayout(context)
        optionsBox.orientation = LinearLayout.VERTICAL
        content.addView(optionsBox)
        val feedback = tv(context, "", 15f, C_TEXT(), true)
        feedback.setPadding(0, dp(context, 12), 0, dp(context, 12))
        content.addView(feedback)

        var index = 0
        var score = 0
        var selected: String? = null
        var answered = false
        val built = mutableListOf<String>()
        lateinit var renderQuestion: () -> Unit
        val checkBtn = primaryButton(context, "Check") {}

        audioBtn.setOnClickListener {
            val speech = questions[index].speech ?: return@setOnClickListener
            if (::tts.isInitialized) {
                tts.speak(speech, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Toast.makeText(context, "Audio unavailable", Toast.LENGTH_SHORT).show()
            }
        }

        checkBtn.setOnClickListener {
            if (!answered) {
                val question = questions[index]
                val isCorrect = if (question.type == QuestionType.SENTENCE_ORDER) {
                    built.joinToString("") == question.answer
                } else {
                    selected == question.answer
                }
                answered = true
                if (isCorrect) score++

                question.wordKey?.let { key ->
                    val mistakes = prefs.getStringSet("review_words", emptySet())?.toMutableSet() ?: mutableSetOf()
                    if (isCorrect) mistakes.remove(key) else mistakes.add(key)
                    prefs.edit().putStringSet("review_words", mistakes).apply()
                }
                feedback.text = if (isCorrect) "Correct!" else "Correct answer: ${question.solution}"
                feedback.setTextColor(if (isCorrect) C_SUCCESS() else C_ACCENT())
                checkBtn.text = if (index == questions.lastIndex) "See results" else "Continue"
            } else if (index == questions.lastIndex) {
                showPracticeResults(mode, score, questions.size)
            } else {
                index++
                renderQuestion()
            }
        }

        renderQuestion = {
            selected = null
            answered = false
            built.clear()
            feedback.text = ""
            tokenAnswer.text = ""
            checkBtn.text = "Check"
            checkBtn.isEnabled = false
            optionsBox.removeAllViews()

            val question = questions[index]
            counter.text = "QUESTION ${index + 1} OF ${questions.size}"
            progress.progress = index * 100 / questions.size
            prompt.text = question.prompt
            prompt.textSize = when (question.type) {
                QuestionType.HANZI, QuestionType.FILL_BLANK, QuestionType.SENTENCE_ORDER, QuestionType.TRUE_FALSE, QuestionType.SENTENCE_TRANSLATION -> 24f
                QuestionType.LISTENING, QuestionType.SENTENCE_LISTENING -> 20f
                else -> 42f
            }
            instruction.text = question.instruction
            audioBtn.visibility = if (question.speech != null) View.VISIBLE else View.GONE
            tokenAnswer.visibility = if (question.type == QuestionType.SENTENCE_ORDER) View.VISIBLE else View.GONE

            if (question.type == QuestionType.SENTENCE_ORDER) {
                for (token in question.tokens) {
                    val tokenBtn = Button(context)
                    tokenBtn.text = token
                    tokenBtn.textSize = 22f
                    tokenBtn.setTextColor(C_TEXT())
                    tokenBtn.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
                    tokenBtn.isAllCaps = false
                    tokenBtn.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 52)
                    ).apply { setMargins(0, 0, 0, dp(context, 10)) }
                    tokenBtn.setOnClickListener {
                        if (answered || !tokenBtn.isEnabled) return@setOnClickListener
                        built.add(token)
                        tokenAnswer.text = built.joinToString("")
                        tokenBtn.isEnabled = false
                        tokenBtn.alpha = 0.35f
                        checkBtn.isEnabled = built.size == question.requiredTokens
                    }
                    optionsBox.addView(tokenBtn)
                }
                val resetBtn = Button(context)
                resetBtn.text = "Reset sentence"
                resetBtn.setTextColor(C_ACCENT())
                resetBtn.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
                resetBtn.isAllCaps = false
                resetBtn.setOnClickListener { renderQuestion() }
                optionsBox.addView(resetBtn)
            } else {
                for (option in question.options) {
                    val optionBtn = Button(context)
                    optionBtn.text = option
                    optionBtn.textSize = when (question.type) {
                        QuestionType.HANZI -> 28f
                        QuestionType.FILL_BLANK -> 22f
                        QuestionType.TRUE_FALSE -> 18f
                        QuestionType.SENTENCE_TRANSLATION, QuestionType.SENTENCE_LISTENING -> 14f
                        else -> 16f
                    }
                    optionBtn.setTextColor(C_TEXT())
                    optionBtn.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
                    optionBtn.isAllCaps = false
                    optionBtn.gravity = Gravity.CENTER
                    optionBtn.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 56)
                    ).apply { setMargins(0, 0, 0, dp(context, 10)) }
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
                }
            }
        }

        content.addView(checkBtn)
        scroll.addView(content)
        root.addView(scroll)
        container.addView(root)
        renderQuestion()
    }

    private fun openMatchingPractice(mode: String) {
        currentTab = -1
        container.removeAllViews()
        val context = this
        val isWords = mode == "match_words"
        val available = HskData.allWords().filter { it.hskLevel <= 2 }
        val boards = List(if (isWords) 5 else 3) {
            if (isWords) PracticeData.wordMatchingBoard(available) else PracticeData.sentenceMatchingBoard()
        }
        val totalPairs = boards.sumOf { it.size }

        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(C_BG())
        val scroll = ScrollView(context)
        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 24))

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.gravity = Gravity.CENTER_VERTICAL
        top.addView(backButton(context) { switchTab(TAB_PRACTICE) })
        top.addView(tv(context, if (isWords) "Match Words" else "Match Sentences", 20f, C_TEXT(), true).apply {
            setPadding(dp(context, 10), 0, 0, 0)
        })
        content.addView(top)

        val counter = tv(context, "", 12f, C_MUTED(), true)
        counter.setPadding(0, dp(context, 16), 0, dp(context, 6))
        content.addView(counter)
        val progress = progressBar(context, 0)
        content.addView(progress)
        content.addView(tv(context, "Tap one item from each column to match the pairs. First-attempt matches earn rewards.", 14f, C_MUTED(), false).apply {
            setPadding(0, dp(context, 16), 0, dp(context, 16))
        })

        val pairsBox = LinearLayout(context)
        pairsBox.orientation = LinearLayout.VERTICAL
        content.addView(pairsBox)
        val feedback = tv(context, "", 15f, C_TEXT(), true)
        feedback.setPadding(0, dp(context, 12), 0, dp(context, 12))
        content.addView(feedback)

        var boardIndex = 0
        var score = 0
        lateinit var renderBoard: () -> Unit
        val nextBtn = primaryButton(context, "Next board") {}
        nextBtn.isEnabled = false
        nextBtn.setOnClickListener {
            if (boardIndex == boards.lastIndex) {
                showPracticeResults(mode, score, totalPairs)
            } else {
                boardIndex++
                renderBoard()
            }
        }

        renderBoard = {
            pairsBox.removeAllViews()
            feedback.text = ""
            nextBtn.isEnabled = false
            nextBtn.text = if (boardIndex == boards.lastIndex) "See results" else "Next board"
            counter.text = "BOARD ${boardIndex + 1} OF ${boards.size}"
            progress.progress = boardIndex * 100 / boards.size

            val pairs = boards[boardIndex]
            val right = pairs.withIndex().shuffled()
            val leftButtons = mutableListOf<Button>()
            val rightButtons = mutableListOf<Button>()
            val matched = mutableSetOf<Int>()
            val failed = mutableSetOf<Int>()
            var selectedLeft: Int? = null
            var selectedRight: Int? = null

            fun refreshStyles() {
                for (i in pairs.indices) {
                    val leftActive = selectedLeft == i
                    leftButtons[i].background = roundedBg(context,
                        when {
                            matched.contains(i) -> C_SUCCESS()
                            leftActive -> C_ACCENT()
                            else -> C_CARD()
                        },
                        if (matched.contains(i) || leftActive) C_ACCENT() else C_BORDER(), 12)
                    leftButtons[i].setTextColor(if (matched.contains(i) || leftActive) Color.WHITE else C_TEXT())
                    val rightIndex = right[i].index
                    val rightActive = selectedRight == i
                    rightButtons[i].background = roundedBg(context,
                        when {
                            matched.contains(rightIndex) -> C_SUCCESS()
                            rightActive -> C_ACCENT()
                            else -> C_CARD()
                        },
                        if (matched.contains(rightIndex) || rightActive) C_ACCENT() else C_BORDER(), 12)
                    rightButtons[i].setTextColor(if (matched.contains(rightIndex) || rightActive) Color.WHITE else C_TEXT())
                }
            }

            fun tryMatch() {
                val left = selectedLeft ?: return
                val rightPosition = selectedRight ?: return
                val rightIndex = right[rightPosition].index
                if (left == rightIndex) {
                    matched.add(left)
                    if (!failed.contains(left)) score++
                    leftButtons[left].isEnabled = false
                    rightButtons[rightPosition].isEnabled = false
                    feedback.text = if (matched.size == pairs.size) "Board complete!" else "Matched!"
                    if (matched.size == pairs.size) nextBtn.isEnabled = true
                } else {
                    failed.add(left)
                    feedback.text = "Not a match — try again"
                    if (isWords) {
                        val mistakes = prefs.getStringSet("review_words", emptySet())?.toMutableSet() ?: mutableSetOf()
                        mistakes.add(pairs[left].first)
                        prefs.edit().putStringSet("review_words", mistakes).apply()
                    }
                }
                selectedLeft = null
                selectedRight = null
                refreshStyles()
            }

            for (rowIndex in pairs.indices) {
                val row = LinearLayout(context)
                row.orientation = LinearLayout.HORIZONTAL
                row.setPadding(0, 0, 0, dp(context, 10))
                val height = dp(context, if (isWords) 72 else 92)
                val leftBtn = Button(context)
                leftBtn.text = pairs[rowIndex].first
                leftBtn.textSize = if (isWords) 24f else 16f
                leftBtn.maxLines = 3
                leftBtn.gravity = Gravity.CENTER
                leftBtn.isAllCaps = false
                leftBtn.layoutParams = LinearLayout.LayoutParams(0, height, 1f)
                val rightBtn = Button(context)
                rightBtn.text = right[rowIndex].value.second
                rightBtn.textSize = if (isWords) 14f else 13f
                rightBtn.maxLines = 3
                rightBtn.gravity = Gravity.CENTER
                rightBtn.isAllCaps = false
                val rightParams = LinearLayout.LayoutParams(0, height, 1f)
                rightParams.setMargins(dp(context, 10), 0, 0, 0)
                rightBtn.layoutParams = rightParams

                leftBtn.setOnClickListener {
                    if (!matched.contains(rowIndex)) {
                        selectedLeft = rowIndex
                        refreshStyles()
                        tryMatch()
                    }
                }
                rightBtn.setOnClickListener {
                    if (!matched.contains(right[rowIndex].index)) {
                        selectedRight = rowIndex
                        refreshStyles()
                        tryMatch()
                    }
                }
                leftButtons.add(leftBtn)
                rightButtons.add(rightBtn)
                row.addView(leftBtn)
                row.addView(rightBtn)
                pairsBox.addView(row)
            }
            refreshStyles()
        }

        content.addView(nextBtn)
        scroll.addView(content)
        root.addView(scroll)
        container.addView(root)
        renderBoard()
    }

    private fun openWritingPractice() {
        val words = HskData.allWords().filter { it.hskLevel <= 2 && it.hanzi.length == 1 }
            .distinctBy { it.hanzi }.shuffled().take(10)
        if (words.isEmpty()) return
        currentTab = -1
        container.removeAllViews()
        val context = this

        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(C_BG())
        val scroll = ScrollView(context)
        val content = LinearLayout(context)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 24))

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.gravity = Gravity.CENTER_VERTICAL
        top.addView(backButton(context) { switchTab(TAB_PRACTICE) })
        top.addView(tv(context, "Hanzi Writing", 20f, C_TEXT(), true).apply {
            setPadding(dp(context, 10), 0, 0, 0)
        })
        content.addView(top)

        val counter = tv(context, "", 12f, C_MUTED(), true)
        counter.setPadding(0, dp(context, 16), 0, dp(context, 6))
        content.addView(counter)
        val progress = progressBar(context, 0)
        content.addView(progress)
        content.addView(tv(context, "Trace with your finger. Guide coverage and stray ink are scored.", 14f, C_MUTED(), false).apply {
            setPadding(0, dp(context, 12), 0, dp(context, 8))
        })

        val wordLabel = tv(context, "", 20f, C_TEXT(), true)
        wordLabel.gravity = Gravity.CENTER
        content.addView(wordLabel)
        val audioBtn = TextView(context)
        audioBtn.text = "🔊 Play"
        audioBtn.textSize = 14f
        audioBtn.setTextColor(C_ACCENT())
        audioBtn.gravity = Gravity.CENTER
        audioBtn.setPadding(dp(context, 8), dp(context, 8), dp(context, 8), dp(context, 8))
        content.addView(audioBtn)

        val pad = WritingPadView(context)
        pad.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
        content.addView(pad)

        val feedback = tv(context, "", 15f, C_TEXT(), true)
        feedback.gravity = Gravity.CENTER
        feedback.setPadding(0, dp(context, 12), 0, dp(context, 12))
        content.addView(feedback)

        val controls = LinearLayout(context)
        controls.orientation = LinearLayout.HORIZONTAL
        val guideBtn = Button(context)
        val undoBtn = Button(context)
        val clearBtn = Button(context)
        val controlButtons = listOf(guideBtn, undoBtn, clearBtn)
        for ((index, button) in controlButtons.withIndex()) {
            button.text = listOf("Hide guide", "Undo", "Clear")[index]
            button.textSize = 14f
            button.setTextColor(C_ACCENT())
            button.background = roundedBg(context, C_CARD(), C_BORDER(), 12)
            button.isAllCaps = false
            button.layoutParams = LinearLayout.LayoutParams(0, dp(context, 52), 1f).apply {
                setMargins(dp(context, 4), 0, dp(context, 4), 0)
            }
            controls.addView(button)
        }
        content.addView(controls)

        var index = 0
        var score = 0
        var checked = false
        var guideVisible = true
        lateinit var renderCharacter: () -> Unit
        val checkBtn = primaryButton(context, "Check trace") {}

        audioBtn.setOnClickListener {
            if (::tts.isInitialized) {
                tts.speak(words[index].hanzi, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        guideBtn.setOnClickListener {
            guideVisible = !guideVisible
            pad.setGuideVisible(guideVisible)
            guideBtn.text = if (guideVisible) "Hide guide" else "Show guide"
        }
        undoBtn.setOnClickListener { pad.undo() }
        clearBtn.setOnClickListener { pad.clear() }

        checkBtn.setOnClickListener {
            if (!checked) {
                val trace = pad.traceScore()
                val passed = trace >= WritingEvaluator.PASS_SCORE
                if (passed) score++
                feedback.text = if (passed) {
                    "Good trace · $trace%"
                } else {
                    "Keep practicing · $trace% · need ${WritingEvaluator.PASS_SCORE}%"
                }
                feedback.setTextColor(if (passed) C_SUCCESS() else C_ACCENT())
                val key = words[index].hanzi
                val mistakes = prefs.getStringSet("review_words", emptySet())?.toMutableSet() ?: mutableSetOf()
                if (passed) mistakes.remove(key) else mistakes.add(key)
                prefs.edit().putStringSet("review_words", mistakes).apply()
                checked = true
                pad.isEnabled = false
                undoBtn.isEnabled = false
                clearBtn.isEnabled = false
                checkBtn.text = if (index == words.lastIndex) "See results" else "Next character"
            } else if (index == words.lastIndex) {
                showPracticeResults("writing", score, words.size)
            } else {
                index++
                renderCharacter()
            }
        }

        renderCharacter = {
            checked = false
            guideVisible = true
            pad.isEnabled = true
            undoBtn.isEnabled = true
            clearBtn.isEnabled = true
            pad.setCharacter(words[index].hanzi)
            pad.setGuideVisible(true)
            pad.contentDescription = "Trace ${words[index].hanzi}"
            guideBtn.text = "Hide guide"
            wordLabel.text = "${words[index].pinyin} · ${words[index].meaning}"
            counter.text = "CHARACTER ${index + 1} OF ${words.size}"
            progress.progress = index * 100 / words.size
            feedback.text = ""
            checkBtn.text = "Check trace"
        }

        content.addView(checkBtn)
        scroll.addView(content)
        root.addView(scroll)
        container.addView(root)
        renderCharacter()
    }

    private fun showPracticeResults(mode: String, score: Int, total: Int) {
        val previouslyUnlocked = PracticeData.achievements(currentStats()).filter { it.unlocked }.map { it.id }.toSet()
        val (earned, coinsEarned) = PracticeData.rewards(mode, score, total)
        prefs.edit()
            .putInt("xp_total", prefs.getInt("xp_total", 0) + earned)
            .putInt("coins_total", prefs.getInt("coins_total", 0) + coinsEarned)
            .putInt("practice_correct_total", prefs.getInt("practice_correct_total", 0) + score)
            .putInt("perfect_sessions", prefs.getInt("perfect_sessions", 0) + if (score == total) 1 else 0)
            .apply()
        val newAchievements = PracticeData.achievements(currentStats()).filter {
            it.unlocked && it.id !in previouslyUnlocked
        }

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
        })
        resultCard.addView(tv(context, "+$coinsEarned coins", 24f, C_GOLD(), true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(context, 4), 0, dp(context, 16))
        })
        if (newAchievements.isNotEmpty()) {
            resultCard.addView(tv(context, "Achievement unlocked:\n${newAchievements.joinToString("\n") { it.title }}", 15f, C_SUCCESS(), true).apply {
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, dp(context, 16))
            })
        }
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
        try {
            avatar.typeface = Typeface.createFromAsset(context.assets, "fonts/Alibaba-PuHuiTi-Heavy.ttf")
        } catch (_: Exception) {
            // Font unavailable on this device; fall back to system typeface
        }
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
        row.addView(statBox(context, prefs.getInt("xp_total", 0).toString(), "XP"))
        row.addView(statBox(context, prefs.getInt("coins_total", 0).toString(), "Coins"))

        statsCard.addView(row)
        statsCard.addView(tv(context, "$doneLessons of $totalLessons lessons complete", 12f, C_MUTED(), false).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(context, 10), 0, 0)
        })
        content.addView(statsCard)

        val achievementsCard = card(context)
        achievementsCard.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, dp(context, 16), 0, 0) }
        achievementsCard.addView(tv(context, "Achievements", 16f, C_TEXT(), true).apply {
            setPadding(0, 0, 0, dp(context, 12))
        })
        for (achievement in PracticeData.achievements(currentStats())) {
            val achievementRow = LinearLayout(context)
            achievementRow.orientation = LinearLayout.HORIZONTAL
            achievementRow.gravity = Gravity.CENTER_VERTICAL
            achievementRow.setPadding(0, dp(context, 8), 0, dp(context, 8))
            val stateColor = if (achievement.unlocked) C_SUCCESS() else C_MUTED()
            achievementRow.addView(tv(context, if (achievement.unlocked) "✓" else "○", 22f, stateColor, true).apply {
                layoutParams = ViewGroup.LayoutParams(dp(context, 36), dp(context, 40))
                gravity = Gravity.CENTER
            })
            val textColumn = LinearLayout(context)
            textColumn.orientation = LinearLayout.VERTICAL
            textColumn.addView(tv(context, achievement.title, 15f, if (achievement.unlocked) C_TEXT() else C_MUTED(), true))
            textColumn.addView(tv(context, "${achievement.description} · ${achievement.progress.coerceAtMost(achievement.target)}/${achievement.target}", 12f, C_MUTED(), false))
            achievementRow.addView(textColumn, wlp(context))
            achievementsCard.addView(achievementRow)
        }
        content.addView(achievementsCard)

        // Settings card
        val settingsCard = card(context)
        settingsCard.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, dp(context, 16), 0, 0) }
        settingsCard.setPadding(dp(context, 16), dp(context, 24), dp(context, 16), dp(context, 12))

        val sTitle = tv(context, "Settings", 16f, C_TEXT(), true)
        sTitle.setPadding(0, 0, 0, dp(context, 12))
        settingsCard.addView(sTitle)

        // Dark mode switch
        val darkRow = LinearLayout(context)
        darkRow.orientation = LinearLayout.HORIZONTAL
        darkRow.gravity = Gravity.CENTER_VERTICAL
        darkRow.minimumHeight = dp(context, 56)
        darkRow.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        darkRow.setPadding(0, 0, 0, dp(context, 12))

        val darkLabel = tv(context, "Dark mode", 15f, C_TEXT(), false)
        darkLabel.gravity = Gravity.CENTER_VERTICAL
        darkRow.addView(darkLabel, wlp(context))

        val darkSwitch = android.widget.Switch(context)
        darkSwitch.showText = false
        darkSwitch.isChecked = isDarkMode()
        darkSwitch.contentDescription = "Dark mode"
        darkSwitch.gravity = Gravity.CENTER
        darkSwitch.layoutParams = ViewGroup.LayoutParams(dp(context, 64), dp(context, 48))
        val switchStates = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            darkSwitch.thumbTintList = ColorStateList(switchStates, intArrayOf(C_ACCENT(), C_MUTED()))
            darkSwitch.trackTintList = ColorStateList(switchStates, intArrayOf(C_GOLD(), C_BORDER()))
        }
        darkSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked != isDarkMode()) setDarkMode(checked)
        }
        darkRow.addView(darkSwitch)

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
