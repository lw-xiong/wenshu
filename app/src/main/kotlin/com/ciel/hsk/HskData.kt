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
    var words: List<HskWord>
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
        HskLesson("h3l1", "Lesson 1 — What's your plan for the weekend", "周末 · 打算 · 跟 · 一直 · 游戏", listOf(
            HskWord("周末", "zhōumò", "weekend", "n."),
            HskWord("打算", "dǎsuàn", "plan; to intend", "n./v."),
            HskWord("啊", "a", "used at the end of a sentence to indicate confirmation or defense", "part."),
            HskWord("跟", "gēn", "with", "prep."),
            HskWord("一直", "yīzhí", "all the time", "adv."),
            HskWord("游戏", "yóuxì", "game", "n."),
            HskWord("作业", "zuòyè", "homework", "n."),
            HskWord("着急", "zháojí", "worried, anxious", "adj."),
            HskWord("复习", "fùxí", "to review", "v."),
            HskWord("南(方)", "nán (fāng)", "south, southern part", "n."),
            HskWord("北方", "běifāng", "north, northern part", "n."),
            HskWord("面包", "miànbāo", "bread", "n."),
            HskWord("带", "dài", "to take along, to bring", "v."),
            HskWord("地图", "dìtú", "map", "n."),
            HskWord("搬", "bān", "to move, to carry", "v."),
            HskWord("小丽", "Xiǎolì", "Xiaoli, name of a person", "Proper Noun"),
            HskWord("小刚", "Xiǎogāng", "Xiaogang, name of a person", "Proper Noun")
        )),
        HskLesson("h3l2", "Lesson 2 — When will he come back", "腿 · 疼 · 脚 · 树 · 容易 · 难", listOf(
            HskWord("腿", "tuǐ", "leg", "n."),
            HskWord("疼", "téng", "hurt, painful", "adj."),
            HskWord("脚", "jiǎo", "foot", "n."),
            HskWord("树", "shù", "tree", "n."),
            HskWord("容易", "róngyì", "easy", "adj."),
            HskWord("难", "nán", "difficult", "adj."),
            HskWord("太太", "tàitai", "Mrs., madam", "n."),
            HskWord("秘书", "mìshū", "secretary", "n."),
            HskWord("经理", "jīnglǐ", "manager", "n."),
            HskWord("办公室", "bàngōngshì", "office", "n."),
            HskWord("辆", "liàng", "used for vehicles", "m."),
            HskWord("楼", "lóu", "building", "n."),
            HskWord("拿", "ná", "to take, to fetch", "v."),
            HskWord("把", "bǎ", "used for things with a handle", "m."),
            HskWord("伞", "sǎn", "umbrella", "n."),
            HskWord("胖", "pàng", "fat", "adj."),
            HskWord("其实", "qíshí", "actually", "adv."),
            HskWord("瘦", "shòu", "thin", "adj."),
            HskWord("周", "zhōu", "week", "n."),
            HskWord("周明", "Zhōumíng", "Zhou Ming, name of a person", "Proper Noun")
        )),
        HskLesson("h3l3", "Lesson 3 — There are plenty of drinks on the table", "还是 · 爬山 · 小心 · 条 · 裤子", listOf(
            HskWord("还是", "háishì", "or", "conj."),
            HskWord("爬山", "pá shān", "to climb a mountain", "v."),
            HskWord("小心", "xiǎoxīn", "careful", "adj."),
            HskWord("条", "tiáo", "used for pants, dresses, etc.", "m."),
            HskWord("裤子", "kùzi", "pants", "n."),
            HskWord("记得", "jìde", "to remember", "v."),
            HskWord("衬衫", "chènshān", "shirt", "n."),
            HskWord("元", "yuán", "unit of money", "m."),
            HskWord("新鲜", "xīnxiān", "fresh", "adj."),
            HskWord("甜", "tián", "sweet", "adj."),
            HskWord("只", "zhǐ", "only, solely", "adv."),
            HskWord("放", "fàng", "to put, to place", "v."),
            HskWord("饮料", "yǐnliào", "drink, beverage", "n."),
            HskWord("或者", "huòzhě", "or", "conj."),
            HskWord("舒服", "shūfu", "comfortable", "adj."),
            HskWord("花", "huā", "flower", "n."),
            HskWord("绿", "lǜ", "green", "adj.")
        )),
        HskLesson("h3l4", "Lesson 4 — She always smiles when talking to customers", "比赛 · 照片 · 年级 · 又 · 聪明", listOf(
            HskWord("比赛", "bǐsài", "match, competition", "n."),
            HskWord("照片", "zhàopiàn", "photo", "n."),
            HskWord("年级", "niánjí", "grade", "n."),
            HskWord("又", "yòu", "and", "adv."),
            HskWord("聪明", "cōngming", "clever, smart", "adj."),
            HskWord("热情", "rèqíng", "warm, enthusiastic", "adj."),
            HskWord("努力", "nǔlì", "hard-working", "adj."),
            HskWord("总是", "zǒngshì", "always", "adv."),
            HskWord("回答", "huídá", "to answer", "v."),
            HskWord("站", "zhàn", "to stand", "v."),
            HskWord("饿", "è", "hungry", "adj."),
            HskWord("超市", "chāoshì", "supermarket", "n."),
            HskWord("蛋糕", "dàngāo", "cake", "n."),
            HskWord("年轻", "niánqīng", "young", "adj."),
            HskWord("认真", "rènzhēn", "serious, earnest", "adj."),
            HskWord("客人", "kèrén", "customer, guest", "n."),
            HskWord("小明", "Xiǎomíng", "Xiaoming, name of a person", "Proper Noun"),
            HskWord("马可", "Mǎkě", "Marco, name of a person", "Proper Noun"),
            HskWord("李小美", "Lǐ Xiǎoměi", "Li Xiaomei, name of a person", "Proper Noun")
        )),
        HskLesson("h3l5", "Lesson 5 — I am getting fatter and fatter lately", "发烧 · 为 · 照顾 · 用 · 感冒", listOf(
            HskWord("发烧", "fā shāo", "to have a fever", "v."),
            HskWord("为", "wèi", "for", "prep."),
            HskWord("照顾", "zhàogù", "to take care of", "v."),
            HskWord("用", "yòng", "to need", "v."),
            HskWord("感冒", "gǎnmào", "to catch a cold", "v."),
            HskWord("张", "Zhāng", "Zhang, a surname", "Proper Noun"),
            HskWord("季节", "jìjié", "season", "n."),
            HskWord("当然", "dāngrán", "of course", "adv."),
            HskWord("春(天)", "chūn (tiān)", "spring", "n."),
            HskWord("草", "cǎo", "grass", "n."),
            HskWord("夏(天)", "xià (tiān)", "summer", "n."),
            HskWord("裙子", "qúnzi", "dress, skirt", "n."),
            HskWord("最近", "zuìjìn", "lately, recently", "adv."),
            HskWord("越", "yuè", "more, to a greater degree", "adv.")
        )),
        HskLesson("h3l6", "Lesson 6 — Why are they suddenly missing", "眼镜 · 突然 · 离开 · 清楚 · 刚才", listOf(
            HskWord("眼镜", "yǎnjìng", "glasses, spectacles", "n."),
            HskWord("突然", "tūrán", "suddenly", "adv."),
            HskWord("离开", "líkāi", "to leave, to part with", "v."),
            HskWord("清楚", "qīngchu", "clear, distinct", "adj."),
            HskWord("刚才", "gāngcái", "just now", "n."),
            HskWord("帮忙", "bāngmáng", "to help", "v."),
            HskWord("特别", "tèbié", "extraordinarily", "adv."),
            HskWord("讲", "jiǎng", "to explain", "v."),
            HskWord("明白", "míngbai", "clear", "adj."),
            HskWord("锻炼", "duànliàn", "to do physical exercise", "v."),
            HskWord("音乐", "yīnyuè", "music", "n."),
            HskWord("公园", "gōngyuán", "park", "n."),
            HskWord("聊天(儿)", "liáo tiān (r)", "to chat", "v."),
            HskWord("睡着", "shuì zháo", "to fall asleep", "v."),
            HskWord("更", "gèng", "more, even more", "adv.")
        )),
        HskLesson("h3l7", "Lesson 7 — I've known her for five years", "同事 · 以前 · 银行 · 久 · 感兴趣", listOf(
            HskWord("同事", "tóngshì", "colleague", "n."),
            HskWord("以前", "yǐqián", "before, ago", "n."),
            HskWord("银行", "yínháng", "bank", "n."),
            HskWord("久", "jiǔ", "for a long time, long", "adj."),
            HskWord("感兴趣", "gǎn xìngqù", "to be interested in", ""),
            HskWord("结婚", "jié hūn", "to marry, to get married", "v."),
            HskWord("欢迎", "huānyíng", "to welcome", "v."),
            HskWord("迟到", "chí dào", "to be late", "v."),
            HskWord("半", "bàn", "half", "num."),
            HskWord("接", "jiē", "to meet (sb.), to pick up (sb.)", "v."),
            HskWord("刻", "kè", "quarter", "m."),
            HskWord("差", "chà", "to fall short of", "v.")
        )),
        HskLesson("h3l8", "Lesson 8 — I'll go wherever you go", "又 · 满意 · 电梯 · 层 · 害怕", listOf(
            HskWord("又", "yòu", "again", "adv."),
            HskWord("满意", "mǎnyì", "to be satisfied", "v."),
            HskWord("电梯", "diàntī", "elevator", "n."),
            HskWord("层", "céng", "used for floors", "m."),
            HskWord("害怕", "hàipà", "to be afraid, to be scared", "v."),
            HskWord("熊猫", "xióngmāo", "panda", "n."),
            HskWord("见面", "jiànmiàn", "to meet", "v."),
            HskWord("安静", "ānjìng", "quiet", "adj."),
            HskWord("可乐", "kělè", "cola, Coke", "n."),
            HskWord("一会儿", "yíhuìr", "a moment", "n."),
            HskWord("马上", "mǎshàng", "immediately, at once", "adv."),
            HskWord("洗手间", "xǐshǒujiān", "bathroom, restroom", "n."),
            HskWord("老", "lǎo", "old", "adj."),
            HskWord("几乎", "jīhū", "almost", "adv."),
            HskWord("变化", "biànhuà", "to change", "v."),
            HskWord("健康", "jiànkāng", "healthy", "adj."),
            HskWord("重要", "zhòngyào", "important", "adj.")
        )),
        HskLesson("h3l9", "Lesson 9 — She speaks Chinese like a native", "中文 · 班 · 一样 · 最后 · 放心", listOf(
            HskWord("中文", "Zhōngwén", "Chinese language", "n."),
            HskWord("班", "bān", "class", "n."),
            HskWord("一样", "yīyàng", "same, as...as...", "adj."),
            HskWord("最后", "zuìhòu", "the last one", "n."),
            HskWord("放心", "fàngxīn", "to ease one's mind, to rest assured", "v."),
            HskWord("一定", "yīdìng", "definitely, certainly", "adv."),
            HskWord("担心", "dānxīn", "to worry", "v."),
            HskWord("比较", "bǐjiào", "fairly, rather", "adv."),
            HskWord("了解", "liǎojiě", "to know", "v."),
            HskWord("先", "xiān", "first, in advance", "adv."),
            HskWord("中间", "zhōngjiān", "middle", "n."),
            HskWord("参加", "cānjiā", "to participate", "v."),
            HskWord("影响", "yǐngxiǎng", "influence", "n.")
        )),
        HskLesson("h3l10", "Lesson 10 — Maths is much harder than history", "个子 · 矮 · 历史 · 体育 · 数学", listOf(
            HskWord("个子", "gèzi", "height, stature", "n."),
            HskWord("矮", "ǎi", "short", "adj."),
            HskWord("历史", "lìshǐ", "history", "n."),
            HskWord("体育", "tǐyù", "P.E., sports", "n."),
            HskWord("数学", "shùxué", "maths", "n."),
            HskWord("方便", "fāngbiàn", "convenient", "adj."),
            HskWord("自行车", "zìxíngchē", "bicycle", "n."),
            HskWord("骑", "qí", "to ride", "v."),
            HskWord("旧", "jiù", "old, used, worn", "adj."),
            HskWord("换", "huàn", "to change, to substitute", "v."),
            HskWord("地方", "dìfang", "place", "n."),
            HskWord("中介", "zhōngjiè", "intermediary, agent", "n."),
            HskWord("主要", "zhǔyào", "main", "adj."),
            HskWord("环境", "huánjìng", "environment", "n."),
            HskWord("附近", "fùjìn", "nearby, vicinity", "n.")
        )),
        HskLesson("h3l11", "Lesson 11 — Don't forget to turn off the air conditioner", "图书馆 · 借 · 词典 · 还 · 灯", listOf(
            HskWord("图书馆", "túshūguǎn", "library", "n."),
            HskWord("借", "jiè", "to borrow, to lend", "v."),
            HskWord("词典", "cídiǎn", "dictionary", "n."),
            HskWord("还", "huán", "to return", "v."),
            HskWord("灯", "dēng", "light, lamp", "n."),
            HskWord("会议", "huìyì", "meeting, conference", "n."),
            HskWord("结束", "jiéshù", "to end, to finish", "v."),
            HskWord("忘记", "wàngjì", "to forget", "v."),
            HskWord("空调", "kōngtiáo", "air conditioner", "n."),
            HskWord("关", "guān", "to turn off, to close", "v."),
            HskWord("地铁", "dìtiě", "subway", "n."),
            HskWord("双", "shuāng", "pair", "m."),
            HskWord("筷子", "kuàizi", "chopsticks", "n."),
            HskWord("啤酒", "píjiǔ", "beer", "n."),
            HskWord("口", "kǒu", "mouthful", "m."),
            HskWord("瓶子", "píngzi", "bottle", "n."),
            HskWord("笔记本（电脑）", "bǐjìběn (diànnǎo)", "notebook, laptop", "n."),
            HskWord("电子邮件", "diànzǐ yóujiàn", "e-mail", ""),
            HskWord("习惯", "xíguàn", "be used to habit; to", "v./n.")
        )),
        HskLesson("h3l12", "Lesson 12 — Leave the important items with me", "太阳 · 西 · 生气 · 行李箱 · 自己", listOf(
            HskWord("太阳", "tàiyáng", "sun", "n."),
            HskWord("西", "xī", "west", "n."),
            HskWord("生气", "shēng qì", "angry", "v."),
            HskWord("行李箱", "xínglǐxiāng", "luggage, suitcase", "n."),
            HskWord("自己", "zìjǐ", "self", "pron."),
            HskWord("包", "bāo", "bag, sack", "n."),
            HskWord("发现", "fāxiàn", "to discover", "v."),
            HskWord("护照", "hùzhào", "passport", "n."),
            HskWord("起飞", "qǐfēi", "(of an aircraft) to take off", "v."),
            HskWord("司机", "sījī", "driver", "n."),
            HskWord("教", "jiāo", "to teach", "v."),
            HskWord("画", "huà", "to draw, to paint; drawing, painting", "v./n."),
            HskWord("需要", "xūyào", "to need", "v."),
            HskWord("黑板", "hēibǎn", "blackboard", "n.")
        )),
        HskLesson("h3l13", "Lesson 13 — I walked back", "终于 · 爷爷 · 礼物 · 奶奶 · 遇到", listOf(
            HskWord("终于", "zhōngyú", "finally", "adv."),
            HskWord("爷爷", "yéye", "grandfather", "n."),
            HskWord("礼物", "lǐwù", "gift, present", "n."),
            HskWord("奶奶", "nǎinai", "grandmother", "n."),
            HskWord("遇到", "yùdào", "to come across, to run into", "v."),
            HskWord("一边", "yībiān", "indicating two actions taking place at the same time", "adv."),
            HskWord("过去", "guòqù", "past", "n."),
            HskWord("一般", "yībān", "general, usual", "adj."),
            HskWord("愿意", "yuànyì", "would like to", "v."),
            HskWord("起来", "qǐlai", "(indicating an upward movement) to rise", "v."),
            HskWord("应该", "yīnggāi", "should", "v."),
            HskWord("生活", "shēnghuó", "life", "n."),
            HskWord("校长", "xiàozhǎng", "headmaster", "n."),
            HskWord("坏", "huài", "broken, ruined", "adj."),
            HskWord("经常", "jīngcháng", "often", "adv.")
        )),
        HskLesson("h3l14", "Lesson 14 — Please bring the fruit here", "打扫 · 干净 · 然后 · 冰箱 · 洗澡", listOf(
            HskWord("打扫", "dǎsǎo", "to clean, to sweep", "v."),
            HskWord("干净", "gānjìng", "clean", "adj."),
            HskWord("然后", "ránhòu", "then, after that", "conj."),
            HskWord("冰箱", "bīngxiāng", "refrigerator", "n."),
            HskWord("洗澡", "xǐzǎo", "to take a bath/shower", "v."),
            HskWord("节目", "jiémù", "programme", "n."),
            HskWord("月亮", "yuèliang", "moon", "n."),
            HskWord("像", "xiàng", "to be like", "v."),
            HskWord("盘子", "pánzi", "plate", "n."),
            HskWord("刮风", "guāfēng", "to be windy", "v."),
            HskWord("叔叔", "shūshu", "uncle", "n."),
            HskWord("阿姨", "āyí", "aunt", "n."),
            HskWord("故事", "gùshì", "story", "n."),
            HskWord("声音", "shēngyīn", "sound, voice", "n."),
            HskWord("菜单", "càidān", "menu", "n."),
            HskWord("简单", "jiǎndān", "simple", "adj."),
            HskWord("香蕉", "xiāngjiāo", "banana", "n.")
        )),
        HskLesson("h3l15", "Lesson 15 — The rest of them are all OK", "留学 · 水平 · 提高 · 练习 · 完成", listOf(
            HskWord("留学", "liú xué", "to study abroad", "v."),
            HskWord("水平", "shuǐpíng", "level, standard", "n."),
            HskWord("提高", "tí gāo", "to improve", "v."),
            HskWord("练习", "liànxí", "exercise", "n."),
            HskWord("完成", "wán chéng", "to complete", "v."),
            HskWord("句子", "jùzi", "sentence", "n."),
            HskWord("其他", "qítā", "the rest", "pron."),
            HskWord("发", "fā", "to send", "v."),
            HskWord("要求", "yāoqiú", "requirement", "n."),
            HskWord("注意", "zhù yì", "to pay attention to", "v."),
            HskWord("小云", "Xiǎoyún", "Xiaoyun, name of a person", "Proper Noun"),
            HskWord("上网", "shàng wǎng", "to surf the Internet", "v."),
            HskWord("除了", "chúle", "other than", "prep."),
            HskWord("新闻", "xīnwén", "news", "n."),
            HskWord("花", "huā", "to spend", "v."),
            HskWord("极（了）", "jí (le)", "extremely", "adv."),
            HskWord("节日", "jiérì", "festival", "n."),
            HskWord("举行", "jǔxíng", "to hold (a meeting, an event, etc.)", "v."),
            HskWord("世界", "shìjiè", "world", "n."),
            HskWord("街道", "jiēdào", "street", "n."),
            HskWord("各", "gè", "each", "pron."),
            HskWord("文化", "wénhuà", "culture", "n.")
        )),
        HskLesson("h3l16", "Lesson 16 — I am so tired that I want to do nothing but sleep after work", "城市 · 如果 · 认为 · 皮鞋 · 帽子", listOf(
            HskWord("城市", "chéngshì", "city", "n."),
            HskWord("如果", "rúguǒ", "if, in case", "conj."),
            HskWord("认为", "rènwéi", "to think, to believe", "v."),
            HskWord("皮鞋", "píxié", "leather shoes", "n."),
            HskWord("帽子", "màozi", "hat, cap", "n."),
            HskWord("长", "zhǎng", "to grow, to develop", "v."),
            HskWord("可爱", "kě'ài", "lovely, adorable", "adj."),
            HskWord("米", "mǐ", "metre", "n."),
            HskWord("公斤", "gōngjīn", "kilogramme", "n."),
            HskWord("鼻子", "bízi", "nose", "n."),
            HskWord("头发", "tóufa", "hair", "n."),
            HskWord("检查", "jiǎnchá", "to check, to examine", "v."),
            HskWord("刷牙", "shuāyá", "to brush the teeth", "v."),
            HskWord("关系", "guānxì", "relationship", "n."),
            HskWord("别人", "biérén", "other people", "n."),
            HskWord("词语", "cíyǔ", "word, expression", "n.")
        )),
        HskLesson("h3l17", "Lesson 17 — Everybody is able to cure your disease", "请假 · 一共 · 邻居 · 后来 · 爱好", listOf(
            HskWord("请假", "qǐng jià", "to ask for leave", "v."),
            HskWord("一共", "yígòng", "in total", "adv."),
            HskWord("邻居", "línjū", "neighbour", "n."),
            HskWord("后来", "hòulai", "later, afterwards", "n."),
            HskWord("爱好", "àihào", "hobby, interest", "n."),
            HskWord("办法", "bànfǎ", "way, approach", "n."),
            HskWord("饱", "bǎo", "full, having eaten one's fill", "adj."),
            HskWord("为了", "wèile", "for the sake of", "prep."),
            HskWord("决定", "juédìng", "to decide", "v."),
            HskWord("选择", "xuǎnzé", "to choose", "v."),
            HskWord("冬（天）", "dōng (tiān)", "winter", "n."),
            HskWord("必须", "bìxū", "must", "adv."),
            HskWord("根据", "gēnjù", "according to, based on", "prep."),
            HskWord("情况", "qíngkuàng", "condition, situation", "n."),
            HskWord("口", "kǒu", "mouth", "n."),
            HskWord("渴", "kě", "thirsty", "adj.")
        )),
        HskLesson("h3l18", "Lesson 18 — I believe they'll agree", "向 · 万 · 只 · 嘴 · 动物", listOf(
            HskWord("向", "xiàng", "towards", "prep."),
            HskWord("万", "wàn", "ten thousand", "num."),
            HskWord("只", "zhī", "used for certain animals", "m."),
            HskWord("嘴", "zuǐ", "mouth", "n."),
            HskWord("动物", "dòngwù", "animal", "n."),
            HskWord("段", "duàn", "used for sections or periods", "m."),
            HskWord("不但……而且……", "bùdàn……érqiě……", "not only... but also...", "conj."),
            HskWord("有名", "yǒumíng", "famous", "adj."),
            HskWord("同意", "tóngyì", "to agree", "v."),
            HskWord("相信", "xiāngxìn", "to believe", "v."),
            HskWord("关于", "guānyú", "about, regarding", "prep."),
            HskWord("机会", "jīhuì", "opportunity", "n."),
            HskWord("国家", "guójiā", "country, nation", "n."),
            HskWord("种", "zhǒng", "kind, type", "m."),
            HskWord("特点", "tèdiǎn", "feature", "n."),
            HskWord("奇怪", "qíguài", "strange, odd", "adj."),
            HskWord("地", "de", "used to connect an adverbial modifier and the verb it modifies", "part.")
        )),
        HskLesson("h3l19", "Lesson 19 — Didn't you recognise him", "耳朵 · 脸 · 短 · 马 · 张", listOf(
            HskWord("耳朵", "ěrduo", "ear", "n."),
            HskWord("脸", "liǎn", "face", "n."),
            HskWord("短", "duǎn", "short", "adj."),
            HskWord("马", "mǎ", "horse", "n."),
            HskWord("张", "zhāng", "used for flat objects such as paper, photos, etc.", "m."),
            HskWord("位", "wèi", "a respectful measure word for people", "m."),
            HskWord("蓝", "lán", "blue", "adj."),
            HskWord("秋（天）", "qiū (tiān)", "autumn, fall", "n."),
            HskWord("过", "guò", "to spend, to pass", "v."),
            HskWord("鸟", "niǎo", "bird", "n."),
            HskWord("哭", "kū", "to cry", "v."),
            HskWord("黄河", "Huáng Hé", "Yellow River, the second longest river in China", "Proper Noun"),
            HskWord("船", "chuán", "boat, ship", "n."),
            HskWord("经过", "jīngguò", "to pass by", "v.")
        )),
        HskLesson("h3l20", "Lesson 20 — I've been influenced by him", "照相机 · 被 · 难过 · 东 · 信用卡", listOf(
            HskWord("照相机", "zhàoxiàngjī", "camera", "n."),
            HskWord("被", "bèi", "used to indicate the passive voice", "prep."),
            HskWord("难过", "nánguò", "sad", "adj."),
            HskWord("东", "dōng", "east", "n."),
            HskWord("信用卡", "xìnyòngkǎ", "credit card", "n."),
            HskWord("关心", "guānxīn", "to care for, to be interested in", "v."),
            HskWord("只有...才...", "zhǐyǒu...cái...", "only ... (that/can ...)", "conj."),
            HskWord("成绩", "chéngjì", "grade, performance, achievement", "n."),
            HskWord("碗", "wǎn", "bowl", "n."),
            HskWord("分", "fēn", "to distinguish", "v."),
            HskWord("解决", "jiějué", "to solve", "v."),
            HskWord("试", "shì", "to try", "v."),
            HskWord("真正", "zhēnzhèng", "really, truly", "adv."),
            HskWord("多么", "duōme", "very, to a great extent", "adv.")
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
