package util

data class FolderInfo(
    var path: String,
    var isRoot: Boolean,
    var depth: Int,
    var parent: FolderInfo?,
    var children: MutableList<FolderInfo> = mutableListOf(),
    var topicsList: List<Pair<String, Double>> = listOf()
)
