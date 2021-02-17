plugins {
    id("java")
    id("io.vertx.vertx-plugin") version "1.2.0"
    idea
}

// https://docs.gradle.org/current/userguide/idea_plugin.html
idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

val projectVertxVersion = "4.0.2"

group = "com.gerardnico"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("io.vertx:vertx-core:$projectVertxVersion")
    implementation("io.vertx:vertx-web:$projectVertxVersion")
    implementation("io.vertx:vertx-web-client:$projectVertxVersion")
}

vertx {
    mainVerticle = "com.gerardnico.web.Server"
    vertxVersion = "$projectVertxVersion"
}

tasks.withType<Test> {
    useJUnitPlatform()
}