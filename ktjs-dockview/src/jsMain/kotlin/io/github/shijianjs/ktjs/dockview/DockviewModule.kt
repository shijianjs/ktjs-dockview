@file:JsModule("dockview")

package io.github.shijianjs.ktjs.dockview

import kotlin.js.JsAny
import web.html.HTMLElement

/**
 * `dockview`: the framework-agnostic core of dockview.
 *
 * ```js
 * import { createDockview, themeDark } from 'dockview';
 * const api = createDockview(el, { theme: themeDark, createComponent: … });
 * ```
 *
 * One npm package, four entry points and one theme list. The component classes below are the objects
 * behind those entry points; a page normally only touches the `*Api` the factories return, which is
 * why the APIs carry the full surface and the components only what a page sets up once.
 */

/** Creates a dock layout: tabs, groups, split views and drag-and-drop. */
external fun createDockview(element: HTMLElement, options: DockviewComponentOptions): DockviewApi

/** Creates a one-dimensional split view. */
external fun createSplitview(element: HTMLElement, options: SplitviewComponentOptions): SplitviewApi

/** Creates a grid of panels without tabs or docking. */
external fun createGridview(element: HTMLElement, options: GridviewComponentOptions): GridviewApi

/** Creates an accordion-style pane view. */
external fun createPaneview(element: HTMLElement, options: PaneviewComponentOptions): PaneviewApi

/** Theme names are exported values, not strings: `themeDark` is `{ name, className, … }`. */
external val themeDark: DockviewTheme
external val themeLight: DockviewTheme
external val themeVisualStudio: DockviewTheme
external val themeAbyss: DockviewTheme
external val themeDracula: DockviewTheme
external val themeAbyssSpaced: DockviewTheme
external val themeLightSpaced: DockviewTheme
external val themeNord: DockviewTheme
external val themeNordSpaced: DockviewTheme
external val themeCatppuccinMocha: DockviewTheme
external val themeCatppuccinMochaSpaced: DockviewTheme
external val themeMonokai: DockviewTheme
external val themeSolarizedLight: DockviewTheme
external val themeSolarizedLightSpaced: DockviewTheme
external val themeGithubDark: DockviewTheme
external val themeGithubDarkSpaced: DockviewTheme
external val themeGithubLight: DockviewTheme
external val themeGithubLightSpaced: DockviewTheme

/**
 * The dock component itself.
 *
 * `createDockview` wraps `new DockviewComponent(el, options)` and returns `component.api`, so a page
 * either holds this object or the API — never needs both.
 */
external open class DockviewComponent(element: HTMLElement, options: DockviewComponentOptions) {
    val api: DockviewApi

    fun addPanel(options: AddPanelOptions): DockviewPanel

    fun addGroup(options: AddGroupOptions?): DockviewGroupPanel

    fun removeGroup(group: DockviewGroupPanel)

    fun clear()

    fun updateOptions(options: DockviewOptions)

    fun toJSON(): SerializedDockview

    fun fromJSON(data: SerializedDockview)

    fun focus()

    fun dispose()
}

/**
 * The layout API a page works with.
 *
 * Every `add*` returns the created object; every `get*` returns `null` when the id is unknown, which is
 * what upstream's `undefined` becomes.
 */
external open class DockviewApi {
    val width: Double

    val height: Double

    /** The number of groups. */
    val size: Int

    /** The number of panels across every group. */
    val totalPanels: Int

    val panels: Array<DockviewPanel>

    val groups: Array<DockviewGroupPanel>

    val activePanel: DockviewPanel?

    val activeGroup: DockviewGroupPanel?

    val onDidLayoutChange: DockviewEventSource<LayoutChangeEvent>

    val onDidLayoutFromJSON: DockviewEventSource<JsAny?>

    val onDidAddPanel: DockviewEventSource<DockviewPanel>

    val onDidRemovePanel: DockviewEventSource<DockviewPanel>

    val onDidActivePanelChange: DockviewEventSource<JsAny?>

    val onDidAddGroup: DockviewEventSource<DockviewGroupPanel>

    val onDidRemoveGroup: DockviewEventSource<DockviewGroupPanel>

    val onDidMovePanel: DockviewEventSource<JsAny?>

    fun addPanel(options: AddPanelOptions): DockviewPanel

    fun addGroup(options: AddGroupOptions?): DockviewGroupPanel

    fun getPanel(id: String): DockviewPanel?

    fun getGroup(id: String): DockviewGroupPanel?

    fun removePanel(panel: DockviewPanel)

    fun removeGroup(group: DockviewGroupPanel)

    fun closeAllGroups()

    fun layout(width: Double, height: Double)

    fun layout(width: Double, height: Double, force: Boolean)

    fun focus()

    fun clear()

    fun dispose()

    fun toJSON(): SerializedDockview

    fun fromJSON(data: SerializedDockview)

    fun updateOptions(options: DockviewOptions)

    fun maximizeGroup(panel: DockviewPanel)

    fun exitMaximizedGroup()
}

