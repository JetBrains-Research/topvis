package util

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlin.math.min

data class Data(val path: String, val topics: Array<String>, val probs: Array<Double>)

data class Response(val data: Array<Data>)

suspend fun getGitTree(): Pair<TreeInfo?, Boolean> {
    val result =
        window.fetch("./topics.txt").await().text().await()
    val json = JSON.parse<Response>(result)
    console.log("Result from Sosed received")
    val folderInfo = buildFileTree(json)
    console.log("Folder tree build done")
    val treeInfo = buildTreeInfo(folderInfo)
    console.log("Folder tree transformation done")
    return Pair(treeInfo, true)
}

fun buildFileTree(json: Response): FolderInfo {
    val globalRoot = FolderInfo(
        path = "",
        isRoot = true,
        parent = null,
        depth = 0,
    )
    var root = globalRoot
    for (data in json.data) {
        val file = data.path
        if (file == "/") {
            globalRoot.topicsList = data.topics.zip(data.probs).toList()
            continue
        }
        while (!file.startsWith("${root.path}${if (root.isRoot) "" else "/"}")) {
            if (root.parent != null) {
                root = root.parent!!
            }
        }
        val info = FolderInfo(
            path = file,
            isRoot = false,
            parent = root,
            depth = root.depth + 1,
            topicsList = data.topics.zip(data.probs).toList()
        )
        console.log(file + " " + root.path)
        root.children.add(info)
        root = info
    }
    return globalRoot
}

fun buildTreeInfo(folderInfo: FolderInfo): TreeInfo {
    var topics = ""
    var isFirst = true
    for (i in 0 until min(3, folderInfo.topicsList.size)) {
        if (isFirst) {
            isFirst = false
        } else {
            topics += ", "
        }
        topics += folderInfo.topicsList[i].first
    }
    if (topics == "") {
        topics = "No topics found"
    }
    val name = (if (folderInfo.parent == null) "/" else folderInfo.path.removePrefix(folderInfo.parent!!.path + "/")) +
            " ($topics)"
    val isOpen = folderInfo.isRoot || (folderInfo.parent != null && folderInfo.parent!!.isRoot)
    return if (folderInfo.children.isEmpty()) {
        TreeInfo(
            name = name,
            isOpen = isOpen,
            children = null
        )
    } else {
        val children = mutableListOf<TreeInfo>()
        for (child in folderInfo.children) {
            children.add(buildTreeInfo(child))
        }
        TreeInfo(
            name = name,
            isOpen = isOpen,
            children = children.toTypedArray()
        )
    }
}

