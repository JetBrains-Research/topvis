package components

import config.Config
import kotlinx.css.TextAlign
import kotlinx.css.fontSize
import kotlinx.css.textAlign
import react.*
import styled.css
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
        RunStatus.OK -> styledH3 {
            css {
                fontSize = Config.fontSize
                textAlign = TextAlign.center
            }
            +"Repository trees:"
        }
        RunStatus.FAILED -> styledH3 {
            css {
                fontSize = Config.fontSize
                textAlign = TextAlign.center
            }
            +"Repositories not found"
        }
    }
}

fun RBuilder.repository(handler: RepositoryProps.() -> Unit) = child(Repository) {
    attrs {
        handler()
    }
}
