plugins {
    kotlin("jvm") version "1.9.22"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("dnsjava:dnsjava:3.6.1")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.16")
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    testImplementation("ch.qos.logback:logback-classic:1.5.7")


    testImplementation("org.jetbrains.kotlin:kotlin-test")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}