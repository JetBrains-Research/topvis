job("Generate and publish sites") {
    container("Generate public site", "openjdk:11") {
        shellScript {
            interpreter = "/bin/bash"
            content = """
                ./generate-public-site.sh
                cp -r site $mountDir/share
            """.trimIndent()
        }
    }

    container("Publish public site", "openjdk:11") {
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