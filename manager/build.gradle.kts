plugins {
    alias(libs.plugins.shadow)
}

base {
    archivesName.set("firepalace-manager")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":common"))

    compileOnly(libs.hytale)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}