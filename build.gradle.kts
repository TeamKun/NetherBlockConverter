plugins {
    kotlin("jvm") version "1.5.20"
}

group = "net.kunmc.lab"
version = "1.4"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    implementation("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filter { it.replace("<@version@>", project.version.toString()) }
        }
    }

    jar {
        from(configurations.compileOnly.get().map { if (it.isDirectory) it else zipTree(it) })
    }

    create("package") {
        dependsOn(jar)
        doLast {
            copy {
                from(jar)
                into(file("./output"))
            }
        }
    }

    create<Copy>("buildPlugin") {
        group = "plugin"
        from(jar)
        val dest = File(projectDir, "server/plugins")
        if (File(dest, jar.get().archiveFileName.get()).exists()) dest.delete()
        into(dest)
    }

    create<DefaultTask>("setupWorkspace") {
        group = "plugin"
        doLast {
            val paperDir = File(projectDir, "server")
            paperDir.mkdirs()
            val download by registering(de.undercouch.gradle.tasks.download.Download::class) {
                src("https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/585/downloads/paper-1.16.5-585.jar")
                dest(paperDir)
            }
            val paper = download.get().outputFiles.first()
            download.get().download()

            runCatching {
                javaexec {
                    workingDir(paperDir)
                    main = "-jar"
                    args("./${paper.name}", "nogui")
                }
                val eula = File(paperDir, "eula.txt")
                eula.writeText(eula.readText(Charsets.UTF_8).replace("eula=false", "eula=true"), Charsets.UTF_8)
                val serverProperties = File(paperDir, "server.properties")
                serverProperties.writeText(
                        serverProperties.readText(Charsets.UTF_8)
                                .replace("online-mode=true", "online-mode=false")
                                .replace("difficulty=easy", "difficulty=peaceful")
                                .replace("spawn-protection=16", "spawn-protection=0")
                                .replace("gamemode=survival", "gamemode=creative")
                                .replace("level-name=world", "level-name=dev_world")
                                //.replace("level-type=default", "level-type=flat")
                                .replace("max-tick-time=60000", "max-tick-time=-1")
                                .replace("view-distance=10", "view-distance=16"), Charsets.UTF_8
                )
                val runBat = File(paperDir, "start.sh")
                if (!runBat.exists()) {
                    runBat.createNewFile()
                    runBat.writeText("java -jar ./${paper.name} nogui", Charsets.UTF_8)
                }
            }.onFailure {
                    it.printStackTrace()
            }
        }
    }
}
