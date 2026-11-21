---
sidebar_position: 3
---

# Licensing

## This wrapper: MIT

`ktjs-dockview` is released under the MIT Licence, the same terms as upstream. The licence text is
the repository's [`LICENSE`](https://github.com/shijianjs/ktjs-dockview/blob/main/LICENSE).

## Upstream: MIT, except the enterprise package

`dockview` is a monorepo, and **not all of its packages share one licence**. Upstream's own
`LICENCE.md` is authoritative per package:

| Upstream package | Licence | Shipping in this module |
| --- | --- | --- |
| `dockview-core` | MIT | yes, as a dependency of `dockview` |
| `dockview` | MIT | yes, the npm package this module declares |
| `dockview-react` | MIT | no |
| `dockview-vue` | MIT | no |
| `dockview-angular` | MIT | no |
| `dockview-enterprise` | **Proprietary — commercial licence** | **no** |

Upstream's MIT text, reproduced as the licence requires:

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

## The commercial package stays out

`dockview-enterprise` is **not** open source. Its `LICENCE.md` is a commercial agreement between
Dockview Ltd and the licensee, and it covers only the enterprise features; the rest of dockview remains
MIT. Nothing from that package is declared, bundled, tested or documented as part of this wrapper, and
the Maven artifact's only runtime npm dependency is the MIT `dockview` package.

If you need enterprise features, licence them from Dockview Ltd directly, then add the npm package to
your own project. This module will not map it: its types are not part of a free distribution.

## What a consumer has to keep

- The `LICENSE` file of this module, and the copyright notice above when redistributing the bundled
  JavaScript.
- The upstream stylesheet (`dockview/dist/styles/dockview.css`) is MIT as well; copying its rules into
  an adopted stylesheet, as the demo does, keeps it under the same terms.
- No attribution is required for the framework packages this wrapper does not use, because they are
  not dependencies of the published artifact.
