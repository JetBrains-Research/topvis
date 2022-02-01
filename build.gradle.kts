plugins {
    id("org.jetbrains.kotlin.js") version "1.5.31"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    mavenCentral()
}

kotlin {
    js {
        browser {
            compilations.all {
                compileKotlinTask.kotlinOptions.freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
            }
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        useCommonJs()
        nodejs()
        binaries.executable()
    }
}
rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class.java) {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().versions.webpackCli.version =
        "4.9.0"
}
fun kotlinw(target: String): String =
    "org.jetbrains.kotlin-wrappers:kotlin-$target"

dependencies {
    implementation(enforcedPlatform(kotlinw("wrappers-bom:0.0.1-pre.264-kotlin-1.5.31")))

    implementation(kotlinw("react"))
    implementation(kotlinw("react-dom"))
    implementation(kotlinw("css"))
    implementation(kotlinw("styled"))

    implementation(npm("core-js", "3.17.3"))
    implementation(npm("@babel/core", "7.16.0"))
    implementation(npm("@babel/runtime", "7.16.0"))

    implementation(npm("react", "17.0.2"))
    implementation(npm("react-dom", "17.0.2"))

    implementation(npm("@salkaevruslan/react", "2.2.2"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
}
