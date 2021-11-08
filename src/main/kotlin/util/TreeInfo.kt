package util

data class TreeInfo(
    val name: String,
    val isOpen: Boolean?,
    val children: Array<TreeInfo>?
)