package util

data class ParsedData(
    var timestamp: String,
    var treeData: List<Pair<TreeInfo, String>>
)

data class DataInfo(
    var runStatus: RunStatus,
    var data: ParsedData?
)
