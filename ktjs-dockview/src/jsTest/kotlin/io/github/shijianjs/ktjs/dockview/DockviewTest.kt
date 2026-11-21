package io.github.shijianjs.ktjs.dockview

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import web.dom.document
import web.html.HTMLDivElement
import web.html.HTMLElement
import web.html.HtmlTagName

/**
 * Browser checks over the bundled `dockview-core`.
 *
 * The container is attached and sized, because dockview measures it: a detached element gives every
 * group a zero size. The renderer is a Kotlin class implementing upstream's `IContentRenderer` — if that
 * stops compiling, the wrapper's renderer contract has drifted from upstream.
 */
class DockviewTest {

    private fun container(): HTMLElement = document.createElement(HtmlTagName.div).also { element ->
        element.style.width = "800px"
        element.style.height = "400px"
        document.body.append(element)
    }

    private fun renderer(options: CreateComponentOptions): IContentRenderer =
        TextRenderer(document.createElement(HtmlTagName.div), options.name)

    @Test
    fun createDockviewReturnsAnEmptyApi() {
        val element = container()
        try {
            val api = createDockview(
                element,
                dockviewComponentOptions(::renderer) { theme = themeDark },
            )
            assertEquals(0, api.panels.size)
            assertEquals(0, api.totalPanels)
            assertEquals(0, api.groups.size)
            assertNull(api.activePanel)
            api.dispose()
        } finally {
            element.remove()
        }
    }

    @Test
    fun addPanelCreatesAPanelAndATab() {
        val element = container()
        try {
            val api = createDockview(element, dockviewComponentOptions(::renderer))
            val panel = api.addPanel(addPanelOptions {
                id = "alpha"
                component = "text"
                title = "Alpha"
            })
            assertEquals("alpha", panel.id)
            assertEquals("Alpha", panel.title)
            assertEquals(1, api.panels.size)
            assertEquals(1, api.groups.size)
            assertEquals(panel, api.activePanel)
        } finally {
            element.remove()
        }
    }

    @Test
    fun panelsAreLookedUpByIdAndRemoved() {
        val element = container()
        try {
            val api = createDockview(element, dockviewComponentOptions(::renderer))
            val first = api.addPanel(addPanelOptions { id = "one"; component = "text"; title = "One" })
            api.addPanel(addPanelOptions { id = "two"; component = "text"; title = "Two" })

            assertEquals(first, api.getPanel("one"))
            assertNull(api.getPanel("missing"))

            api.removePanel(first)
            assertEquals(1, api.panels.size)
            assertNull(api.getPanel("one"))
            assertNotNull(api.getPanel("two"))
        } finally {
            element.remove()
        }
    }

    @Test
    fun theRendererReceivesTheTitle() {
        val element = container()
        try {
            val api = createDockview(element, dockviewComponentOptions(::renderer))
            api.addPanel(addPanelOptions { id = "alpha"; component = "text"; title = "Alpha" })

            val rendererElement = element.querySelector(HTML_SELECTOR)
            assertNotNull(rendererElement, "the renderer's element is mounted inside the group")
            assertEquals("Alpha", rendererElement.textContent)
        } finally {
            element.remove()
        }
    }

    @Test
    fun aGroupCanBeAddedNextToAnother() {
        val element = container()
        try {
            val api = createDockview(element, dockviewComponentOptions(::renderer))
            api.addPanel(addPanelOptions { id = "alpha"; component = "text"; title = "Alpha" })

            val group = api.addGroup(addGroupOptions {
                id = "second"
                direction = "right"
                referenceGroup = api.groups.first()
            })
            assertEquals("second", group.id)
            assertEquals(2, api.groups.size)
        } finally {
            element.remove()
        }
    }

    @Test
    fun theLayoutRoundTripsThroughJSON() {
        val element = container()
        try {
            val api = createDockview(element, dockviewComponentOptions(::renderer))
            api.addPanel(addPanelOptions { id = "alpha"; component = "text"; title = "Alpha" })
            api.addPanel(addPanelOptions { id = "bravo"; component = "text"; title = "Bravo" })

            val saved = api.toJSON()
            assertEquals(2, api.panels.size)

            api.clear()
            assertEquals(0, api.panels.size)

            api.fromJSON(saved)
            assertEquals(2, api.panels.size)
            assertNotNull(api.getPanel("alpha"))
            assertNotNull(api.getPanel("bravo"))
        } finally {
            element.remove()
        }
    }

    @Test
    fun aSubscriptionIsDisposable() {
        val element = container()
        try {
            val api = createDockview(element, dockviewComponentOptions(::renderer))
            var added = 0
            val subscription = api.onDidAddPanel.listen { added++ }

            api.addPanel(addPanelOptions { id = "one"; component = "text"; title = "One" })
            assertEquals(1, added)

            subscription.dispose()
            api.addPanel(addPanelOptions { id = "two"; component = "text"; title = "Two" })
            assertEquals(1, added)
        } finally {
            element.remove()
        }
    }

    @Test
    fun panelTitlesCanBeRewrittenThroughThePanelApi() {
        val element = container()
        try {
            val api = createDockview(element, dockviewComponentOptions(::renderer))
            val panel = api.addPanel(addPanelOptions { id = "alpha"; component = "text"; title = "Alpha" })

            var titles = 0
            val subscription = panel.api.onDidTitleChange.listen { titles++ }
            panel.api.setTitle("Renamed")

            assertEquals("Renamed", panel.title)
            assertEquals(1, titles)
            subscription.dispose()
        } finally {
            element.remove()
        }
    }

    @Test
    fun themesAreObjectsWithAClassName() {
        assertEquals("dockview-theme-dark", themeDark.className)
        assertEquals("dockview-theme-light", themeLight.className)
        assertEquals("dark", themeDark.colorScheme)
    }

    /** A renderer written in Kotlin, which is the point of declaring the interface as external. */
    private class TextRenderer(override val element: HTMLDivElement, private val name: String) : IContentRenderer {
        override fun init(parameters: GroupPanelPartInitParameters) {
            element.setAttribute("data-renderer", name)
            element.textContent = parameters.title
        }

        override fun dispose() = Unit
    }

    private companion object {
        private const val HTML_SELECTOR = "div[data-renderer='text']"
    }
}
