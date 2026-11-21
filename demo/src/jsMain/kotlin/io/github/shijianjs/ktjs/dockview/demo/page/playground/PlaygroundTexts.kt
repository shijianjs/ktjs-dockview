package io.github.shijianjs.ktjs.dockview.demo.page.playground

/** The playground page's copy: headings, the button labels, the status suffixes and the panel titles. */
internal class PlaygroundTexts(
    val title: String,
    val summary: String,
    val heading: String,
    val hint: String,
    val addPanel: String,
    val saveLayout: String,
    val restoreLayout: String,
    val switchTheme: String,
    val statusPrefix: String,
    val savedSuffix: String,
    val restoredSuffix: String,
    val nothingSavedSuffix: String,
    val darkSuffix: String,
    val lightSuffix: String,
    val extraTitle: String,
    val panels: List<PanelTexts>,
)

/** One panel: [id] is what the API reports, [title] is the tab, [body] is what the renderer draws. */
internal class PanelTexts(val id: String, val title: String, val body: String)
