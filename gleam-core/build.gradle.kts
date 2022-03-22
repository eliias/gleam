val beamVersion = "2.37.0"
val bigtableBeamVersion = "2.0.0"
val fakerVersion = "1.10.0"
val jacksonVersion = "2.13.1"
val junitJupiterVersion = "5.8.2"
val log4jVersion = "2.17.1"

plugins {
  jacoco
  kotlin("jvm")
}

repositories {
  mavenCentral()
}

dependencies {
  // Kotlin
  api(kotlin("reflect"))
  api(platform("org.jetbrains.kotlin:kotlin-bom"))
  api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  // Beam
  api(platform("org.apache.beam:beam-sdks-java-core:$beamVersion"))
  api("org.apache.beam:beam-sdks-java-extensions-sql:$beamVersion")
  api("org.apache.beam:beam-runners-direct-java:$beamVersion")
  api("com.google.cloud.bigtable:bigtable-hbase-beam:$bigtableBeamVersion")

  // Faker
  implementation("io.github.serpro69:kotlin-faker:$fakerVersion")

  // Logging
  api("org.apache.logging.log4j:log4j-api:$log4jVersion")
  api("org.apache.logging.log4j:log4j-core:$log4jVersion")
  api("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

  // Serialization
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

  // Tests
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

configurations {
  all {
    exclude("org.slf4j", "slf4j-log4j12")
  }
}

tasks {
  test {
    useJUnitPlatform()
  }
}
