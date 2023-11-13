plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.12.0"
}

group = "com.hhh.plugin"
version = "3.1-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("java","maven","gradle"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

}
dependencies {
    implementation ("com.github.javaparser:javaparser-core:3.25.3")
    implementation ("com.github.javaparser:javaparser-symbol-solver-core:3.25.3")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("org.projectlombok:lombok:1.18.28")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation ("com.knuddels:jtokkit:0.4.0")
    implementation(group = "org.apache.maven.plugins", name = "maven-plugin-plugin", version = "3.8.1")
    implementation(group = "org.apache.maven.plugins", name = "maven-surefire-plugin", version = "3.1.0")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    implementation ("org.apache.maven.shared:maven-dependency-tree:3.0.1")
    implementation ("org.apache.maven:maven-core:3.0")

    implementation ("org.apache.maven:maven-plugin-api:3.9.1")
    implementation ("org.apache.maven.plugin-tools:maven-plugin-annotations:3.8.1")

    implementation ("org.junit.platform:junit-platform-launcher:1.5.2")
    implementation ("org.dom4j:dom4j:2.1.3")

}
