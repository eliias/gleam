val fakerVersion = "1.13.0"

plugins {
  jacoco
  kotlin("jvm")
}

repositories {
  mavenCentral()
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  // Gleam
  implementation(project(":gleam-core"))
  implementation(project(":gleam-flow"))

  // Tests
  implementation("io.github.serpro69:kotlin-faker:$fakerVersion")
}

configurations {
  all {
    exclude("org.slf4j", "slf4j-log4j12")
  }
}
