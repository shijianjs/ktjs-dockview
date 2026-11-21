package io.github.shijianjs.ktjs.dockview

import js.objects.unsafeJso
import kotlin.js.JsAny
import web.html.HTMLElement

/**
 * The option and event shapes `dockview-core` reads.
 *
 * The npm package ships full `.d.ts` files, unlike the other wrappers in this family: these
 * declarations follow `dist/cjs/index.d.ts`. Where upstream is a union or a very large serialized shape
 * (`SerializedDockview`, the module option bags) the wrapper keeps a `JsAny` slot or an opaque
 * interface instead of inventing a Kotlin union.
 */

/** The disposer every subscription and component hands back. */
external interface DockviewIDisposable {
    fun dispose()
}

/**
 * Upstream's `Event<T>` — a JS function object, `event(listener)`.
 *
 * In the type system it is a function taking the listener and returning the disposer, which is what a
 * callable object is; [listen] is the extension a page uses.
 */
typealias DockviewEventSource<T> = ((T) -> Unit) -> DockviewIDisposable

/** Subscribes a listener and returns the disposer; `event.listen { … }`. */
fun <T> DockviewEventSource<T>.listen(listener: (T) -> Unit): DockviewIDisposable = this(listener)

/** The payload of `onDidLayoutChange`: the reasons that fired, as upstream's `ReadonlySet`. */
external interface LayoutChangeEvent {
    val reasons: JsAny
}

/** The payload of `onDidTitleChange`. */
external interface TitleEvent {
    val title: String
}

/** A theme: a name and the class that carries its CSS variables. */
external interface DockviewTheme {
    var name: String
    var className: String
    var colorScheme: String?
    var gap: Double?
}

/**
 * The dock options a page sets once.
 *
 * A subset of upstream's `DockviewOptions`: the fields below are the ones the wrapper's documentation
 * and demo cover. Upstream has around sixty; the union-typed ones (`dndEdges`, `overflow`, `smartGuides`)
 * stay `JsAny` because their shapes are their own types.
 */
external interface DockviewOptions {
    var disableAutoResizing: Boolean?
    var proportionalLayout: Boolean?
    var hideBorders: Boolean?
    var singleTabMode: String?
    var disableFloatingGroups: Boolean?
    var floatingGroupBounds: JsAny?
    var floatingGroupDragHandle: String?
    var popoutUrl: String?
    var disableDnd: Boolean?
    var dndEdges: JsAny?
    var dndStrategy: String?
    var dndCompass: JsAny?
    var locked: Boolean?
    var className: String?
    var noPanelsOverlay: String?
    var closeEmptyGroups: Boolean?
    var theme: DockviewTheme?
    var scrollbars: String?
    var disableTabsOverflowList: Boolean?
    var overflow: JsAny?
    var defaultRenderer: String?
    var defaultHeaderPosition: String?
    var debug: Boolean?
}

/** The name a renderer is registered under and the component it draws. */
external interface CreateComponentOptions {
    var id: String
    var name: String
}

/**
 * What dockview hands a content renderer in `init`.
 *
 * `params` is the panel's own params object; `title` is the tab title; the two APIs are the panel's and
 * the whole layout's.
 */
external interface GroupPanelPartInitParameters {
    var params: JsAny?
    var title: String
    var api: DockviewPanelApi
    var containerApi: DockviewApi
}

/**
 * The content of a panel, as a plain object: an element, an `init`, and a `dispose`.
 *
 * A Kotlin class may implement this interface — that is how the demo draws its panels.
 */
external interface IContentRenderer {
    val element: HTMLElement
    fun init(parameters: GroupPanelPartInitParameters)
    fun dispose()
}

/** The pane content of a pane view; the same element-plus-init shape. */
external interface IPanePart {
    val element: HTMLElement
    fun init(params: JsAny)
}

/** The dock component's options: [DockviewOptions] plus the renderer factory upstream requires. */
external interface DockviewComponentOptions : DockviewOptions {
    var createComponent: (CreateComponentOptions) -> IContentRenderer
    var defaultTabComponent: String?
    var createTabComponent: ((CreateComponentOptions) -> JsAny)?
}

/** One panel to add: `component` names a renderer the factory can build. */
external interface AddPanelOptions {
    var id: String
    var component: String
    var title: String?
    var params: JsAny?
    var tabComponent: String?
    var renderer: String?
    var inactive: Boolean?
    var initialWidth: Double?
    var initialHeight: Double?

    /** Where to put it: a `position` object, or `floating = true`. */
    var position: JsAny?
    var floating: JsAny?
}

