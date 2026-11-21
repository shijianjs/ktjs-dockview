# ktjs-dockview

[`dockview`](https://www.npmjs.com/package/dockview) 的 Kotlin/JS 封装：一个 npm 包、一个 Gradle
模块、一个 Kotlin 包。`DockviewModule.kt` 放工厂、组件类与四个 API，`DockviewTypes.kt` 放选项集合、
渲染器契约与事件类型。

## 安装

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-dockview:8.9.1-kt.1")
}
```

## 用法

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

dock 需要上游的样式表（`dockview/dist/styles/dockview.css`）；页面不加载它，布局就没有边框、没有标签页、
没有拖拽手柄——控制台也不会报错。

## 构建与测试

```bash
./gradlew :ktjs-dockview:jsBrowserTest
```

新增或升级 npm 依赖后，刷新共享锁文件：

```bash
./gradlew kotlinUpgradePackageLock
```

npm 包自带完整 `.d.ts`，声明照 `dist/cjs/index.d.ts` 写。封了什么、刻意没封什么（包括商业的
`dockview-enterprise` 包），见 [`../docs/development/coverage.md`](../docs/development/coverage.md)。
