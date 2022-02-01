@file:JsModule("@salkaevruslan/react")
@file:JsNonModule

package imports

import react.*


@JsName("Tabs")
external val tabs: ComponentClass<RadioProps>

external interface RadioProps : Props {
    var initialValue: String
    var value: String
    var onChange: (String) -> Unit
}
