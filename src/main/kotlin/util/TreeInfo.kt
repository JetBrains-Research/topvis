package util

data class TreeInfo(
    val type: String,
    val name: String,
    val extra: String,
    val files: Array<TreeInfo>?
)