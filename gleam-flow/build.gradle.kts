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