/** One group to add. */
external interface AddGroupOptions {
    var id: String?
    var locked: Boolean?
    var hideHeader: Boolean?
    var direction: String?

    /** A panel id or a group object; kept `JsAny` because upstream accepts both. */
    var referencePanel: JsAny?
    var referenceGroup: JsAny?
}

/**
 * The serialized layout.
 *
 * Opaque on purpose: `toJSON()` returns a deeply nested structure (grid, panels, floating and popout
 * groups, edge groups) whose shape changes with upstream minors, and the wrapper only passes it back to
 * `fromJSON()`. Persist it with `JSON.stringify` on the JS side if a string is what you need.
 */
external interface SerializedDockview

/** The split view serialized shape; opaque for the same reason. */
external interface SerializedSplitview

/** Split view options. */
external interface SplitviewOptions {
    var orientation: String?
    var margin: Double?
    var proportionalLayout: Boolean?
    var disableAutoResizing: Boolean?
    var className: String?
}

/** `createSplitview` options. */
external interface SplitviewComponentOptions : SplitviewOptions {
    var createComponent: (CreateComponentOptions) -> SplitviewPanel
}

/** One split view panel to add. */
external interface AddSplitviewComponentOptions {
    var id: String
    var component: String
    var title: String?
    var params: JsAny?
    var index: Int?
    var size: Double?
    var minimumSize: Double?
    var maximumSize: Double?
}

/** Grid view options. */
external interface GridviewOptions {
    var orientation: String
    var disableAutoResizing: Boolean?
    var proportionalLayout: Boolean?
    var hideBorders: Boolean?
    var className: String?
}

/** `createGridview` options. */
external interface GridviewComponentOptions : GridviewOptions {
    var createComponent: (CreateComponentOptions) -> GridviewPanel
}

/** One grid panel to add. */
external interface AddGridviewComponentOptions {
    var id: String
    var component: String
    var title: String?
    var params: JsAny?
}

/** Pane view options. */
external interface PaneviewOptions {
    var disableAutoResizing: Boolean?
    var disableDnd: Boolean?
    var className: String?
}

/** `createPaneview` options. */
external interface PaneviewComponentOptions : PaneviewOptions {
    var createComponent: (CreateComponentOptions) -> IPanePart
}

/** One pane to add. */
external interface AddPaneviewComponentOptions {
    var id: String
    var component: String
    var title: String?
    var params: JsAny?
    var size: Double?
}

/** Builds a [DockviewOptions] with only the fields the lambda sets. */
fun dockviewOptions(configure: DockviewOptions.() -> Unit = {}): DockviewOptions =
    unsafeJso<DockviewOptions>().apply(configure)

/** Builds the dock component options: the renderer factory plus the plain options. */
fun dockviewComponentOptions(
    createComponent: (CreateComponentOptions) -> IContentRenderer,
    configure: DockviewOptions.() -> Unit = {},
): DockviewComponentOptions = unsafeJso<DockviewComponentOptions> {
    this.createComponent = createComponent
}.apply(configure)

/** Builds an [AddPanelOptions]. */
fun addPanelOptions(configure: AddPanelOptions.() -> Unit): AddPanelOptions =
    unsafeJso<AddPanelOptions>().apply(configure)

/** Builds an [AddGroupOptions]. */
fun addGroupOptions(configure: AddGroupOptions.() -> Unit = {}): AddGroupOptions =
    unsafeJso<AddGroupOptions>().apply(configure)

/** Builds a [SplitviewComponentOptions]. */
fun splitviewComponentOptions(
    createComponent: (CreateComponentOptions) -> SplitviewPanel,
    configure: SplitviewOptions.() -> Unit = {},
): SplitviewComponentOptions = unsafeJso<SplitviewComponentOptions> {
    this.createComponent = createComponent
}.apply(configure)

/** Builds a [GridviewComponentOptions]. */
fun gridviewComponentOptions(
    orientation: String,
    createComponent: (CreateComponentOptions) -> GridviewPanel,
    configure: GridviewOptions.() -> Unit = {},
): GridviewComponentOptions = unsafeJso<GridviewComponentOptions> {
    this.orientation = orientation
    this.createComponent = createComponent
}.apply(configure)

/** Builds a [PaneviewComponentOptions]. */
fun paneviewComponentOptions(
    createComponent: (CreateComponentOptions) -> IPanePart,
    configure: PaneviewOptions.() -> Unit = {},
): PaneviewComponentOptions = unsafeJso<PaneviewComponentOptions> {
    this.createComponent = createComponent
}.apply(configure)
