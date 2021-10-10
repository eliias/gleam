val beamVersion = "2.33.0"
val flinkVersion = "1.13"

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
  implementation("org.apache.beam:beam-runners-flink-$flinkVersion:$beamVersion")
  implementation("org.apache.beam:beam-runners-flink-$flinkVersion-job-server:$beamVersion")
  implementation("org.apache.beam:beam-sdks-java-bom:$beamVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
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
