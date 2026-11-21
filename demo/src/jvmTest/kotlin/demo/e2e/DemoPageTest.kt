package demo.e2e

import com.microsoft.playwright.Page
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.WaitUntilState
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Smoke checks for the demo: the shared shell mounts, the drawer drives the router, the dock renders
 * its tabs, the buttons add a panel and save/restore the layout, the table and the list render their
 * copy, and a language switch rewrites the pages in place.
 *
 * The shell and each page own a shadow tree, so the demo's nodes are selected from `#demo` down
 * through the page element — Playwright's CSS engine crosses shadow boundaries.
 */
@UsePlaywright(DemoOptions::class)
class DemoPageTest {
    @RetryingTest(maxAttempts = 2)
    fun `overview renders the shell and the three cards`(page: Page) {
        openPage(page)

        assertTrue(page.title().contains("ktjs-dockview"), "title: ${page.title()}")
        assertTrue(page.locator("#demo img.layout__logo").isVisible(), "the shell serves the project logo")
        assertEquals("Overview", page.locator("#demo dockview-overview-page h1").first().textContent())

        // One top-level row for the lifted start group, one group with the two reference pages.
        val rows = page.locator("#demo vaadin-side-nav-item").count()
        assertTrue(rows >= 4, "drawer rows: $rows")

        val cards = page.locator("#demo dockview-overview-page a.card-link").count()
        assertEquals(3, cards, "one card per page behind the overview")
    }

    @RetryingTest(maxAttempts = 2)
    fun `a card opens the playground and the dock draws three tabs`(page: Page) {
        openPage(page)
        page.locator("#demo dockview-overview-page a.card-link[data-route='playground']").first().click()
        page.waitForSelector("#demo dockview-playground-page h1")
        page.waitForSelector("#demo dockview-playground-page #dockview-host .dv-tab")

        assertEquals("Playground", page.locator("#demo dockview-playground-page h1").first().textContent())
        assertEquals(3, page.locator("#demo dockview-playground-page #dockview-host .dv-tab").count())
        assertEquals("Alpha", page.locator("#demo dockview-playground-page #dockview-host .dv-tab").first().textContent())
        // The active panel is the one dockview mounts; its content is the Kotlin `IContentRenderer`.
        assertEquals(1, page.locator("#demo dockview-playground-page #dockview-host div[data-renderer='text']").count())
    }

    @RetryingTest(maxAttempts = 2)
    fun `the buttons add a panel and round-trip the layout`(page: Page) {
        openPage(page)
        openRoute(page, "playground")

        val status = page.locator("#demo dockview-playground-page .readout").first()
        assertEquals("Panels: 3", status.textContent())

        // add → save → restore → switch theme, in that order.
        page.locator("#demo dockview-playground-page vaadin-button").first().click()
        assertEquals("Panels: 4", status.textContent())

        page.locator("#demo dockview-playground-page vaadin-button").nth(1).click()
        assertEquals("Panels: 4 · layout saved", status.textContent())

        page.locator("#demo dockview-playground-page vaadin-button").nth(2).click()
        assertEquals("Panels: 4 · layout restored", status.textContent())

        page.locator("#demo dockview-playground-page vaadin-button").nth(3).click()
        assertEquals("Panels: 4 · light theme", status.textContent())
    }

    @RetryingTest(maxAttempts = 2)
    fun `the mapping table and the conventions list render`(page: Page) {
        openPage(page)
        openRoute(page, "api")
        assertEquals("What the wrapper maps", page.locator("#demo dockview-api-page h1").first().textContent())
        // Seven rows of the mapping plus the header row.
        assertEquals(8, page.locator("#demo dockview-api-page table tr").count())

        openRoute(page, "conventions")
        assertEquals("Conventions", page.locator("#demo dockview-conventions-page h1").first().textContent())
        assertEquals(5, page.locator("#demo dockview-conventions-page ul li").count())
    }

    @RetryingTest(maxAttempts = 2)
    fun `the language switch rewrites the page in place`(page: Page) {
        openPage(page)
        openRoute(page, "playground")
        page.waitForSelector("#demo dockview-playground-page #dockview-host .dv-tab")

        switchLanguage(page, "zh-Hans")
        assertEquals("演练场", page.locator("#demo dockview-playground-page h1").first().textContent())
        assertEquals("加面板", page.locator("#demo dockview-playground-page vaadin-button").first().textContent())
        assertEquals("面板: 3", page.locator("#demo dockview-playground-page .readout").first().textContent())
        assertEquals("甲", page.locator("#demo dockview-playground-page #dockview-host .dv-tab").first().textContent())
    }

    @RetryingTest(maxAttempts = 2)
    fun `the page keeps a single scrollbar`(page: Page) {
        openPage(page)
        openRoute(page, "api")

        val horizontal = page.evaluate(
            "() => document.documentElement.scrollWidth - document.documentElement.clientWidth",
        ) as Number
        assertEquals(0, horizontal.toInt(), "no horizontal overflow")
    }

    @RetryingTest(maxAttempts = 2)
    fun `page logs no console errors or failed requests`(page: Page) {
        val consoleErrors = mutableListOf<String>()
        val failedRequests = mutableListOf<String>()
        page.onConsoleMessage { message ->
            if (message.type() == "error") consoleErrors += message.text()
        }
        page.onRequestFailed { request ->
            failedRequests += "${request.url()} ${request.failure()}"
        }

        openPage(page)
        openRoute(page, "playground")
        page.waitForSelector("#demo dockview-playground-page #dockview-host .dv-tab")

        assertTrue(consoleErrors.isEmpty(), "console errors: $consoleErrors")
        assertTrue(failedRequests.isEmpty(), "failed requests: $failedRequests")
    }

    private fun openPage(page: Page) {
        page.navigate("/", Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
        page.waitForSelector("#demo dockview-overview-page h1")
    }

    /** Clicks the drawer item for [route]; the router is also what the menu drives. */
    private fun openRoute(page: Page, route: String) {
        page.locator("#demo vaadin-side-nav-item[data-route='$route']").first().click()
        page.waitForSelector("#demo dockview-$route-page h1")
    }

    /** Opens the layout's language menu and picks [code]. */
    private fun switchLanguage(page: Page, code: String) {
        page.locator("#demo [data-menu='lang']").first().click()
        page.locator("#demo [data-action='lang:$code']").first().click()
        page.waitForFunction("(expected) => document.documentElement.lang === expected", code)
    }
}
