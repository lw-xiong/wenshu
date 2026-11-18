package com.ciel.hsk

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
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
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var container: FrameLayout
    private lateinit var navBar: LinearLayout
    private var currentTab: Int = 0

    private val TAB_HOME = 0
    private val TAB_LEARN = 1
    private val TAB_VOCAB = 2
    private val TAB_REVIEW = 3
    private val TAB_PROFILE = 4

    private val tabLabels = listOf("Home", "Learn", "Vocabulary", "Review", "Profile")
    private val tabIcons = listOf("◆", "▩", "▤", "↻", "◯")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("hsk_app", Context.MODE_PRIVATE)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        root.setBackgroundColor(Color.parseColor("#F8F6F2"))

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

    /* ---------- Bottom navigation ---------- */

    private fun buildNavBar(): LinearLayout {
        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        bar.setBackgroundColor(Color.parseColor("#FFFFFF"))
        bar.setPadding(8, 8, 8, 8)

        for (i in tabLabels.indices) {
            val item = LinearLayout(this)
            item.orientation = LinearLayout.VERTICAL
            val ip = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            ip.weight = 1f
            item.layoutParams = ip
            item.gravity = Gravity.CENTER
            item.setPadding(4, 4, 4, 4)
            item.tag = i

            val icon = TextView(this)
            icon.text = tabIcons[i]
            icon.textSize = 20f
            icon.setTextColor(Color.parseColor("#77736E"))

            val label = TextView(this)
            label.text = tabLabels[i]
            label.textSize = 11f
            label.setTypeface(label.typeface, Typeface.BOLD)
            label.setTextColor(Color.parseColor("#77736E"))

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
            icon.setTextColor(if (active) Color.parseColor("#C62828") else Color.parseColor("#77736E"))
            label.setTextColor(if (active) Color.parseColor("#C62828") else Color.parseColor("#77736E"))
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
        c.setBackgroundColor(Color.parseColor("#FFFFFF"))
        c.setPadding(16, 16, 16, 16)
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

    private fun progressBar(context: Context, pct: Int): LinearLayout {
        val wrap = LinearLayout(context)
        wrap.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 6
        )

        val track = View(context)
        track.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 6
        )
        track.setBackgroundColor(Color.parseColor("#E9E5DF"))

        val fill = View(context)
        val width = (pct * context.resources.displayMetrics.widthPixels / 100).coerceIn(0, context.resources.displayMetrics.widthPixels)
        fill.layoutParams = ViewGroup.LayoutParams(width, 6)
        fill.setBackgroundColor(Color.parseColor("#C62828"))

        wrap.addView(track)
        wrap.addView(fill)
        return wrap
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
        root.setBackgroundColor(Color.parseColor("#F8F6F2"))
        root.setPadding(16, 0, 16, 0)

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
        content.setPadding(0, 16, 0, 80)

        content.addView(tv(context, "Hi there.", 22f, Color.parseColor("#171717"), true))

        val sub = tv(context, "Ready to continue with Mandarin?", 14f, Color.parseColor("#77736E"), false)
        sub.setPadding(0, 4, 0, 20)
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

        val pTitle = tv(context, "Your progress", 16f, Color.parseColor("#171717"), true)
        pTitle.setPadding(0, 0, 0, 12)
        progressCard.addView(pTitle)

        val row1 = LinearLayout(context)
        row1.orientation = LinearLayout.HORIZONTAL
        row1.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        row1.setPadding(0, 0, 0, 16)

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

        val cTitle = tv(context, "Continue learning", 16f, Color.parseColor("#171717"), true)
        cTitle.setPadding(0, 0, 0, 12)
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

        val nextBtn = Button(context)
        nextBtn.text = if (nextLesson != null) "${nextLesson.title}  ·  HSK $nextLevel" else "All lessons complete"
        nextBtn.textSize = 16f
        nextBtn.setTextColor(Color.parseColor("#FFFFFF"))
        nextBtn.setBackgroundColor(Color.parseColor("#C62828"))
        nextBtn.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        nextBtn.setPadding(12, 14, 12, 14)
        nextBtn.setOnClickListener {
            if (nextLesson != null) openLesson(nextLevel, HskData.modules().first { it.level == nextLevel }.lessons.indexOf(nextLesson))
        }
        contCard.addView(nextBtn)
        content.addView(contCard)

        // HSK course cards
        content.addView(tv(context, "HSK courses", 16f, Color.parseColor("#171717"), true).apply {
            setPadding(0, 24, 0, 12)
        })

        for (mod in HskData.modules()) {
            content.addView(courseCard(context, mod))
        }

        content.addView(View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100)
        })

        scroll.addView(content)
        root.addView(scroll)
        return root
    }

    private fun statColumn(context: Context, value: String, label: String): LinearLayout {
        val col = LinearLayout(context)
        col.orientation = LinearLayout.VERTICAL
        col.layoutParams = ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        col.layoutParams = wlp(context)
        col.gravity = Gravity.CENTER

        col.addView(tv(context, value, 20f, Color.parseColor("#C62828"), true))
        val l = tv(context, label, 11f, Color.parseColor("#77736E"), true)
        l.setPadding(4, 0, 0, 0)
        col.addView(l)
        return col
    }

    private fun courseCard(context: Context, mod: HskModule): LinearLayout {
        val card = card(context)
        card.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        card.setPadding(16, 14, 16, 14)

        val header = LinearLayout(context)
        header.orientation = LinearLayout.HORIZONTAL
        header.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val badge = Button(context)
        badge.text = mod.level.toString()
        badge.textSize = 14f
        badge.setTextColor(Color.parseColor("#FFFFFF"))
        badge.setBackgroundColor(Color.parseColor("#C62828"))
        badge.layoutParams = ViewGroup.LayoutParams(32, 32)
        badge.gravity = Gravity.CENTER
        header.addView(badge)

        val info = LinearLayout(context)
        info.orientation = LinearLayout.VERTICAL
        val ip = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        ip.weight = 1f
        info.layoutParams = ip
        info.setPadding(10, 0, 0, 0)

        info.addView(tv(context, mod.title, 17f, Color.parseColor("#171717"), true))
        info.addView(tv(context, mod.subtitle, 13f, Color.parseColor("#77736E"), false))

        header.addView(info)
        card.addView(header)

        val done = countDone(mod.level)
        val total = mod.lessons.size
        card.addView(tv(context, "$done / $total lessons", 12f, Color.parseColor("#77736E"), false).apply {
            setPadding(0, 6, 0, 0)
        })

        card.addView(progressBar(context, if (total > 0) (done * 100 / total) else 0))

        card.setOnClickListener {
            prefs.edit().putInt("selected_hsk", mod.level).apply()
            switchTab(TAB_LEARN)
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
        root.setBackgroundColor(Color.parseColor("#F8F6F2"))

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
        content.setPadding(16, 16, 0, 80)

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        top.setPadding(0, 0, 0, 16)

        val btnBack = Button(context)
        btnBack.text = "←"
        btnBack.textSize = 20f
        btnBack.setTextColor(Color.parseColor("#77736E"))
        btnBack.setBackgroundColor(Color.parseColor("#FFFFFF"))
        btnBack.layoutParams = ViewGroup.LayoutParams(44, 44)
        btnBack.gravity = Gravity.CENTER
        btnBack.setOnClickListener { switchTab(TAB_HOME) }
        top.addView(btnBack)

        val selectedLevel = prefs.getInt("selected_hsk", 1)
        val mod = HskData.modules().first { it.level == selectedLevel }

        val title = tv(context, mod.title, 20f, Color.parseColor("#171717"), true)
        title.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setPadding(10, 0, 0, 0)
        title.gravity = Gravity.CENTER_VERTICAL
        top.addView(title)

        content.addView(top)

        content.addView(tv(context, mod.subtitle, 13f, Color.parseColor("#77736E"), false).apply {
            setPadding(0, 0, 0, 16)
        })

        for ((index, lesson) in mod.lessons.withIndex()) {
            content.addView(lessonCard(context, mod, index + 1, lesson))
        }

        content.addView(View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100)
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
        if (done) card.setBackgroundColor(Color.parseColor("#F1F7EE"))

        val header = LinearLayout(context)
        header.orientation = LinearLayout.HORIZONTAL
        header.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        header.addView(tv(context, "Lesson $num", 12f, Color.parseColor("#C62828"), true))

        val state = tv(
            context,
            if (done) "Completed" else "Available",
            11f,
            if (done) Color.parseColor("#36845B") else Color.parseColor("#77736E"),
            true
        )
        state.gravity = Gravity.END
        state.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        header.addView(state)
        card.addView(header)

        card.addView(tv(context, lesson.title, 17f, Color.parseColor("#171717"), true).apply {
            setPadding(0, 6, 0, 2)
        })
        card.addView(tv(context, lesson.sub, 13f, Color.parseColor("#77736E"), false))

        card.setOnClickListener { openLesson(mod.level, mod.lessons.indexOf(lesson)) }

        return card
    }

    /* ========================================================================
       LESSON SCREEN — tap-to-reveal flashcard
       ======================================================================== */

    private fun openLesson(hskLevel: Int, lessonIndex: Int) {
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
        root.setBackgroundColor(Color.parseColor("#F8F6F2"))

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
        content.setPadding(16, 16, 16, 80)

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        top.setPadding(0, 0, 0, 16)

        val btnBack = Button(context)
        btnBack.text = "←"
        btnBack.textSize = 20f
        btnBack.setTextColor(Color.parseColor("#77736E"))
        btnBack.setBackgroundColor(Color.parseColor("#FFFFFF"))
        btnBack.layoutParams = ViewGroup.LayoutParams(44, 44)
        btnBack.gravity = Gravity.CENTER
        btnBack.setOnClickListener { switchTab(TAB_LEARN) }
        top.addView(btnBack)

        val title = tv(context, "${mod.title} · Lesson ${lessonIndex + 1}", 18f, Color.parseColor("#171717"), true)
        title.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setPadding(10, 0, 0, 0)
        title.gravity = Gravity.CENTER_VERTICAL
        top.addView(title)

        content.addView(top)

        val totalWords = lesson.words.size
        val progressKey = "card_progress:${lesson.id}"
        var currentIdx = prefs.getInt(progressKey, 0)
        var revealed = false

        content.addView(tv(context, "Progress", 11f, Color.parseColor("#77736E"), true).apply {
            setPadding(0, 0, 0, 6)
        })
        content.addView(progressBar(context, if (totalWords > 0) ((currentIdx + 1) * 100 / totalWords).coerceAtMost(100) else 100))

        val cardStage = LinearLayout(context)
        cardStage.orientation = LinearLayout.VERTICAL
        cardStage.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cardStage.setPadding(0, 20, 0, 16)
        cardStage.gravity = Gravity.CENTER

        val hanzi = tv(context, lesson.words[currentIdx].hanzi, 64f, Color.parseColor("#171717"), true)
        hanzi.gravity = Gravity.CENTER
        cardStage.addView(hanzi)

        val pinyin = tv(context, lesson.words[currentIdx].pinyin, 18f, Color.parseColor("#77736E"), false)
        pinyin.gravity = Gravity.CENTER
        pinyin.setPadding(0, 4, 0, 0)
        cardStage.addView(pinyin)

        val revealBtn = Button(context)
        revealBtn.text = getStringRes(context, R.string.reveal, "Reveal meaning")
        revealBtn.textSize = 14f
        revealBtn.setTextColor(Color.parseColor("#FFFFFF"))
        revealBtn.setBackgroundColor(Color.parseColor("#C62828"))
        revealBtn.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        revealBtn.setPadding(14, 10, 14, 10)

        val meaning = tv(
            context,
            lesson.words[currentIdx].meaning,
            16f,
            Color.parseColor("#C62828"),
            true
        )
        meaning.gravity = Gravity.CENTER
        meaning.setPadding(0, 10, 0, 0)
        meaning.visibility = View.GONE

        revealBtn.setOnClickListener {
            revealed = !revealed
            meaning.visibility = if (revealed) View.VISIBLE else View.GONE
            revealBtn.text = if (revealed) "Hide meaning" else "Reveal meaning"
        }
        cardStage.addView(revealBtn)
        cardStage.addView(meaning)

        content.addView(cardStage)

        val nav = LinearLayout(context)
        nav.orientation = LinearLayout.HORIZONTAL
        nav.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        nav.setPadding(0, 12, 0, 0)

        val btnPrev = Button(context)
        btnPrev.text = getStringRes(context, R.string.previous, "Previous")
        btnPrev.textSize = 14f
        btnPrev.setTextColor(Color.parseColor("#C62828"))
        btnPrev.setBackgroundColor(Color.parseColor("#FFFFFF"))
        btnPrev.layoutParams = wlp(context)
        btnPrev.setPadding(12, 12, 12, 12)
        btnPrev.isEnabled = currentIdx > 0
        btnPrev.setOnClickListener {
            revealed = false
            meaning.visibility = View.GONE
            revealBtn.text = "Reveal meaning"
            currentIdx = (currentIdx - 1 + totalWords) % totalWords
            prefs.edit().putInt(progressKey, currentIdx).apply()
            hanzi.text = lesson.words[currentIdx].hanzi
            pinyin.text = lesson.words[currentIdx].pinyin
            meaning.text = lesson.words[currentIdx].meaning
        }
        nav.addView(btnPrev)

        val btnNext = Button(context)
        btnNext.text = getStringRes(context, R.string.next, "Next")
        btnNext.textSize = 14f
        btnNext.setTextColor(Color.parseColor("#FFFFFF"))
        btnNext.setBackgroundColor(Color.parseColor("#C62828"))
        btnNext.layoutParams = wlp(context)
        btnNext.setPadding(12, 12, 12, 12)
        btnNext.isEnabled = currentIdx < totalWords - 1
        btnNext.setOnClickListener {
            revealed = false
            meaning.visibility = View.GONE
            revealBtn.text = "Reveal meaning"
            currentIdx = (currentIdx + 1) % totalWords
            prefs.edit().putInt(progressKey, currentIdx).apply()
            hanzi.text = lesson.words[currentIdx].hanzi
            pinyin.text = lesson.words[currentIdx].pinyin
            meaning.text = lesson.words[currentIdx].meaning
        }
        nav.addView(btnNext)

        content.addView(nav)

        val btnDone = Button(context)
        btnDone.text = getStringRes(context, R.string.mark_done, "Mark lesson done")
        btnDone.textSize = 14f
        btnDone.setTextColor(Color.parseColor("#FFFFFF"))
        btnDone.setBackgroundColor(if (currentIdx >= totalWords - 1) Color.parseColor("#36845B") else Color.parseColor("#C62828"))
        btnDone.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        btnDone.setPadding(12, 12, 12, 12)
        btnDone.setOnClickListener {
            prefs.edit().putBoolean("done:${lesson.id}", true).apply()
            prefs.edit().putInt(progressKey, totalWords).apply()
            Toast.makeText(context, getStringRes(context, R.string.lesson_complete, "Lesson complete"), Toast.LENGTH_SHORT).show()
            switchTab(TAB_LEARN)
        }
        content.addView(btnDone)

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
        root.setBackgroundColor(Color.parseColor("#F8F6F2"))

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
        content.setPadding(16, 16, 16, 80)

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        top.setPadding(0, 0, 0, 16)

        val btnBack = Button(context)
        btnBack.text = "←"
        btnBack.textSize = 20f
        btnBack.setTextColor(Color.parseColor("#77736E"))
        btnBack.setBackgroundColor(Color.parseColor("#FFFFFF"))
        btnBack.layoutParams = ViewGroup.LayoutParams(44, 44)
        btnBack.gravity = Gravity.CENTER
        btnBack.setOnClickListener { switchTab(TAB_HOME) }
        top.addView(btnBack)

        val title = tv(context, "Vocabulary", 20f, Color.parseColor("#171717"), true)
        title.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setPadding(10, 0, 0, 0)
        title.gravity = Gravity.CENTER_VERTICAL
        top.addView(title)

        content.addView(top)

        val search = EditText(context)
        search.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        search.setHint(getStringRes(context, R.string.search, "Search words"))
        search.setHintTextColor(Color.parseColor("#77736E"))
        search.setTextColor(Color.parseColor("#171717"))
        search.textSize = 15f
        search.setPadding(14, 12, 14, 12)
        search.setBackgroundResource(android.R.drawable.editbox_background)
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
                val empty = tv(context, getStringRes(context, R.string.no_results, "No words found"), 15f, Color.parseColor("#77736E"), false)
                empty.setPadding(0, 20, 0, 0)
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
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100)
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
        row.setPadding(12, 10, 12, 10)

        row.addView(tv(context, w.hanzi, 22f, Color.parseColor("#171717"), true))
        row.addView(tv(context, w.pinyin, 15f, Color.parseColor("#77736E"), false))
        row.addView(tv(context, w.meaning, 15f, Color.parseColor("#171717"), false))
        row.addView(tv(context, "HSK ${w.hskLevel}", 10f, Color.parseColor("#C62828"), true).apply {
            setPadding(0, 4, 0, 0)
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
        root.setBackgroundColor(Color.parseColor("#F8F6F2"))

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
        content.setPadding(16, 16, 16, 80)

        val top = LinearLayout(context)
        top.orientation = LinearLayout.HORIZONTAL
        top.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        top.setPadding(0, 0, 0, 16)

        val btnBack = Button(context)
        btnBack.text = "←"
        btnBack.textSize = 20f
        btnBack.setTextColor(Color.parseColor("#77736E"))
        btnBack.setBackgroundColor(Color.parseColor("#FFFFFF"))
        btnBack.layoutParams = ViewGroup.LayoutParams(44, 44)
        btnBack.gravity = Gravity.CENTER
        btnBack.setOnClickListener { switchTab(TAB_HOME) }
        top.addView(btnBack)

        val title = tv(context, "Review", 20f, Color.parseColor("#171717"), true)
        title.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        title.setPadding(10, 0, 0, 0)
        title.gravity = Gravity.CENTER_VERTICAL
        top.addView(title)

        content.addView(top)

        content.addView(tv(context, "Words marked for review", 13f, Color.parseColor("#77736E"), false).apply {
            setPadding(0, 0, 0, 16)
        })

        val marked = prefs.getStringSet("review_words", emptySet()) ?: emptySet<String>()

        if (marked.isEmpty()) {
            content.addView(tv(context, "No words marked for review yet.", 15f, Color.parseColor("#77736E"), false).apply {
                setPadding(0, 40, 0, 0)
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
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100)
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
        row.setPadding(12, 10, 12, 10)

        row.addView(tv(context, w.hanzi, 22f, Color.parseColor("#171717"), true))
        row.addView(tv(context, w.pinyin, 15f, Color.parseColor("#77736E"), false))
        row.addView(tv(context, w.meaning, 15f, Color.parseColor("#171717"), false))

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
        root.setBackgroundColor(Color.parseColor("#F8F6F2"))

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
        content.setPadding(16, 16, 16, 80)

        val avatar = Button(context)
        avatar.text = "文"
        avatar.textSize = 32f
        avatar.setTextColor(Color.parseColor("#0A0A0A"))
        avatar.setBackgroundColor(Color.parseColor("#C62828"))
        avatar.layoutParams = ViewGroup.LayoutParams(64, 64)
        avatar.gravity = Gravity.CENTER

        val avatarWrap = LinearLayout(context)
        avatarWrap.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        avatarWrap.gravity = Gravity.CENTER
        avatarWrap.addView(avatar)
        content.addView(avatarWrap)

        content.addView(tv(context, "Learner", 20f, Color.parseColor("#171717"), true).apply {
            setPadding(0, 8, 0, 2)
            gravity = Gravity.CENTER
        })

        content.addView(tv(context, "HSK 2 · 300 words", 13f, Color.parseColor("#77736E"), false).apply {
            gravity = Gravity.CENTER
        })

        content.addView(View(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 24)
        })

        val statsCard = card(context)
        statsCard.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val sTitle = tv(context, "Your stats", 16f, Color.parseColor("#171717"), true)
        sTitle.setPadding(0, 0, 0, 12)
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
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100)
        })

        scroll.addView(content)
        root.addView(scroll)
        return root
    }

    private fun statBox(context: Context, value: String, label: String): LinearLayout {
        val col = LinearLayout(context)
        col.orientation = LinearLayout.VERTICAL
        col.layoutParams = ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        col.layoutParams = wlp(context)
        col.gravity = Gravity.CENTER

        col.addView(tv(context, value, 20f, Color.parseColor("#C62828"), true))

        val l = tv(context, label, 11f, Color.parseColor("#77736E"), true)
        l.setPadding(4, 0, 0, 0)
        col.addView(l)
        return col
    }

    private fun dividerLine(context: Context): View {
        val v = View(context)
        v.layoutParams = ViewGroup.LayoutParams(1, 40)
        v.setBackgroundColor(Color.parseColor("#E9E5DF"))
        return v
    }
}
