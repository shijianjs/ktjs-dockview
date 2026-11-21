package io.github.shijianjs.ktjs.dockview.demo.page.playground

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Readout
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dockview.DockviewApi
import io.github.shijianjs.ktjs.dockview.DockviewIDisposable
import io.github.shijianjs.ktjs.dockview.DockviewTheme
import io.github.shijianjs.ktjs.dockview.GroupPanelPartInitParameters
import io.github.shijianjs.ktjs.dockview.IContentRenderer
import io.github.shijianjs.ktjs.dockview.SerializedDockview
import io.github.shijianjs.ktjs.dockview.addPanelOptions
import io.github.shijianjs.ktjs.dockview.createDockview
import io.github.shijianjs.ktjs.dockview.dockviewComponentOptions
import io.github.shijianjs.ktjs.dockview.dockviewOptions
import io.github.shijianjs.ktjs.dockview.listen
import io.github.shijianjs.ktjs.dockview.themeDark
import io.github.shijianjs.ktjs.dockview.themeLight
import io.github.shijianjs.ktjs.dom.css.cssSheet
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.button.createButton
import web.cssom.CSSStyleSheet
import web.cssom.ClassName
import web.cssom.pct
import web.cssom.px
import web.dom.ElementId
import web.dom.clickEvent
import web.events.EventHandler
import web.events.addHandler
import web.html.HTMLDivElement
import web.html.HtmlTagName

/**
 * The one thing the wrapper exists for: a dock a reader can drive.
 *
 * The dock host is sized by the page's own sheet (dockview measures its container), the renderer is a
 * Kotlin class implementing upstream's [IContentRenderer], and every panel is added through the API.
 * The buttons take the half of the API a static page cannot reach otherwise: add a panel, save the
 * layout, restore it, and swap the theme.
 *
 * The readout is written from `api.panels` after every change, so it reports what upstream holds rather
 * than what the page remembers.
 */
internal class PlaygroundPage : DemoPage() {

    private val binder = Binder(playgroundTexts)

    private val host: HTMLDivElement = HtmlTagName.div {
        classList.add(HOST)
        id = ElementId("dockview-host")
    }

    private val status = Readout.NAME { show() }

    private val addPanel = createButton()

    private val saveLayout = createButton()

    private val restoreLayout = createButton()

    private val switchTheme = createButton()

    private val renderers = mutableListOf<PanelRenderer>()

    private var current: PlaygroundTexts = english

    /** The trailing note of the readout ("layout saved", …); it survives layout events until the next action. */
    private var suffix: String? = null

    private var saved: SerializedDockview? = null

    private var dark = true

    private var added = 0

    private lateinit var api: DockviewApi

    private lateinit var subscription: DockviewIDisposable

    init {
        val section = SectionBlock.NAME { show() }
        binder.bind(section.heading) { it.heading }
        binder.bind(section.hint) { it.hint }
        section.body.append(
            exampleBox(null, host, row(addPanel, saveLayout, restoreLayout, switchTheme), status.root),
        )

        addSheet(HOST_SHEET)
        // dockview ships its styles as a page-level stylesheet; the frame re-hosts it inside this page's
        // shadow tree, since document styles never cross that boundary.
        addLinkedSheet(DOCKVIEW_CSS)
        body.append(section.root)

        api = createDockview(
            host,
            dockviewComponentOptions(createComponent = { options ->
                PanelRenderer(HtmlTagName.div(), options.name) { current }.also { renderers += it }
            }) {
                theme = themeDark
            },
        )

        english.panels.forEach { panel ->
            api.addPanel(
                addPanelOptions {
                    id = panel.id
                    component = COMPONENT
                    title = panel.title
                },
            )
        }

        subscription = api.onDidLayoutChange.listen { writeStatus() }

        addPanel.clickEvent.addHandler(EventHandler {
            added++
            api.addPanel(
                addPanelOptions {
                    id = "extra-$added"
                    component = COMPONENT
                    title = current.extraTitle.replace("$", added.toString())
                },
            )
            suffix = null
            writeStatus()
        })
        saveLayout.clickEvent.addHandler(EventHandler {
            saved = api.toJSON()
            suffix = current.savedSuffix
            writeStatus()
        })
        restoreLayout.clickEvent.addHandler(EventHandler {
            val layout = saved
            if (layout == null) {
                suffix = current.nothingSavedSuffix
                writeStatus()
            } else {
                renderers.clear()
                api.fromJSON(layout)
                applyPanelTexts()
                suffix = current.restoredSuffix
                writeStatus()
            }
        })
        switchTheme.clickEvent.addHandler(EventHandler {
            dark = !dark
            api.updateOptions(dockviewOptions { theme = themeFor(dark) })
            suffix = if (dark) current.darkSuffix else current.lightSuffix
            writeStatus()
        })

        writeStatus()
    }

    /** Reads the panel count from upstream, so the readout cannot drift from the dock. */
    private fun writeStatus() {
        val base = "${current.statusPrefix}: ${api.panels.size}"
        status.set(if (suffix == null) base else "$base · $suffix")
    }

    private fun themeFor(dark: Boolean): DockviewTheme = if (dark) themeDark else themeLight

