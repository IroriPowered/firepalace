plugins {
    alias(libs.plugins.shadow)
}

base {
    archivesName.set("firepalace-common")
}

dependencies {
    implementation(project(":api"))

    compileOnly(libs.hytale)

    implementation(libs.jedis)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        relocate("org.apache", "cc.irori.firepalace.libs.org.apache")
        relocate("org.json", "cc.irori.firepalace.libs.org.json")
        relocate("org.slf4j", "cc.irori.firepalace.libs.org.slf4j")
        relocate("com.google.errorprone", "cc.irori.firepalace.libs.com.google.errorprone")
        relocate("com.google.gson", "cc.irori.firepalace.libs.com.google.gson")
        relocate("redis.clients.authentication", "cc.irori.firepalace.libs.redis.clients.authentication")
        relocate("redis.clients.jedis", "cc.irori.firepalace.libs.redis.clients.jedis")
    }

    withType<Checkstyle>().configureEach {
        // Checkstyle throws parsing errors for this file
        exclude("**/DownstreamStatusPacket.java")
    }
}
