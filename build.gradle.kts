val beamVersion = "2.23.0"
val log4jVersion = "2.13.3"

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

  // Logging
  implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

  // Tests
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  mainClassName = "at.hannesmoser.gleam.AppKt"
}
