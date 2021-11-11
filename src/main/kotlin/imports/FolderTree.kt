@file:JsModule("react-folder-tree")
@file:JsNonModule

package imports

import react.*
import util.TreeInfo

@JsName("default")
external val folderTree: ComponentClass<FolderTreeProps>

external interface FolderTreeProps : Props {
    var data: TreeInfo
    var initCheckedStatus: String
    var initOpenStatus: String
    var showCheckbox: Boolean?
    var readOnly: Boolean?
}