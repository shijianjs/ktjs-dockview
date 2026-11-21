# ktjs-dockview

Kotlin/JS wrapper for [`dockview`](https://www.npmjs.com/package/dockview). One npm package, one
Gradle module, one Kotlin package: `DockviewModule.kt` holds the factories, the component classes and
the four APIs, `DockviewTypes.kt` the option bags, the renderer contract and the event types.

## Installation

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-dockview:8.9.1-kt.1")
}
```

## Usage

```kotlin
import io.github.shijianjs.ktjs.dockview.GroupPanelPartInitParameters
import io.github.shijianjs.ktjs.dockview.IContentRenderer
import io.github.shijianjs.ktjs.dockview.addPanelOptions
import io.github.shijianjs.ktjs.dockview.createDockview
import io.github.shijianjs.ktjs.dockview.dockviewComponentOptions
import io.github.shijianjs.ktjs.dockview.listen
import io.github.shijianjs.ktjs.dockview.themeDark

val host = document.createElement(HtmlTagName.div).apply {
    style.width = "100%"
    style.height = "420px"
    document.body.append(this)
}

val api = createDockview(
    host,
    dockviewComponentOptions(createComponent = { options ->
        PanelRenderer(document.createElement(HtmlTagName.div), options.name)
    }) {
        theme = themeDark
    },
)

api.onDidAddPanel.listen { panel -> println("added ${panel.id}") }
api.addPanel(addPanelOptions { id = "alpha"; component = "text"; title = "Alpha" })

val saved = api.toJSON()
api.fromJSON(saved)

class PanelRenderer(
    override val element: HTMLDivElement,
    private val componentName: String,
) : IContentRenderer {
    override fun init(parameters: GroupPanelPartInitParameters) {
        element.textContent = parameters.title
        element.dataset["renderer"] = componentName
    }

    override fun dispose() = Unit
}
```

The dock needs upstream's stylesheet (`dockview/dist/styles/dockview.css`); a page that never loads it
gets an unstyled layout with no borders, tabs or drag handles — and no console error.

## Build and test

```bash
./gradlew :ktjs-dockview:jsBrowserTest
```

After adding or upgrading an npm dependency, refresh the shared lock file:

```bash
./gradlew kotlinUpgradePackageLock
```

The npm package ships full `.d.ts` files; the declarations follow `dist/cjs/index.d.ts`. What is
wrapped, what is deliberately not (including the commercial `dockview-enterprise` package), is listed
in [`../docs/development/coverage.md`](../docs/development/coverage.md).
