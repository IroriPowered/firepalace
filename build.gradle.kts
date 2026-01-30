plugins {
    id("cc.irori.firepalace.build.java") apply false
    alias(libs.plugins.shadow) apply false
}

/* ===== Project Properties ===== */
val modGroup    = project.property("mod_group")     as String
val modVersion  = project.property("mod_version")   as String
/* ============================== */

allprojects {
    group = modGroup
    version = modVersion
}

subprojects {
    apply(plugin = "cc.irori.firepalace.build.java")

    repositories {
        mavenCentral()
        maven("https://maven.hytale.com/release")
        maven("https://maven.hytale.com/pre-release")
    }
}
