@file:JsModule("@salkaevruslan/react")
@file:JsNonModule

package imports

import react.*
import util.TreeInfo

@JsName("Tree")
external val tree: ComponentClass<TreeProps>

external interface TreeProps : Props {
    var value: Array<TreeInfo>
}
