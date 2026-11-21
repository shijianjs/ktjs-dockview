package io.github.shijianjs.ktjs.dockview.demo.page.overview

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Grid
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dockview.demo.siteTexts
import io.github.shijianjs.ktjs.dom.css.cssSheet
import io.github.shijianjs.ktjs.dom.css.dsl.child
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.tag
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.VaadinTagName
import io.github.shijianjs.ktjs.vaadin.card.CardVariants
import io.github.shijianjs.ktjs.vaadin.card.createCard
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.cssom.CSSStyleSheet
import web.cssom.ClassName
import web.cssom.Color
import web.cssom.Display
import web.cssom.None
import web.cssom.number
import web.html.HTMLElement
import web.html.HtmlTagName

/**
 * Landing page: what the module wraps, and the three pages that follow it.
 *
 * A card is a `vaadin-card` inside a link: the whole surface is clickable, and middle-click, "copy link
 * address" and keyboard activation come from the anchor instead of from a click handler.
 */
internal class OverviewPage : DemoPage() {

    private val binder = Binder(overviewTexts)

    private val cards = mutableListOf<CardLink>()

    init {
        val what = SectionBlock.NAME { show() }
        binder.bind(what.heading) { it.whatHeading }
        what.body.append(binder.bind(paragraph()) { it.whatBody })

        val start = SectionBlock.NAME { show() }
        binder.bind(start.heading) { it.startHeading }
        binder.bind(start.hint) { it.startHint }
        start.body.append(Grid.NAME { show(240); add(*buildCards().toTypedArray()) })

        addSheet(CARD_SHEET)
        body.append(what.root, start.root)
    }

    private fun buildCards(): List<HTMLElement> = listOf("playground", "api", "conventions").map { id ->
        val summary = HtmlTagName.div()
        // The title comes from the frame's table after the first language is applied: one place names
        // a page, and the card is another view of that name.
        val card = createCard {
            addTheme(CardVariants.elevated)
            titleHeadingLevel = 4.0
            append(summary)
        }
        val link = HtmlTagName.a {
            href = "#/$id"
            classList.add(CARD_LINK)
            dataset["route"] = id
            append(card)
        }
        cards += CardLink(id, link, card, summary)
        link
    }

    override fun applyTranslation(lang: Lang) {
        val current = overviewTexts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        binder.apply(lang)

        val site = siteTexts[lang]
        cards.forEach { entry ->
            entry.card.cardTitle = site.pageTitles[entry.id] ?: entry.id
            entry.summary.textContent = current.cardSummaries[entry.id].orEmpty()
        }
    }

    companion object : OverviewPageDefaultReference() {
        private val english: OverviewTexts = OverviewTexts(
            title = "Overview",
            summary = "A Kotlin/JS wrapper around the dockview-core layout engine.",
            whatHeading = "What this module wraps",
            whatBody = "One npm package, one module: `ktjs-dockview` maps what `dockview-core` declares as " +
                "API onto Kotlin types the compiler checks — the dock, its panels and groups, the option bags and " +
                "the callable events. The demo is an ordinary consumer: it builds the layout through " +
                "`createDockview` and its own `IContentRenderer`, never through a string tag or a `dynamic` call.",
            startHeading = "Where to start",
            startHint = "Three pages, in the order a consumer needs them.",
            cardSummaries = mapOf(
                "playground" to "The one thing the wrapper exists for: a dock created by `createDockview`, panels " +
                    "added and closed, a layout saved and restored.",
                "api" to "What the wrapper maps, and what stays upstream's business — the table a consumer reads first.",
                "conventions" to "The rules this module is written by: declarations only, one package, types that " +
                    "follow the upstream declarations.",
            ),
        )
        private val chinese: OverviewTexts = OverviewTexts(
            title = "概览",
            summary = "围绕 dockview-core 布局引擎的 Kotlin/JS 封装。",
            whatHeading = "这个模块封装了什么",
            whatBody = "一个 npm 包对应一个模块：`ktjs-dockview` 只把 `dockview-core` 声明为 API 的东西映射成" +
                "编译器能检查的 Kotlin 类型——dock、面板与分组、选项与可调用事件。这个 demo 就是普通使用方：" +
                "通过 `createDockview` 与自己的 `IContentRenderer` 建布局，不用字符串标签、也不写 dynamic 调用。",
            startHeading = "从这里开始",
            startHint = "三页，按使用方需要的顺序排。",
            cardSummaries = mapOf(
                "playground" to "封装存在的理由：用 `createDockview` 建一个 dock，增删面板，保存并恢复布局。",
                "api" to "封装映射了什么、哪些仍是上游的事——使用方先读的那张表。",
                "conventions" to "这个模块遵守的规矩：只做声明、一个包一个模块、类型跟着上游声明走。",
            ),
        )
        private val overviewTexts: Translations<OverviewTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        /** The class the cards are found by in tests; it carries no style of its own. */
        private val CARD_LINK: ClassName = ClassName("card-link")

        /**
         * What the index needs from the wrapper — two declarations, each one caused by the anchor rather than by
         * the card: a link paints its text like a link and does not pass a row's height on to its children. The
         * card itself is left exactly as the component draws itself.
         */
        private val CARD_SHEET: CSSStyleSheet = cssSheet {
            className(CARD_LINK).invoke {
                display = Display.flex
                color = Color("inherit")
                textDecorationLine = None.none
            }

            // The grid stretches the anchor to the tallest card of its row; inside a flex anchor the card grows
            // to that height, so the surfaces of one row line up whatever their summaries are.
            className(CARD_LINK).child(tag(VaadinTagName.card)).invoke {
                flexGrow = number(1.0)
            }
        }
    }
}
