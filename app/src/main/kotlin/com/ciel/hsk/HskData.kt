package com.ciel.hsk

data class HskWord(
    val hanzi: String,
    val pinyin: String,
    val meaning: String,
    val pos: String,
    val outOfSyllabus: Boolean = false
)

data class HskLesson(
    val id: String,
    val title: String,
    val sub: String,
    val words: List<HskWord>
)

data class HskModule(
    val level: Int,
    val title: String,
    val subtitle: String,
    val lessons: List<HskLesson>
)

object HskData {

    fun modules(): List<HskModule> {
        return listOf(
            module(1, "HSK 1", "150 words · Beginner", lessons1),
            module(2, "HSK 2", "300 words · Elementary (15 lessons)", lessons2),
            module(3, "HSK 3", "600 words · Intermediate", lessons3),
            module(4, "HSK 4", "1200 words · Upper-intermediate", lessons4),
            module(5, "HSK 5", "2500 words · Advanced", lessons5)
        )
    }

    private fun module(level: Int, title: String, subtitle: String, lessons: List<HskLesson>): HskModule =
        HskModule(level, title, subtitle, lessons)

    private val lessons1 = listOf(
        HskLesson("h1l1", "Greetings", "你好 · 谢谢 · 对不起", listOf(
            HskWord("你好", "nǐ hǎo", "hello / hi", "greeting"),
            HskWord("谢谢", "xièxie", "thank you", "v."),
            HskWord("对不起", "duìbùqǐ", "sorry", "phrase"),
            HskWord("不客气", "bù kèqi", "you're welcome", "phrase"),
            HskWord("再见", "zàijiàn", "goodbye", "greeting")
        )),
        HskLesson("h1l2", "Numbers", "一 二 三 十 百", listOf(
            HskWord("一", "yī", "one", "num."),
            HskWord("二", "èr", "two", "num."),
            HskWord("三", "sān", "three", "num."),
            HskWord("十", "shí", "ten", "num."),
            HskWord("百", "bǎi", "hundred", "num.")
        )),
        HskLesson("h1l3", "Family", "爸爸 · 妈妈 · 哥哥 · 姐姐", listOf(
            HskWord("爸爸", "bàba", "father", "n."),
            HskWord("妈妈", "māma", "mother", "n."),
            HskWord("哥哥", "gēge", "elder brother", "n."),
            HskWord("姐姐", "jiějiě", "elder sister", "n."),
            HskWord("家", "jiā", "home / family", "n.")
        ))
    )

