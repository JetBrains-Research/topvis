package components

import imports.collapse
import imports.cssBaseline
import util.TreeInfo
import imports.geistProvider
import imports.tree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.fontSize
import kotlinx.css.px
import react.*
import react.Props
import react.dom.*
import styled.css
import styled.styledH1
import util.*

external interface AppState : State {
    var runStatus: Int
    var data: List<Pair<TreeInfo, String>>
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class App : RComponent<Props, AppState>() {
    init {
        this.state.runStatus = 2
        MainScope().launch(Dispatchers.Default) {
            val result = getGitTree()
            console.log("Now updating")
            setState {
                if (result.second) {
                    data = result.first!!
                    runStatus = 1
                } else {
                    runStatus = 0
                }
            }
        }
    }

    override fun RBuilder.render() {
        geistProvider {
            cssBaseline {}
            styledH1 {
                css {
                    fontSize = 24.px
                }
                +"Repo tree visualizer"
            }
            div {
                repository {
                    isRunMade = state.runStatus
                }
                if (state.runStatus == 1) {
                    for (info in state.data) {
                        collapse {
                            attrs.title = info.second
                            tree {
                                attrs.value = info.first.files!!
                            }
                        }
                    }
                }
                if (state.runStatus == 0) {
                    h1 {
                        +"Something went wrong"
                    }
                }
            }
        }
    }
}
