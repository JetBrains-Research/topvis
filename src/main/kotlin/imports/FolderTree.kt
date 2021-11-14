@file:JsModule("@geist-ui/react")
@file:JsNonModule

package imports

import react.*
import util.TreeInfo

@JsName("GeistProvider")
external val geistProvider: ComponentClass<dynamic>

@JsName("CssBaseline")
external val cssBaseline: ComponentClass<dynamic>

@JsName("Tree")
external val tree: ComponentClass<TreeProps>

external interface TreeProps : Props {
    var value: Array<TreeInfo>
}
