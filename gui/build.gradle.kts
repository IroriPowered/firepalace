repositories {
    maven("https://maven.irori.cc/repository/public")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":common"))
    compileOnly(project(":manager"))

    compileOnly(libs.hytale)

    compileOnly(libs.shodo)
}

base {
    archivesName.set("firepalace-gui")
}
