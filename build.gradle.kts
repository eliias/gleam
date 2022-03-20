plugins {
  jacoco
  kotlin("jvm") version "1.6.10"
  id("com.adarshr.test-logger") version "3.2.0"
}

repositories {
  mavenCentral()
  maven("https://packages.confluent.io/maven/")
}
