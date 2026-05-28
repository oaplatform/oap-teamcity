allprojects {
    repositories {
        maven { url = uri("https://artifacts.oaplatform.org/repository/oap-maven/") }
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "checkstyle")
    apply(plugin = "maven-publish")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf(
            "-parameters",
            "-Xlint:unchecked",
            "--add-modules", "java.xml,java.compiler"
        ))
    }

    configure<org.gradle.api.plugins.quality.CheckstyleExtension> {
        toolVersion = "10.24.0"
        config = resources.text.fromUri("https://raw.githubusercontent.com/oaplatform/oap-maven/master/.idea/checkstyle.xml")
    }

    tasks.withType<Test>().configureEach {
        useTestNG()
        jvmArgs(
            "-ea",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.math=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.util.stream=ALL-UNNAMED",
            "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
            "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
            "--add-opens=java.base/java.net=ALL-UNNAMED",
            "--add-opens=java.base/java.text=ALL-UNNAMED",
            "--add-opens=java.base/java.io=ALL-UNNAMED",
            "--add-opens=java.base/java.nio=ALL-UNNAMED",
            "--add-opens=java.sql/java.sql=ALL-UNNAMED"
        )
        exclude("**/*Performance*", "**/*Perf*")
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
        repositories {
            maven {
                url = uri("https://artifacts.oaplatform.org/repository/oap-maven/")
                credentials {
                    username = project.findProperty("oap.repository.user") as String?
                        ?: System.getenv("OAP_REPOSITORY_USER")
                    password = project.findProperty("oap.repository.password") as String?
                        ?: System.getenv("OAP_REPOSITORY_PASSWORD")
                }
            }
        }
    }

    dependencies {
        "compileOnly"("org.projectlombok:lombok:1.18.42")
        "annotationProcessor"("org.projectlombok:lombok:1.18.42")
    }
}
