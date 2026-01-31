plugins {
    `maven-publish`
}

base {
    archivesName.set("firepalace-api")
}

dependencies {
    compileOnly(libs.hytale)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "IroriPoweredMaven"
            url = uri("https://maven.irori.cc/repository/public/")
            credentials {
                username = project.findProperty("irori_maven_username")?.toString() ?: ""
                password = project.findProperty("irori_maven_password")?.toString() ?: ""
            }
        }
    }
}
