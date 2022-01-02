package components

import config.Config
import imports.*
import kotlinx.coroutines.*
import kotlinx.css.*
import react.*
import react.Props
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledH1
import util.*

external interface AppState : State {
    var filesReadStatus: RunStatus
    var data: MutableMap<String, DataInfo>
}

@OptIn(ExperimentalJsExport::class)
@JsExport
class App : RComponent<Props, AppState>() {
    init {
        this.state.filesReadStatus = RunStatus.IN_PROCESS
        MainScope().launch(Dispatchers.Default) {
            val files = getTreeSourcesList()
            setState {
                data = mutableMapOf()
                for (file in files) {
                    data[file] = DataInfo(
                        runStatus = RunStatus.IN_PROCESS,
                        data = null
                    )
                }
                filesReadStatus = RunStatus.OK
            }
            buildTreeData(files.first())
        }

    }

    private fun buildTreeData(value: String) {
        MainScope().launch(Dispatchers.Default) {
            if (state.data[value]?.runStatus == RunStatus.IN_PROCESS) {
                console.log("Loading $value")
                val result = getGitTree(value)
                console.log("Now updating $value")
                setState {
                    if (result.second) {
                        data[value]?.data = result.first!!
                        data[value]?.runStatus = RunStatus.OK
                    } else {
                        data[value]?.runStatus = RunStatus.FAILED
                    }
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
            styledDiv {
                css {
                    marginLeft = Config.globalLeftMargin
                    marginRight = Config.globalRightMargin
                }

                if (state.filesReadStatus == RunStatus.OK) {
                    tabs {
                        attrs.initialValue = state.data.keys.first()
                        attrs.onChange = { value -> buildTreeData(value) }
                        for (file in state.data.keys) {
                            tabItem {
                                attrs.value = file
                                attrs.label = file

                                repository {
                                    isRunMade = state.data[file]?.runStatus ?: RunStatus.IN_PROCESS
                                }

                                if (state.data[file]?.runStatus == RunStatus.OK) {
                                    val data = state.data[file]?.data!!
                                    for (info in data.treeData) {
                                        collapse {
                                            attrs.title = info.second
                                            styledDiv {
                                                css {
                                                    marginLeft = Config.treeLeftMargin
                                                }
                                                tree {
                                                    attrs.value = info.first.files!!
                                                }
                                            }
                                        }
                                    }
                                    styledDiv {
                                        css {
                                            position = Position.fixed
                                            bottom = 0.px
                                            left = 0.px
                                            right = 0.px
                                            textAlign = TextAlign.center
                                        }
                                        +"UTC timestamp: ${data.timestamp}"
                                    }
                                }
                            }
                        }
                    }
                }
                if (state.filesReadStatus == RunStatus.IN_PROCESS) {
                    h1 {
                        +"Reading files..."
                    }
                }
                if (state.filesReadStatus == RunStatus.FAILED) {
                    h1 {
                        +"Something went wrong"
                    }
                }
            }
        }
    }
}
