import components.App
import kotlinx.browser.document
import react.dom.*

fun main() {
    render(document.getElementById("root")) {
        child(App::class) {}
    }
}