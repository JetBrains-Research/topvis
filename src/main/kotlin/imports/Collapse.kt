@file:JsModule("@geist-ui/react")
@file:JsNonModule

package imports

import react.*


@JsName("Collapse")
external val collapse: ComponentClass<CollapseProps>

external interface CollapseProps : Props {
    var title: String
}
