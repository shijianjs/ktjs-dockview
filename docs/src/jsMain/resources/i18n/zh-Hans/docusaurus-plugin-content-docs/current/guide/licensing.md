---
sidebar_position: 3
---

# 许可

## 本封装：MIT

`ktjs-dockview` 以 MIT 许可发布，与上游条款一致。许可全文在仓库的
[`LICENSE`](https://github.com/shijianjs/ktjs-dockview/blob/main/LICENSE)。

## 上游：MIT，商业包除外

`dockview` 是一个 monorepo，**并非所有包都共用同一套许可**。上游自己的 `LICENCE.md` 才是逐包的权威：

| 上游包 | 许可 | 本模块是否分发 |
| --- | --- | --- |
| `dockview-core` | MIT | 是，作为 `dockview` 的依赖 |
| `dockview` | MIT | 是，本模块声明的 npm 包 |
| `dockview-react` | MIT | 否 |
| `dockview-vue` | MIT | 否 |
| `dockview-angular` | MIT | 否 |
| `dockview-enterprise` | **专有——商业许可** | **否** |

上游 MIT 全文，按许可要求转载：

> Copyright (c) 2021 mathuo
>
> Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
> associated documentation files (the "Software"), to deal in the Software without restriction,
> including without limitation the rights to use, copy, modify, merge, publish, distribute,
> sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
> furnished to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in all copies or substantial
> portions of the Software.
>
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
> NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
> NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
> OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
> CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## 商业包不进这里

`dockview-enterprise` **不是**开源软件。它的 `LICENCE.md` 是 Dockview Ltd 与被许可方之间的商业协议，
只覆盖企业功能；dockview 的其余部分仍是 MIT。这个包的任何内容都没有作为本封装的一部分被声明、
打包、测试或文档化，Maven 产物的唯一运行时 npm 依赖是 MIT 的 `dockview`。

如果需要企业功能，请直接向 Dockview Ltd 购买许可，再在自己的项目里加那个 npm 包。本模块不会去映射
它：它的类型不属于免费分发的一部分。

## 消费方要保留什么

- 本模块的 `LICENSE`，以及再分发打包后的 JavaScript 时上面的版权声明。
- 上游样式表（`dockview/dist/styles/dockview.css`）同样是 MIT；像 demo 那样把规则拷进 adopted
  stylesheet，仍在同一套条款之下。
- 本封装没有使用的框架包不需要署名，因为它们不是发布产物的依赖。
