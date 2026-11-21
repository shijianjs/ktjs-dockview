# TODO

## 上游 API 覆盖

把 `dockview` 8.9.1 的公开声明（`dist/cjs/index.d.ts`：四个工厂、组件与四个 API 类、18 个主题、
选项与事件形状）映射成 Kotlin 类型，并在浏览器里跑通。

- 总计：70
- 已完成：66
- 明确不做：4
- 待完成：0

详细清单：[coverage.md](docs/development/coverage.md)

### 当前待办

无。下一步可选：`modules` 系统与 `popout` 助手若要纳入，需先补 `DockviewOptions` 的剩余字段
（上游约 60 个，现在声明了文档覆盖的 23 个）。

## 交付状态

- [x] 封装模块：`DockviewModule.kt`（工厂、组件、四个 API、主题）+ `DockviewTypes.kt`（选项、渲染器、事件）
- [x] 浏览器测试：9 个用例，`jsBrowserTest` 全绿
- [x] demo：四页（概览 / 演练场 / API / 约定）+ 中英切换 + Playwright 8 个用例
- [x] docs：四篇双语 + 首页 kit 五件套 + 右侧目录折叠
- [x] 元数据：README（中英，根与模块）、AGENTS.md、LICENSE、Pages 工作流
- [x] 建仓、提交、推送（<https://github.com/shijianjs/ktjs-dockview>，commit `7ef03ca`）
- [x] Pages：<https://shijianjs.github.io/ktjs-dockview/>（legacy + `gh-pages`，文档根、`/demo/`、`/api/`、`/zh-Hans/` 均 200；Pages 工作流暂为手动，等 kit 发版再恢复 push 触发）
