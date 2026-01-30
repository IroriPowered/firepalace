plugins {
    alias(libs.plugins.shadow)
}

base {
    archivesName.set("firepalace-common")
}

dependencies {
    compileOnly(project(":api"))

    compileOnly(libs.hytale)

    implementation(libs.jedis)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    withType<Checkstyle>().configureEach {
        // Checkstyle throws parsing errors for this file
        exclude("**/DownstreamStatusPacket.java")
    }
}
