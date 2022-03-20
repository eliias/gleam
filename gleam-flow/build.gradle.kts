val beamVersion = "2.37.0"
val fakerVersion = "1.10.0"
val jacksonVersion = "2.13.1"
val log4jVersion = "2.17.1"
val ktorVersion = "1.4.1"

plugins {
  jacoco
  kotlin("jvm")
}

repositories {
  mavenCentral()
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  implementation(project(":gleam-core"))
}

configurations {
  all {
    exclude("org.slf4j", "slf4j-log4j12")
  }
}
