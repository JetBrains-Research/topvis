package util

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlin.math.min

data class FileData(val path: String, val topics: Array<String>, val probs: Array<Double>)

data class RepoData(val path: String, val files: Array<FileData>)

data class Response(val data: Array<RepoData>)

suspend fun getGitTree(): Pair<List<Pair<TreeInfo, String>>?, Boolean> {
    val result =
        window.fetch("topics.json").await().text().await()
    val json = JSON.parse<Response>(result)
    console.log("Result from Sosed received")
    val treeInfoList = json.data.map { data ->
        val folderInfo = buildFileTree(data)
        console.log("Folder tree build done")
        val treeInfo = buildTreeInfo(folderInfo)
        console.log("Folder tree transformation done")
        Pair(treeInfo, data.path)
    }.toList()

    return Pair(treeInfoList, true)
}

fun buildFileTree(json: RepoData): FolderInfo {
    val globalRoot = FolderInfo(
        path = "",
        isRoot = true,
        parent = null,
        depth = 0,
    )
    var root = globalRoot
    for (data in json.files) {
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
    val name = (if (folderInfo.parent == null) "/" else folderInfo.path.removePrefix(folderInfo.parent!!.path + "/"))
    return if (folderInfo.children.isEmpty()) {
        TreeInfo(
            type = "file",
            name = name,
            extra = "($topics)",
            files = null
        )
    } else {
        val children = mutableListOf<TreeInfo>()
        for (child in folderInfo.children) {
            children.add(buildTreeInfo(child))
        }
        TreeInfo(
            type = "directory",
            name = name,
            extra = "($topics)",
            files = children.toTypedArray()
        )
    }
}

