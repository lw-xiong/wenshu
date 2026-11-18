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

    private val TAB_HOME = 0
    private val TAB_LEARN = 1
    private val TAB_VOCAB = 2
    private val TAB_REVIEW = 3
    private val TAB_PROFILE = 4

    private val tabLabels = listOf("Home", "Learn", "Vocabulary", "Review", "Profile")

    // Design tokens
    private val C_BG = Color.parseColor("#F8F6F2")
    private val C_CARD = Color.parseColor("#FFFFFF")
    private val C_TEXT = Color.parseColor("#171717")
    private val C_MUTED = Color.parseColor("#77736E")
    private val C_ACCENT = Color.parseColor("#C62828")
    private val C_GOLD = Color.parseColor("#D6A84F")
    private val C_SUCCESS = Color.parseColor("#36845B")
    private val C_BORDER = Color.parseColor("#E9E5DF")
    private val C_HANZI = Color.parseColor("#171717")
    private val C_PINYIN = Color.parseColor("#77736E")
    private val C_LOCKED = Color.parseColor("#C9C4BD")
    private val C_DONE_BG = Color.parseColor("#F1F7EE")
    private val C_DONE_TEXT = Color.parseColor("#36845B")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("hsk_app", Context.MODE_PRIVATE)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(C_BG)

        container = FrameLayout(this)
        container.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.addView(container)

        navBar = buildNavBar()
        root.addView(navBar)

        setContentView(root)

        switchTab(TAB_HOME)
    }

    private fun dp(context: Context, d: Int): Int {
        return (d * context.resources.displayMetrics.density).toInt()
    }

    /* ---------- Bottom navigation ---------- */

    private fun buildNavBar(): LinearLayout {
        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(this, 56)
        )
        bar.setBackgroundColor(C_CARD)
        bar.setPadding(dp(this, 8), dp(this, 8), dp(this, 8), dp(this, 8))

        for (i in tabLabels.indices) {
            val item = LinearLayout(this)
            item.orientation = LinearLayout.VERTICAL
            val ip = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            ip.weight = 1f
            item.layoutParams = ip
            item.gravity = Gravity.CENTER
            item.setPadding(dp(this, 4), dp(this, 4), dp(this, 4), dp(this, 4))
            item.tag = i

            val icon = TextView(this)
            icon.text = tabLabels[i].first().toString().uppercase()
            icon.textSize = 18f
            icon.setTypeface(icon.typeface, Typeface.BOLD)
            icon.setTextColor(C_MUTED)

            val label = TextView(this)
            label.text = tabLabels[i]
            label.textSize = 11f
            label.setTypeface(label.typeface, Typeface.BOLD)
            label.setTextColor(C_MUTED)

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
            val c = if (active) C_ACCENT else C_MUTED
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
            TAB_REVIEW -> container.addView(buildReviewScreen(this))
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

    private fun card(context: Context): LinearLayout {
        val c = LinearLayout(context)
        c.orientation = LinearLayout.VERTICAL
        c.setBackgroundResource(R.drawable.card_bg)
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
        pb.setProgressTintList(ColorStateList.valueOf(C_ACCENT))
        pb.setProgressBackgroundTintList(ColorStateList.valueOf(C_BORDER))
        return pb
    }

    private fun backButton(context: Context, onClick: () -> Unit): TextView {
        val b = TextView(context)
        b.text = "←"
        b.textSize = 24f
        b.setTextColor(C_MUTED)
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
        b.setBackgroundResource(R.drawable.btn_primary)
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
        btnNext.isEnabled = currentIdx < totalWords - 1
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
        root.setBackgroundColor(C_BG)
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
        content.addView(tv(context, "Welcome back.", 24f, C_TEXT, true))

        val sub = tv(context, "Keep building your Mandarin.", 14f, C_MUTED, false)
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

        val pTitle = tv(context, "Your progress", 16f, C_TEXT, true)
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
        progressCard.addView(row1)

        progressCard.addView(progressBar(context, if (totalLessons > 0) (doneLessons * 100 / totalLessons) else 0))
        content.addView(progressCard)

        // Continue learning
        val contCard = card(context)
        contCard.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val cTitle = tv(context, "Continue learning", 16f, C_TEXT, true)
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
        content.addView(tv(context, "HSK courses", 16f, C_TEXT, true).apply {
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

        col.addView(tv(context, value, 20f, C_ACCENT, true))
        val l = tv(context, label, 11f, C_MUTED, true)
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
        badge.setBackgroundResource(R.drawable.btn_primary)
        badge.layoutParams = ViewGroup.LayoutParams(dp(context, 32), dp(context, 32))
        badge.gravity = Gravity.CENTER
        header.addView(badge)

        val info = LinearLayout(context)
        info.orientation = LinearLayout.VERTICAL
        val ip = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        ip.weight = 1f
        info.layoutParams = ip
        info.setPadding(dp(context, 10), 0, 0, 0)

        info.addView(tv(context, mod.title, 17f, C_TEXT, true))
        info.addView(tv(context, mod.subtitle, 13f, C_MUTED, false))

        header.addView(info)
        card.addView(header)

        val done = countDone(mod.level)
        val total = mod.lessons.size
        card.addView(tv(context, "$done / $total lessons", 12f, C_MUTED, false).apply {
            setPadding(0, dp(context, 6), 0, 0)
        })

        card.addView(progressBar(context, if (total > 0) (done * 100 / total) else 0))

        if (mod.level > 2) {
            card.addView(tv(context, "Coming soon", 12f, C_MUTED, false).apply {
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
        root.setBackgroundColor(C_BG)

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

        val title = tv(context, mod.title, 20f, C_TEXT, true)
        title.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setPadding(dp(context, 10), 0, 0, 0)
        title.gravity = Gravity.CENTER_VERTICAL
        top.addView(title)

        content.addView(top)

        content.addView(tv(context, mod.subtitle, 13f, C_MUTED, false).apply {
            setPadding(0, 0, 0, dp(context, 16))
        })

        if (selectedLevel > 2) {
            content.addView(tv(context, "Coming soon", 20f, C_ACCENT, true).apply {
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

        header.addView(tv(context, "Lesson $num", 12f, C_ACCENT, true))

        val state = tv(
            context,
            if (done) "Completed" else "Available",
            11f,
            if (done) C_SUCCESS else C_MUTED,
            true
        )
        state.gravity = Gravity.END
        state.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        header.addView(state)
        card.addView(header)

        card.addView(tv(context, lesson.title, 17f, C_TEXT, true).apply {
            setPadding(0, dp(context, 6), 0, dp(context, 2))
        })
        card.addView(tv(context, lesson.sub, 13f, C_MUTED, false))

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
        root.setBackgroundColor(C_BG)

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

        val title = tv(context, "${mod.title} · Lesson ${lessonIndex + 1}", 18f, C_TEXT, true)
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

        content.addView(tv(context, "Progress", 11f, C_MUTED, true).apply {
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
        cardStage.setBackgroundResource(R.drawable.card_bg)

        val hanzi = tv(context, lesson.words[currentIdx].hanzi, 64f, C_HANZI, true)
        hanzi.gravity = Gravity.CENTER
        cardStage.addView(hanzi)

        val pinyin = tv(context, lesson.words[currentIdx].pinyin, 18f, C_PINYIN, false)
        pinyin.gravity = Gravity.CENTER
        pinyin.setPadding(0, dp(context, 4), 0, 0)
        cardStage.addView(pinyin)

        val hint = tv(context, "Tap card to reveal meaning", 11f, C_MUTED, false)
        hint.gravity = Gravity.CENTER
        hint.setPadding(0, dp(context, 8), 0, dp(context, 10))
        cardStage.addView(hint)

        val meaning = tv(
            context,
            lesson.words[currentIdx].meaning,
            16f,
            C_ACCENT,
            true
        )
        meaning.gravity = Gravity.CENTER
        meaning.setPadding(0, 0, 0, dp(context, 10))
        meaning.visibility = View.GONE
        cardStage.addView(meaning)

        cardStage.setOnClickListener {
            val anim = android.animation.ObjectAnimator.ofFloat(cardStage, "rotationY", 0f, 180f)
            anim.duration = 400
            var swapped = false
            anim.addUpdateListener { animation ->
                if (animation.animatedFraction >= 0.5f && !swapped) {
                    swapped = true
                    revealed = !revealed
                    meaning.visibility = if (revealed) View.VISIBLE else View.GONE
                    hint.visibility = if (revealed) View.GONE else View.VISIBLE
                }
            }
            anim.start()
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
        btnNext.setBackgroundResource(R.drawable.btn_primary)
        btnNext.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        btnNext.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 14))
        btnNext.isAllCaps = false

        btnPrev.text = "Previous"
        btnPrev.textSize = 14f
        btnPrev.setTextColor(C_ACCENT)
        btnPrev.setBackgroundResource(R.drawable.btn_ghost)
        btnPrev.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        btnPrev.setPadding(dp(context, 14), dp(context, 14), dp(context, 14), dp(context, 14))
        btnPrev.gravity = Gravity.CENTER
        btnPrev.isEnabled = currentIdx > 0

        // Shared navigation logic
        fun goTo(index: Int) {
            revealed = false
            meaning.visibility = View.GONE
            hint.visibility = View.VISIBLE
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
        btnShuffle.setTextColor(C_ACCENT)
        btnShuffle.setBackgroundResource(R.drawable.btn_ghost)
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
        root.setBackgroundColor(C_BG)

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

        val title = tv(context, "Vocabulary", 20f, C_TEXT, true)
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
        search.setHintTextColor(C_MUTED)
        search.setTextColor(C_TEXT)
        search.textSize = 15f
        search.setPadding(dp(context, 14), dp(context, 12), dp(context, 14), dp(context, 12))
        search.setBackgroundResource(R.drawable.input_bg)
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
                val empty = tv(context, "No words found", 15f, C_MUTED, false)
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

        row.addView(tv(context, w.hanzi, 22f, C_TEXT, true))
        row.addView(tv(context, w.pinyin, 15f, C_PINYIN, false))
        row.addView(tv(context, w.meaning, 15f, C_TEXT, false))
        row.addView(tv(context, "HSK ${w.hskLevel}", 10f, C_ACCENT, true).apply {
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

    private fun buildReviewScreen(context: Context): View {
        val root = LinearLayout(context)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(C_BG)

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

        val title = tv(context, "Review", 20f, C_TEXT, true)
        title.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setPadding(dp(context, 10), 0, 0, 0)
        title.gravity = Gravity.CENTER_VERTICAL
        top.addView(title)

        content.addView(top)

        content.addView(tv(context, "Words marked for review", 13f, C_MUTED, false).apply {
            setPadding(0, 0, 0, dp(context, 16))
        })

        val marked = prefs.getStringSet("review_words", emptySet()) ?: emptySet<String>()

        if (marked.isEmpty()) {
            content.addView(tv(context, "No words marked for review yet.", 15f, C_MUTED, false).apply {
                setPadding(0, dp(context, 40), 0, 0)
                gravity = Gravity.CENTER
            })
        } else {
            for (hanzi in marked) {
                var found: HskData.WordEntry? = null
                for (w in HskData.allWords()) {
                    if (w.hanzi == hanzi) { found = w; break }
                }
                if (found != null) content.addView(reviewRow(context, found))
            }
        }

        content.addView(View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 100))
        })

        scroll.addView(content)
        root.addView(scroll)
        return root
    }

    private fun reviewRow(context: Context, w: HskData.WordEntry): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = LinearLayout.VERTICAL
        row.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        row.setPadding(dp(context, 12), dp(context, 10), dp(context, 12), dp(context, 10))

        row.addView(tv(context, w.hanzi, 22f, C_TEXT, true))
        row.addView(tv(context, w.pinyin, 15f, C_PINYIN, false))
        row.addView(tv(context, w.meaning, 15f, C_TEXT, false))

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
        root.setBackgroundColor(C_BG)

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
        avatar.text = "文"
        avatar.textSize = 32f
        avatar.setTypeface(avatar.typeface, Typeface.BOLD)
        avatar.setTextColor(Color.parseColor("#0A0A0A"))
        avatar.setBackgroundResource(R.drawable.btn_primary)
        avatar.layoutParams = ViewGroup.LayoutParams(dp(context, 64), dp(context, 64))
        avatar.gravity = Gravity.CENTER

        val avatarWrap = LinearLayout(context)
        avatarWrap.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        avatarWrap.gravity = Gravity.CENTER
        avatarWrap.addView(avatar)
        content.addView(avatarWrap)

        content.addView(tv(context, "Learner", 20f, C_TEXT, true).apply {
            setPadding(0, dp(context, 8), 0, dp(context, 2))
            gravity = Gravity.CENTER
        })

        content.addView(tv(context, "HSK 2 · 300 words", 13f, C_MUTED, false).apply {
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

        val sTitle = tv(context, "Your stats", 16f, C_TEXT, true)
        sTitle.setPadding(0, 0, 0, dp(context, 12))
        statsCard.addView(sTitle)

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

        col.addView(tv(context, value, 20f, C_ACCENT, true))

        val l = tv(context, label, 11f, C_MUTED, true)
        l.setPadding(dp(context, 4), 0, 0, 0)
        col.addView(l)
        return col
    }

    private fun dividerLine(context: Context): View {
        val v = View(context)
        v.layoutParams = ViewGroup.LayoutParams(1, dp(context, 40))
        v.setBackgroundColor(C_BORDER)
        return v
    }
}
