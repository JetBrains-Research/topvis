package components

import config.Config
import imports.collapse
import imports.cssBaseline
import util.TreeInfo
import imports.geistProvider
import imports.tree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import react.*
import react.Props
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledH1
import util.*

external interface AppState : State {
    var runStatus: RunStatus
    var data: List<Pair<TreeInfo, String>>
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class App : RComponent<Props, AppState>() {
    init {
        this.state.runStatus = RunStatus.IN_PROCESS
        MainScope().launch(Dispatchers.Default) {
            val result = getGitTree()
            console.log("Now updating")
            setState {
                if (result.second) {
                    data = result.first!!
                    runStatus = RunStatus.OK
                } else {
                    runStatus = RunStatus.FAILED
                }
            }
        }
    }

    override fun RBuilder.render() {
        geistProvider {
            cssBaseline {}
            styledH1 {
                css {
                    fontSize = Config.fontSize
                    textAlign = TextAlign.center
                }
                +"Repo tree visualizer"
            }
            repository {
                isRunMade = state.runStatus
            }
            styledDiv {
                css {
                    marginLeft =  Config.globalLeftMargin
                }
                if (state.runStatus == RunStatus.OK) {
                    for (info in state.data) {
                        collapse {
                            attrs.title = info.second
                            styledDiv {
                                css {
                                    marginLeft =  Config.treeLeftMargin
                                }
                                tree {
                                    attrs.value = info.first.files!!
                                }
                            }
                        }
                    }
                }
                if (state.runStatus == RunStatus.FAILED) {
                    h1 {
                        +"Something went wrong"
                    }
                }
            }
        }
    }
}