    /** Rewrites the tab titles and the panel copy after a language switch or a `fromJSON`. */
    private fun applyPanelTexts() {
        api.panels.forEach { panel ->
            val text = current.panels.firstOrNull { it.id == panel.id } ?: return@forEach
            panel.api.setTitle(text.title)
        }
        renderers.forEach { it.applyText(current) }
    }

    override fun applyTranslation(lang: Lang) {
        current = playgroundTexts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        binder.apply(lang)
        addPanel.textContent = current.addPanel
        saveLayout.textContent = current.saveLayout
        restoreLayout.textContent = current.restoreLayout
        switchTheme.textContent = current.switchTheme
        applyPanelTexts()
        // The note belongs to the previous language; drop it rather than show a half-translated readout.
        suffix = null
        writeStatus()
    }

    companion object : PlaygroundPageDefaultReference() {
        /** The renderer name every panel names; upstream requires one registered component name. */
        private const val COMPONENT = "text"

        /** The stylesheet `index.html` links and the page re-hosts into its shadow tree. */
        private const val DOCKVIEW_CSS = "dockview.css"

        private val english: PlaygroundTexts = PlaygroundTexts(
            title = "Playground",
            summary = "A dock created by `createDockview`: panels can be added, saved as JSON and restored, " +
                "and the theme swaps without rebuilding the layout.",
            heading = "Drive the dock",
            hint = "The panels are added through `DockviewApi.addPanel`; each one is drawn by a Kotlin " +
                "`IContentRenderer`. Save takes `toJSON()`, restore hands it back to `fromJSON()`.",
            addPanel = "Add panel",
            saveLayout = "Save layout",
            restoreLayout = "Restore layout",
            switchTheme = "Switch theme",
            statusPrefix = "Panels",
            savedSuffix = "layout saved",
            restoredSuffix = "layout restored",
            nothingSavedSuffix = "nothing saved yet",
            darkSuffix = "dark theme",
            lightSuffix = "light theme",
            extraTitle = "Panel $",
            panels = listOf(
                PanelTexts("alpha", "Alpha", "the first panel; drag its tab to another group"),
                PanelTexts("bravo", "Bravo", "a second panel in the same group"),
                PanelTexts("charlie", "Charlie", "close it and the empty group closes too"),
            ),
        )
        private val chinese: PlaygroundTexts = PlaygroundTexts(
            title = "演练场",
            summary = "用 `createDockview` 建的一个 dock：面板可以新增、存成 JSON 再恢复，换主题不用重建布局。",
            heading = "驱动这个 dock",
            hint = "面板都通过 `DockviewApi.addPanel` 加；每个面板由一个 Kotlin 写的 `IContentRenderer` 绘制。" +
                "保存走 `toJSON()`，恢复把它交回 `fromJSON()`。",
            addPanel = "加面板",
            saveLayout = "保存布局",
            restoreLayout = "恢复布局",
            switchTheme = "换主题",
            statusPrefix = "面板",
            savedSuffix = "布局已保存",
            restoredSuffix = "布局已恢复",
            nothingSavedSuffix = "还没保存过",
            darkSuffix = "暗色主题",
            lightSuffix = "亮色主题",
            extraTitle = "面板 $",
            panels = listOf(
                PanelTexts("alpha", "甲", "第一个面板；拖它的标签可以换到别的分组"),
                PanelTexts("bravo", "乙", "同一分组里的第二个面板"),
                PanelTexts("charlie", "丙", "关掉它，空分组会跟着关闭"),
            ),
        )
        private val playgroundTexts: Translations<PlaygroundTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )

        /** The host's own class: dockview needs a parent with a height, and the page's sheet owns that. */
        private val HOST: ClassName = ClassName("dockview-host")

        private val HOST_SHEET: CSSStyleSheet = cssSheet {
            className(HOST).invoke {
                width = 100.pct
                height = 420.px
            }
        }
    }
}

/**
 * The panel content of this page: a div that shows the panel's title and a line of copy.
 *
 * `init` is where the panel learns its own id, so the copy is looked up per panel instead of being baked
 * into the factory; [applyText] rewrites it when the site's language changes.
 */
internal class PanelRenderer(
    override val element: HTMLDivElement,
    private val componentName: String,
    private val texts: () -> PlaygroundTexts,
) : IContentRenderer {

    private var panelId: String? = null

    override fun init(parameters: GroupPanelPartInitParameters) {
        panelId = parameters.api.id
        element.classList.add(BODY)
        element.dataset["renderer"] = componentName
        element.dataset["panel"] = parameters.api.id
        draw(parameters.title)
    }

    /** Rewrites the heading and the copy; the tab title is set by the page through the panel API. */
    fun applyText(current: PlaygroundTexts) {
        val id = panelId ?: return
        draw(current.panels.firstOrNull { it.id == id }?.title ?: id)
    }

    override fun dispose() = Unit

    private fun draw(title: String) {
        val copy = texts().panels.firstOrNull { it.id == panelId }?.body.orEmpty()
        element.replaceChildren(
            HtmlTagName.strong { textContent = title },
            HtmlTagName.div { textContent = copy },
        )
    }

    private companion object {
        private val BODY: ClassName = ClassName("dockview-panel-body")
    }
}
