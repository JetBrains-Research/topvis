job("Generate and publish public site") {
    startOn {
        gitPush {
            branchFilter {
                +"refs/heads/tree-visualization"
                +"refs/heads/main"
            }
        }
    }
    container("Generate public site", "openkbs/jre-mvn-py3") {
        shellScript {
            interpreter = "/bin/bash"
            content = """
                ./topics-public.sh input/input.txt
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

job("Generate and publish internal site") {
    startOn {
        gitPush {
            enabled = false
        }
    }
    
    container("Generate internal site", "openkbs/jre-mvn-py3") {
        env["USER"] = Secrets("git-user")
        env["TOKEN"] = Secrets("git-token")
        shellScript {
            interpreter = "/bin/bash"
            content = """
                echo "tcd/loader-sample" >> input/input-internal.txt
                ./topics-internal.sh input/input-internal.txt ${'$'}TOKEN
                mkdir $mountDir/share/site
                cp -r site/internal $mountDir/share/site
            """.trimIndent()
        }
    }

    container("Publish internal site", "openkbs/jre-mvn-py3") {
        kotlinScript { api ->
            val siteSource = "$mountDir/share/site/internal"
            val sourceDir = java.io.File(siteSource)
            if (!sourceDir.isDirectory || sourceDir.list().isEmpty()) {
                error("Directory $siteSource is empty")
            }
            api.space().experimentalApi.hosting.publishSite(
                siteSource,
                "topvis",
                HostingSiteSettings(public = false)
            )
        }
    }
}
