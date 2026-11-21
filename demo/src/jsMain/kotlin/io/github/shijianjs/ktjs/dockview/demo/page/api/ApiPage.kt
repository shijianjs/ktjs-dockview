package io.github.shijianjs.ktjs.dockview.demo.page.api

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.css.cssSheet
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.element.invoke
import web.cssom.BorderCollapse
import web.cssom.CSSStyleSheet
import web.cssom.ClassName
import web.cssom.Padding
import web.cssom.TextAlign
import web.cssom.integer
import web.cssom.pct
import web.cssom.px
import web.html.HTMLTableRowElement
import web.html.HTMLElement
import web.html.HtmlTagName

/**
 * The mapping table: what the module declares, and what it deliberately leaves to upstream.
 *
 * The right column is code, identical in both languages, so a switch only rewrites the left column.
 */
internal class ApiPage : DemoPage() {

    private val binder = Binder(apiTexts)

    private val headRow: HTMLTableRowElement = HtmlTagName.tr()

    private val bodyRows = HtmlTagName.tbody()

    private val rows: List<ApiRow>

    init {
        rows = english.rows.map {
            val labelCell = HtmlTagName.td { classList.add(API_CELL) }
            val valueCell = HtmlTagName.td { classList.add(API_CELL) }
            bodyRows.append(HtmlTagName.tr { append(labelCell, valueCell) })
            ApiRow(labelCell, valueCell)
        }

        val table = HtmlTagName.table {
            classList.add(API_TABLE)
            append(HtmlTagName.thead { append(headRow) }, bodyRows)
        }

        val section = SectionBlock.NAME { show() }
        binder.bind(section.heading) { it.heading }
        binder.bind(section.hint) { it.hint }
        section.body.append(exampleBox(null, table))

        addSheet(TABLE_SHEET)
        body.append(section.root)
    }

    override fun applyTranslation(lang: Lang) {
        val current = apiTexts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        binder.apply(lang)

        headRow.replaceChildren(
            HtmlTagName.th {
                classList.add(API_CELL)
                classList.add(API_HEAD_CELL)
                textContent = current.upstreamHeading
            },
            HtmlTagName.th {
                classList.add(API_CELL)
                classList.add(API_HEAD_CELL)
                textContent = current.kotlinHeading
            },
        )
        current.rows.zip(rows).forEach { (row, cells) ->
            cells.labelCell.textContent = row.first
            cells.valueCell.textContent = row.second
        }
    }

    companion object : ApiPageDefaultReference() {
        private val english: ApiTexts = ApiTexts(
            title = "What the wrapper maps",
            summary = "Upstream ships full `.d.ts` files: every Kotlin type below follows `dist/cjs/index.d.ts`.",
            heading = "Declaration by declaration",
            hint = "Read the upstream declaration, then decide the Kotlin type — the table is what the module answers for.",
            upstreamHeading = "upstream",
            kotlinHeading = "in Kotlin",
            rows = listOf(
                "npm package" to "dockview-core (module: dist/package/main.esm.mjs)",
                "entry points" to "createDockview / createSplitview / createGridview / createPaneview",
                "components" to "DockviewComponent: api, addPanel, addGroup, toJSON, fromJSON, updateOptions",
                "APIs" to "DockviewApi: panels, groups, activePanel, addPanel, getPanel, removePanel, maximizeGroup",
                "renderers" to "IContentRenderer: element, init(params), dispose() — a Kotlin class may implement it",
                "themes" to "18 exported values: themeDark, themeLight, themeAbyss, …",
                "opaque shapes" to "SerializedDockview / SerializedSplitview — passed back to fromJSON",
            ),
        )
        private val chinese: ApiTexts = ApiTexts(
            title = "封装层映射了什么",
            summary = "上游自带完整的 `.d.ts`：下面每个 Kotlin 类型都对 `dist/cjs/index.d.ts` 声明。",
            heading = "逐条对照",
            hint = "先读上游声明，再决定 Kotlin 类型——这张表就是模块要交代的部分。",
            upstreamHeading = "上游",
            kotlinHeading = "Kotlin 侧",
            rows = listOf(
                "npm 包" to "dockview-core（module: dist/package/main.esm.mjs）",
                "入口" to "createDockview / createSplitview / createGridview / createPaneview",
                "组件" to "DockviewComponent：api、addPanel、addGroup、toJSON、fromJSON、updateOptions",
                "API" to "DockviewApi：panels、groups、activePanel、addPanel、getPanel、removePanel、maximizeGroup",
                "渲染器" to "IContentRenderer：element、init(params)、dispose()——Kotlin 类可以实现它",
                "主题" to "18 个导出值：themeDark、themeLight、themeAbyss……",
                "不透明形状" to "SerializedDockview / SerializedSplitview —— 原样交回 fromJSON",
            ),
        )
        private val apiTexts: Translations<ApiTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        /** The table's own class names: only this page selects and carries them. */
        private val API_TABLE: ClassName = ClassName("api-table")
        private val API_CELL: ClassName = ClassName("api-cell")
        private val API_HEAD_CELL: ClassName = ClassName("api-cell-head")

        /**
         * A static definition table has no Vaadin component counterpart — `vaadin-grid` is the element for data
         * a user navigates, and this is six fixed rows — so it stays a real `<table>`.
         *
         * The rules are the table's own: collapse the borders, one padding per cell, and the header weight. Colours
         * come from Lumo's tokens, so the table follows the theme like everything else on the page.
         */
        private val TABLE_SHEET: CSSStyleSheet = cssSheet {
            className(API_TABLE).invoke {
                width = 100.pct
                borderCollapse = BorderCollapse.collapse
            }

            className(API_CELL).invoke {
                padding = Padding(6.px, 8.px)
            }

            className(API_HEAD_CELL).invoke {
                width = 32.pct
                fontWeight = integer(600)
                textAlign = TextAlign.left
            }
        }
    }
}