    private val lessons2 = listOf(
        HskLesson("h2l1", "Lesson 1 — Travel & Sports", "旅游 · 觉得 · 为什么 · 运动", listOf(
            HskWord("旅游", "lǚyóu", "to travel, to take a trip", "v."),
            HskWord("觉得", "juéde", "to think, to feel", "v."),
            HskWord("最", "zuì", "most, to the greatest extent", "adv."),
            HskWord("为什么", "wèishénme", "why", ""),
            HskWord("也", "yě", "also, too", "adv."),
            HskWord("运动", "yùndòng", "sport; to take physical exercise", "n./v."),
            HskWord("踢足球", "tī zúqiú", "to play football", ""),
            HskWord("一起", "yìqǐ", "together", "adv."),
            HskWord("要", "yào", "to want to, would like to", "aux."),
            HskWord("新", "xīn", "new", "adj."),
            HskWord("它", "tā", "it", "pron."),
            HskWord("眼睛", "yǎnjing", "eye", "n.")
        )),
        HskLesson("h2l2", "Lesson 2 — Health & Routine", "生病 · 早上 · 跑步 · 起床 · 药 · 出院", listOf(
            HskWord("生病", "shēngbìng", "to fall ill, to be sick", "v."),
            HskWord("每", "měi", "every, each", "pron."),
            HskWord("早上", "zǎoshang", "morning", "n."),
            HskWord("跑步", "pǎobù", "to run, to jog", "v."),
            HskWord("起床", "qǐchuáng", "to get up, to get out of bed", "v."),
            HskWord("药", "yào", "medicine, drug", "n."),
            HskWord("身体", "shēntǐ", "body", "n."),
            HskWord("出院", "chūyuàn", "to be discharged from hospital", "v."),
            HskWord("高", "gāo", "tall, high", "adj."),
            HskWord("米", "mǐ", "meter", "m.", outOfSyllabus = true),
            HskWord("知道", "zhīdào", "to know", "v."),
            HskWord("休息", "xiūxí", "to rest", "v."),
            HskWord("忙", "máng", "busy", "adj."),
            HskWord("时间", "shíjiān", "time", "n.")
        )),
        HskLesson("h2l3", "Lesson 3 — Shopping & Home", "手表 · 千 · 报纸 · 送 · 房间 · 丈夫 · 颜色", listOf(
            HskWord("手表", "shǒubiǎo", "watch", "n."),
            HskWord("千", "qiān", "thousand", "num."),
            HskWord("报纸", "bàozhǐ", "newspaper", "n."),
            HskWord("送", "sòng", "to send, to deliver", "v."),
            HskWord("一下", "yīxià", "used after a verb, indicating an attempt", "num.-m."),
            HskWord("牛奶", "niúnǎi", "milk", "n."),
            HskWord("房间", "fángjiān", "room", "n."),
            HskWord("丈夫", "zhàngfū", "husband", "n."),
            HskWord("旁边", "pángbiān", "beside", "n."),
            HskWord("真", "zhēn", "really, indeed", "adv."),
            HskWord("粉色", "fěnsè", "pink", "n.", outOfSyllabus = true),
            HskWord("颜色", "yánsè", "color", "n."),
            HskWord("左边", "zuǒbian", "left side", "n."),
            HskWord("红色", "hóngsè", "red", "n./adj.")
        )),
        HskLesson("h2l4", "Lesson 4 — Birthday & Work", "生日 · 快乐 · 给 · 接 · 晚上 · 问 · 开始 · 介绍", listOf(
            HskWord("生日", "shēngrì", "birthday", "n."),
            HskWord("快乐", "kuàilè", "happy, glad", "adj."),
            HskWord("给", "gěi", "to, for (used after a verb)", "prep."),
            HskWord("接", "jiē", "to receive, to take, to accept", "v.", outOfSyllabus = true),
            HskWord("晚上", "wǎnshang", "evening, night", "n."),
            HskWord("问", "wèn", "to ask", "v."),
            HskWord("非常", "fēicháng", "very, extremely", "adv."),
            HskWord("开始", "kāishǐ", "to begin, to start", "v."),
            HskWord("已经", "yǐjīng", "already", "adv."),
            HskWord("长", "cháng", "long", "adj."),
            HskWord("两", "liǎng", "two", "num."),
            HskWord("帮", "bāng", "to help, to assist", "v."),
            HskWord("介绍", "jièshào", "to introduce, to recommend", "v.")
        )),
        HskLesson("h2l5", "Lesson 5 — Restaurant & Shopping", "外面 · 准备 · 鱼 · 吧 · 件 · 可以 · 考试 · 意思 · 咖啡", listOf(
            HskWord("外面", "wàimiàn", "outside", "n."),
            HskWord("准备", "zhǔnbèi", "to intend, to plan", "v."),
            HskWord("就", "jiù", "used to indicate a conclusion", "adv."),
            HskWord("鱼", "yú", "fish", "n."),
            HskWord("吧", "ba", "particle (suggestion, request)", "part."),
            HskWord("件", "jiàn", "piece (for clothes)", "m."),
            HskWord("还", "hái", "passably, fairly, rather", "adv."),
            HskWord("可以", "kěyǐ", "not bad", "adj."),
            HskWord("不错", "bùcuò", "pretty good", "adj."),
            HskWord("考试", "kǎoshì", "test, exam", "n."),
            HskWord("意思", "yìsi", "meaning", "n."),
            HskWord("咖啡", "kāfēi", "coffee", "n."),
            HskWord("对", "duì", "prep.", "prep.")
        )),
        HskLesson("h2l6", "Lesson 6 — Food & Directions", "门 · 外 · 自行车 · 羊肉 · 好吃 · 面条 · 打篮球 · 因为 · 所以 · 游泳", listOf(
            HskWord("门", "mén", "door, gate", "n."),
            HskWord("外", "wài", "outer, outside", "n."),
            HskWord("自行车", "zìxíngchē", "bike", "n."),
            HskWord("羊肉", "yángròu", "mutton", "n."),
            HskWord("好吃", "hǎochī", "delicious, yummy", "adj."),
            HskWord("面条", "miàntiáo", "noodles", "n."),
            HskWord("打篮球", "dǎ lánqiú", "to play basketball", ""),
            HskWord("因为", "yīnwèi", "because, since", "conj."),
            HskWord("所以", "suǒyǐ", "so, therefore", "conj."),
            HskWord("游泳", "yóuyǒng", "to swim", "v."),
            HskWord("经常", "jīngcháng", "often, frequently", "adv.", outOfSyllabus = true),
            HskWord("公斤", "gōngjīn", "kilogram", "m.", outOfSyllabus = true),
            HskWord("姐姐", "jiějiě", "elder sister", "n.")
        )),
        HskLesson("h2l7", "Lesson 7 — Airport & Travel", "教室 · 机场 · 路 · 离 · 公司 · 远 · 公共汽车 · 小时 · 到 · 走", listOf(
            HskWord("教室", "jiàoshì", "classroom", "n."),
            HskWord("机场", "jīchǎng", "airport", "n."),
            HskWord("路", "lù", "road, path, way", "n."),
            HskWord("离", "lí", "to be away from", "v."),
            HskWord("公司", "gōngsī", "company, firm", "n."),
            HskWord("远", "yuǎn", "far, distant", "adj."),
            HskWord("公共汽车", "gōnggòng qìchē", "bus", ""),
            HskWord("小时", "xiǎoshí", "hour", "n."),
            HskWord("慢", "màn", "slow", "adj."),
            HskWord("快", "kuài", "quick, fast", "adj."),
            HskWord("过", "guò", "to pass (time), to spend (time)", "v.", outOfSyllabus = true),
            HskWord("走", "zǒu", "to walk", "v."),
            HskWord("到", "dào", "to arrive, to reach", "v.")
        )),
        HskLesson("h2l8", "Lesson 8 — Hotel & Store", "再 · 让 · 告诉 · 等 · 找 · 事情 · 服务员 · 白 · 黑 · 贵", listOf(
            HskWord("再", "zài", "again, once more", "adv."),
            HskWord("让", "ràng", "to let, to allow", "v."),
            HskWord("告诉", "gàosù", "to tell", "v."),
            HskWord("等", "děng", "to wait, to await", "v."),
            HskWord("找", "zhǎo", "to look for", "v."),
            HskWord("事情", "shìqing", "thing, matter, affair", "n."),
            HskWord("服务员", "fúwùyuán", "attendant, waiter/waitress", "n."),
            HskWord("白", "bái", "white", "adj."),
            HskWord("黑", "hēi", "black", "adj."),
            HskWord("贵", "guì", "expensive", "adj.")
        )),
        HskLesson("h2l9", "Lesson 9 — Job & Test", "从 · 跳舞 · 第一 · 希望 · 问题 · 欢迎 · 上班 · 懂 · 完 · 题", listOf(
            HskWord("从", "cóng", "from", "prep."),
            HskWord("跳舞", "tiàowǔ", "to dance", "v."),
            HskWord("第一", "dì yī", "first", "num."),
            HskWord("希望", "xīwàng", "to hope, to wish", "v."),
            HskWord("问题", "wèntí", "question, problem", "n."),
            HskWord("欢迎", "huānyíng", "to welcome", "v.", outOfSyllabus = true),
            HskWord("上班", "shàngbān", "to work, to do a job", "v."),
            HskWord("懂", "dǒng", "to understand, to know", "v."),
            HskWord("完", "wán", "to finish, to end", "v."),
            HskWord("题", "tí", "question, problem", "n.")
        )),
        HskLesson("h2l10", "Lesson 10 — Hospital & Home", "课 · 帮助 · 别 · 哥哥 · 鸡蛋 · 西瓜 · 正在 · 手机 · 洗", listOf(
            HskWord("课", "kè", "class, lesson", "n."),
            HskWord("帮助", "bāngzhù", "to help, to assist, to aid", "v."),
            HskWord("别", "bié", "don't", "adv."),
            HskWord("哥哥", "gēge", "elder brother", "n."),
            HskWord("鸡蛋", "jīdàn", "(hen's) egg", "n."),
            HskWord("西瓜", "xīguā", "watermelon", "n."),
            HskWord("正在", "zhèngzài", "in the process of", "adv."),
            HskWord("手机", "shǒujī", "cell phone", "n."),
            HskWord("洗", "xǐ", "to wash, to bathe", "v.")
        )),
        HskLesson("h2l11", "Lesson 11 — Karaoke & Age", "唱歌 · 男 · 女 · 孩子 · 右边 · 比 · 便宜 · 说话 · 可能 · 去年 · 姓", listOf(
            HskWord("唱歌", "chànggē", "to sing", "v."),
            HskWord("男", "nán", "man, male", "adj."),
            HskWord("女", "nǚ", "woman, female", "adj."),
            HskWord("孩子", "háizi", "child, kid", "n."),
            HskWord("右边", "yòubian", "right, right side", "n."),
            HskWord("比", "bǐ", "than, (superior or inferior) to", "prep."),
            HskWord("便宜", "piányi", "cheap, inexpensive", "adj."),
            HskWord("说话", "shuōhuà", "to speak, to say, to talk", "v."),
            HskWord("可能", "kěnéng", "maybe, perhaps, probably", "adv."),
            HskWord("去年", "qùnián", "last year", "n."),
            HskWord("姓", "xìng", "family name, surname", "v.")
        )),
        HskLesson("h2l12", "Lesson 12 — Wife & Cold", "妻子 · 雪 · 零 · 度 · 穿 · 进 · 弟弟 · 近", listOf(
            HskWord("妻子", "qīzi", "wife", "n."),
            HskWord("雪", "xuě", "snow", "n."),
            HskWord("零", "líng", "zero", "num."),
            HskWord("度", "dù", "degree", "n.", outOfSyllabus = true),
            HskWord("穿", "chuān", "to wear, to put on", "v."),
            HskWord("进", "jìn", "to enter, to come/go in", "v."),
            HskWord("弟弟", "dìdì", "younger brother", "n."),
            HskWord("近", "jìn", "near, close", "adj.")
        )),
        HskLesson("h2l13", "Lesson 13 — Office & Directions", "着 · 手 · 拿 · 铅笔 · 班 · 长 · 笑 · 宾馆 · 一直 · 往 · 路口", listOf(
            HskWord("着", "zhe", "part. used to indicate a state", "part."),
            HskWord("手", "shǒu", "hand", "n."),
            HskWord("拿", "ná", "to hold, to take, to bring", "v.", outOfSyllabus = true),
            HskWord("铅笔", "qiānbǐ", "pencil", "n."),
            HskWord("班", "bān", "class, grade, team", "n.", outOfSyllabus = true),
            HskWord("长", "zhǎng", "to grow, to develop", "v.", outOfSyllabus = true),
            HskWord("笑", "xiào", "to smile, to laugh", "v."),
            HskWord("宾馆", "bīnguǎn", "hotel", "n."),
            HskWord("一直", "yìzhí", "straight, all along", "adv.", outOfSyllabus = true),
            HskWord("往", "wǎng", "to, towards", "prep."),
            HskWord("路口", "lùkǒu", "crossing, crossroads", "n.")
        )),
        HskLesson("h2l14", "Lesson 14 — Movie & Weather", "有意思 · 但是 · 虽然 · 次 · 玩儿 · 晴 · 百", listOf(
            HskWord("有意思", "yǒu yìsi", "interesting, fun", ""),
            HskWord("但是", "dànshì", "but, still, yet", "conj."),
            HskWord("虽然", "suīrán", "although, though", "conj."),
            HskWord("次", "cì", "m. time", "m."),
            HskWord("玩儿", "wánr", "to play, to have fun", "v."),
            HskWord("晴", "qíng", "sunny, fine, clear", "adj."),
            HskWord("百", "bǎi", "hundred", "num.")
        )),
        HskLesson("h2l15", "Lesson 15 — New Year", "日 · 新年 · 票 · 火车站 · 大家 · 更 · 妹妹 · 阴", listOf(
            HskWord("日", "rì", "day, date", "n."),
            HskWord("新年", "xīnnián", "New Year", "n."),
            HskWord("票", "piào", "ticket", "n."),
            HskWord("火车站", "huǒchēzhàn", "railway station", "n."),
            HskWord("大家", "dàjiā", "all, everybody", "pron.", outOfSyllabus = true),
            HskWord("更", "gèng", "more, to a greater extent", "adv.", outOfSyllabus = true),
            HskWord("妹妹", "mèimei", "younger sister", "n."),
            HskWord("阴", "yīn", "overcast, cloudy", "adj.")
        ))
    )

