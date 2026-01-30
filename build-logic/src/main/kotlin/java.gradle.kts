package cc.irori.firepalace.build

plugins {
    java
    checkstyle
}

/* ===== Project Properties ===== */
val javaVersion = project.property("java_version")  as String
/* ============================== */

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
    }
}

checkstyle {
    toolVersion = "13.0.0"
    configProperties["org.checkstyle.google.suppressionfilter.config"] = file("${rootDir}/config/checkstyle/checkstyle-suppressions.xml")
}

tasks {
    compileJava {
        options.release.set(javaVersion.toInt())
        options.encoding = "UTF-8"
    }
}
