import components.App
import kotlinx.browser.document
import react.dom.*

fun main() {
    kotlinext.js.require("react-folder-tree/dist/style.css")
    render(document.getElementById("root")) {
        child(App::class) {}
    }
}