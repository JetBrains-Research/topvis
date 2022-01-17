job("Generate and publish sites") {
    container("Generate public site", "openkbs/jdk11-mvn-py3") {
        env["NPMAUTH"] = Secrets("npm-auth-line")
        shellScript {
            interpreter = "/bin/bash"
            content = """
                echo ${'$'}NPMAUTH >> .npmrc
                ./topics.sh input/input.txt
                cp -r site $mountDir/share
            """.trimIndent()
        }
    }

    container("Publish public site", "openkbs/jre-mvn-py3") {
        kotlinScript { api ->
            val siteSource = "$mountDir/share/site/public"
            val sourceDir = java.io.File(siteSource)
            if (!sourceDir.isDirectory || sourceDir.list().isEmpty()) {
                error("Directory $siteSource is empty")
            }
            api.space().experimentalApi.hosting.publishSite(
                siteSource,
                "topic-vis-demo-public",
                HostingSiteSettings(public = true)
            )
        }
    }
}