    private val lessons3 = listOf(
        HskLesson("h3l1", "Lesson 1 — Daily Life", "请问 · 方便 · 方向 · 交通", listOf(
            HskWord("请问", "qǐngwèn", "may I ask", "v."),
            HskWord("方便", "fāngbiàn", "convenient", "adj."),
            HskWord("方向", "fāngxiàng", "direction", "n."),
            HskWord("交通", "jiāotōng", "traffic / transport", "n.")
        )),
        HskLesson("h3l2", "Lesson 2 — Work & Study", "工作 · 学习 · 认真 · 办法", listOf(
            HskWord("工作", "gōngzuò", "work / job", "n./v."),
            HskWord("学习", "xuéxí", "to study", "v."),
            HskWord("认真", "rènzhēn", "serious / conscientious", "adj."),
            HskWord("办法", "bànfǎ", "method / way", "n.")
        ))
    )

    private val lessons4 = listOf(
        HskLesson("h4l1", "Lesson 1 — Society", "发展 · 社会 · 环境 · 问题", listOf(
            HskWord("发展", "fāzhǎn", "development", "n./v."),
            HskWord("社会", "shèhuì", "society", "n."),
            HskWord("环境", "huánjìng", "environment", "n."),
            HskWord("问题", "wèntí", "problem", "n.")
        ))
    )

    private val lessons5 = listOf(
        HskLesson("h5l1", "Lesson 1 — Abstract Concepts", "虽然 · 但是 · 尽管如此 · 影响", listOf(
            HskWord("虽然", "suīrán", "although", "conj."),
            HskWord("影响", "yǐngxiǎng", "influence / to affect", "n./v."),
            HskWord("尽管如此", "jìngǔnrúcǐ", "despite this", "conj.")
        ))
    )

    data class WordEntry(
        val hanzi: String,
        val pinyin: String,
        val meaning: String,
        val hskLevel: Int,
        val lessonId: String
    )

    fun allWords(): List<WordEntry> {
        val result = mutableListOf<WordEntry>()
        for (mod in modules()) {
            for (lesson in mod.lessons) {
                for (w in lesson.words) {
                    result.add(WordEntry(w.hanzi, w.pinyin, w.meaning, mod.level, lesson.id))
                }
            }
        }
        return result
    }

    fun wordsForLevel(level: Int): List<WordEntry> {
        return allWords().filter { it.hskLevel == level }
    }
}
