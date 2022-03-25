val beamVersion = "2.43.0"
val bigtableBeamVersion = "2.6.5"
val fakerVersion = "1.13.0"
val jacksonVersion = "2.14.1"
val junitJupiterVersion = "5.9.1"
val koinVersion = "3.3.2"
val log4jVersion = "2.19.0"
val slf4jVersion = "2.0.6"

plugins {
  kotlin("jvm")
  jacoco
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

  // Dependency Injection
  api("io.insert-koin:koin-core:$koinVersion")
  testImplementation("io.insert-koin:koin-test:$koinVersion")
  testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")

  // Faker
  implementation("io.github.serpro69:kotlin-faker:$fakerVersion")

  // Logging
  api("org.apache.logging.log4j:log4j-api:$log4jVersion")
  api("org.apache.logging.log4j:log4j-core:$log4jVersion")
  api("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
  runtimeOnly("org.slf4j:slf4j-jdk14:$slf4jVersion")

  // Serialization
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")

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
