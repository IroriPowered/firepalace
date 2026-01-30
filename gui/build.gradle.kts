dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":common"))
    compileOnly(project(":manager"))

    compileOnly(libs.hytale)
}

base {
    archivesName.set("firepalace-gui")
}
