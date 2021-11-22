package components

import react.*
import react.dom.*

external interface RepositoryProps : Props {
    var isRunMade: Int
}

val Repository = fc<RepositoryProps> { props ->
    when (props.isRunMade) {
        2 -> h3 {
            +"Loading..."
        }
        1 -> h3 {
            +"Repository trees:"
        }
        0 -> h3 {
            +"Repositories not found"
        }
        else -> h3 {}
    }
}

fun RBuilder.repository(handler: RepositoryProps.() -> Unit) = child(Repository) {
    attrs {
        handler()
    }
}