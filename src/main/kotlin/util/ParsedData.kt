package util

data class ParsedData(
    var timestamp: String,
    var treeData: List<Pair<TreeInfo, String>>
)
