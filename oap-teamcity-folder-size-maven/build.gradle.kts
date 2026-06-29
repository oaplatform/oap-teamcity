dependencies {
    implementation(project(":oap-teamcity"))
    implementation("org.apache.maven:maven-plugin-api:3.9.11")
    compileOnly("org.apache.maven.plugin-tools:maven-plugin-annotations:3.15.2")
}

configure<PublishingExtension> {
    publications {
        named<MavenPublication>("maven") {
            pom {
                packaging = "maven-plugin"
            }
        }
    }
}
