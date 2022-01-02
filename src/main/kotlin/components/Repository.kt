package components

import config.Config
import kotlinx.css.TextAlign
import kotlinx.css.fontSize
import kotlinx.css.textAlign
import react.*
import styled.css
import styled.styledDiv
import styled.styledH3
import util.RunStatus

external interface RepositoryProps : Props {
    var isRunMade: RunStatus
}

val Repository = fc<RepositoryProps> { props ->
    when (props.isRunMade) {
        RunStatus.IN_PROCESS -> styledH3 {
            css {
                fontSize = Config.fontSize
                textAlign = TextAlign.center
            }
            +"Loading..."
        }
        RunStatus.OK -> styledDiv {
        }
        RunStatus.FAILED -> styledH3 {
            css {
                fontSize = Config.fontSize
                textAlign = TextAlign.center
            }
            +"Something went wrong"
        }
    }
}

fun RBuilder.repository(handler: RepositoryProps.() -> Unit) = child(Repository) {
    attrs {
        handler()
    }
}
