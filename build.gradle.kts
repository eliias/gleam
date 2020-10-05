val beamVersion = "2.24.0"
val fakerVersion = "1.5.0"
val jacksonVersion = "2.11.3"
val log4jVersion = "2.13.3"
val ktorVersion = "1.4.1"

plugins {
  jacoco

  id("com.adarshr.test-logger") version "2.0.0"
  id("com.github.johnrengelman.shadow") version "5.2.0"
  id("org.jetbrains.kotlin.jvm") version "1.4.0"
  id("org.jlleitschuh.gradle.ktlint") version "9.2.1"

  application
}

repositories {
  maven("https://packages.confluent.io/maven/")
  jcenter()
}

dependencies {
  // Kotlin
  implementation(kotlin("reflect"))
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  // Beam
  implementation("org.apache.beam:beam-runners-direct-java:$beamVersion")
  implementation("org.apache.beam:beam-sdks-java-extensions-sql:$beamVersion")

  // ktor
  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-netty:$ktorVersion")
  implementation("io.ktor:ktor-websockets:$ktorVersion")

  // Logging
  implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

  // Serialization
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

  // Tests
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
  implementation("io.github.serpro69:kotlin-faker:$fakerVersion")
}

application {
  mainClassName = "at.hannesmoser.gleam.AppKt"
}
