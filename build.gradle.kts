import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val beamVersion = "2.28.0"
val fakerVersion = "1.5.0"
val jacksonVersion = "2.11.3"
val log4jVersion = "2.13.3"
val ktorVersion = "1.4.1"
val hamcrestVersion = "2.2"

plugins {
  jacoco
  application

  id("com.adarshr.test-logger") version "2.0.0"
  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("org.jetbrains.kotlin.jvm") version "1.4.10"

  kotlin("plugin.serialization") version "1.4.10"
}

repositories {
  mavenCentral()
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  // Kotlin
  implementation(kotlin("reflect"))
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.0.0")

  // Beam
  implementation(platform("org.apache.beam:beam-sdks-java-bom:$beamVersion"))
//  implementation("org.apache.beam:beam-runners-direct-java:$beamVersion")
  implementation("org.apache.beam:beam-runners-flink-1.12-job-server:$beamVersion")
//  implementation("org.apache.beam:beam-sdks-java-extensions-sql:$beamVersion")

  // ktor
//  implementation("io.ktor:ktor-server-core:$ktorVersion")
//  implementation("io.ktor:ktor-server-netty:$ktorVersion")
//  implementation("io.ktor:ktor-websockets:$ktorVersion")

  // Logging
  implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

  // Persistence
//  implementation("com.google.cloud.bigtable:bigtable-hbase-beam:1.15.0")

  // Serialization
//  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

  // Tests
//  testImplementation("org.jetbrains.kotlin:kotlin-test")
//  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
//  testImplementation("org.hamcrest:hamcrest:$hamcrestVersion")
//  implementation("io.github.serpro69:kotlin-faker:$fakerVersion")
}

configurations {
  all {
    exclude("org.slf4j", "slf4j-log4j12")
  }
}

application {
  mainClass.set("at.hannesmoser.gleam.App")
}

tasks {
  runShadow {
    args(
      "--runner=FlinkRunner",
      "--flinkMaster=localhost:8081",
//      "--flinkMaster=flink.conc.at:80",
      "--fasterCopy",
//      "--streaming"
    )
  }

  shadowJar {
    isZip64 = true
    mergeServiceFiles()
//    minimize()
  }
}

val compileKotlin: KotlinCompile by tasks