/** A panel: one tab plus the content its renderer draws. */
external open class DockviewPanel {
    val id: String

    val title: String?

    val params: JsAny?

    val group: DockviewGroupPanel

    val api: DockviewPanelApi

    fun setActive()

    fun close()
}

/** A group: one tab strip and the panels under it. */
external open class DockviewGroupPanel {
    val id: String

    val panels: Array<DockviewPanel>

    val activePanel: DockviewPanel?

    val api: DockviewGroupPanelApi

    fun setActive()

    fun close()
}

/** The API of a single panel: title, activation and the events around them. */
external interface DockviewPanelApi {
    val id: String

    val title: String?

    val isActive: Boolean

    fun setTitle(title: String)

    fun setActive()

    fun close()

    val onDidTitleChange: DockviewEventSource<TitleEvent>

    val onDidActiveChange: DockviewEventSource<JsAny?>
}

/** The API of a single group. */
external interface DockviewGroupPanelApi {
    val id: String

    fun setActive()

    fun close()
}

/** The split view API: `createSplitview` returns this. */
external open class SplitviewApi {
    val width: Double

    val height: Double

    val orientation: String

    val panels: Array<SplitviewPanel>

    val onDidLayoutChange: DockviewEventSource<LayoutChangeEvent>

    val onDidAddView: DockviewEventSource<JsAny?>

    val onDidRemoveView: DockviewEventSource<JsAny?>

    val onDidLayoutFromJSON: DockviewEventSource<JsAny?>

    fun addPanel(options: AddSplitviewComponentOptions): SplitviewPanel

    fun getPanel(id: String): SplitviewPanel?

    fun removePanel(panel: SplitviewPanel)

    fun movePanel(from: Int, to: Int)

    fun layout(width: Double, height: Double)

    fun clear()

    fun focus()

    fun dispose()

    fun toJSON(): SerializedSplitview

    fun fromJSON(data: SerializedSplitview)
}

/** A split view panel. */
external open class SplitviewPanel {
    val id: String

    val title: String?

    val api: PanelApi
}

/** The grid view API: `createGridview` returns this. */
external open class GridviewApi {
    val width: Double

    val height: Double

    val panels: Array<GridviewPanel>

    val onDidLayoutChange: DockviewEventSource<LayoutChangeEvent>

    val onDidAddPanel: DockviewEventSource<GridviewPanel>

    val onDidRemovePanel: DockviewEventSource<GridviewPanel>

    val onDidActivePanelChange: DockviewEventSource<GridviewPanel?>

    fun addPanel(options: AddGridviewComponentOptions): GridviewPanel

    fun getPanel(id: String): GridviewPanel?

    fun removePanel(panel: GridviewPanel)

    fun layout(width: Double, height: Double)

    fun clear()

    fun focus()

    fun dispose()
}

/** A grid view panel. */
external open class GridviewPanel {
    val id: String

    val title: String?

    val api: PanelApi
}

/** The pane view API: `createPaneview` returns this. */
external open class PaneviewApi {
    val width: Double

    val height: Double

    val panels: Array<PaneviewPanel>

    val onDidLayoutChange: DockviewEventSource<LayoutChangeEvent>

    fun addPanel(options: AddPaneviewComponentOptions): PaneviewPanel

    fun getPanel(id: String): PaneviewPanel?

    fun removePanel(panel: PaneviewPanel)

    fun movePanel(from: Int, to: Int)

    fun layout(width: Double, height: Double)

    fun clear()

    fun focus()

    fun dispose()
}

/** A pane view panel. */
external open class PaneviewPanel {
    val id: String

    val title: String?

    val api: PanelApi
}

/** The base panel API every component shares. */
external interface PanelApi {
    val id: String

    val isActive: Boolean

    fun setActive()

    fun focus()
}
