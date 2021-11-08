package components

import util.TreeInfo
import imports.folderTree
import kotlinx.coroutines.*
import react.*
import react.Props
import react.dom.*
import util.*

external interface AppState : State {
    var runStatus: Int
    var data: TreeInfo
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
        h1 {
            +"Repo tree visualizer"
        }
        div {
            repository {
                isRunMade = state.runStatus
            }
            if (state.runStatus == 1) {
                folderTree {
                    attrs.data = state.data
                    attrs.initCheckedStatus = "custom"
                    attrs.initOpenStatus = "closed"
                    attrs.showCheckbox = false
                    attrs.readOnly = true
                }
            }
        }
    }
}
