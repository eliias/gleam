val beamVersion = "2.33.0"
val beamFlinkVersion = "1.13"
val flinkVersion = "1.13.1"
val log4jVersion = "2.12.1"
val slf4jVersion = "1.7.15"

plugins {
  kotlin("jvm") version "1.5.31"

  id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
  mavenCentral()
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  // Beam
  //  implementation("org.apache.beam:beam-runners-direct-java:$beamVersion")
  implementation("org.apache.beam:beam-runners-direct-java:$beamVersion")
  implementation("org.apache.beam:beam-runners-flink-$beamFlinkVersion:$beamVersion")
  implementation("org.apache.beam:beam-runners-flink-$beamFlinkVersion-job-server:$beamVersion")
  implementation("org.apache.beam:beam-sdks-java-bom:$beamVersion")

  // Flink
  implementation("org.apache.flink:flink-streaming-java_2.11:$flinkVersion")
  implementation("org.apache.flink:flink-connector-kafka_2.11:$flinkVersion")

  // Logging
  implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
  implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:${log4jVersion}")
  implementation("org.slf4j:slf4j-log4j12:${slf4jVersion}")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

configurations {
  s
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.javaParameters = true
  }

  register<JavaExec>("execute") {
    mainClass.set("at.hannesmoser.gleam.App")
    classpath = sourceSets["main"].runtimeClasspath
    args(
      "--runner=FlinkRunner",
      "--flinkMaster=flink.conc.at",
      "--filesToStage=build/libs/gleam-0.0.1-SNAPSHOT-all.jar"
    )
  }

  shadowJar {
    mergeServiceFiles()
    archiveBaseName.set("gleam")
    manifest.attributes["Main-Class"] = "at.hannesmoser.gleam.App"
    manifest.attributes["Multi-Release"] = "true"
    isZip64 = true
//    minimize()
  }
}
