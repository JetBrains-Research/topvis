@file:JsModule("@salkaevruslan/react")
@file:JsNonModule
@file:JsQualifier("Tabs")

package imports

import react.*


@JsName("Item")
external val tabItem: ComponentClass<TabItemProps>

external interface TabItemProps : Props {
    var label: String
    var value: String
}
