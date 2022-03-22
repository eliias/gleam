import io.gitlab.arturbosch.detekt.Detekt

plugins {
  kotlin("jvm") version "1.6.10"
  jacoco
  id("io.gitlab.arturbosch.detekt") version "1.19.0"
  id("com.adarshr.test-logger") version "3.2.0"
}

dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")
}

repositories {
  mavenCentral()
  maven("https://packages.confluent.io/maven/")
}

detekt {
  parallel = true
  buildUponDefaultConfig = true
  config = files("$rootDir/config/detekt/detekt.yml")
}

subprojects {
  apply {
    plugin("io.gitlab.arturbosch.detekt")
    tasks {
      withType<Detekt>().configureEach {
        reports {
          xml.required.set(true)
          html.required.set(true)
          txt.required.set(false)
          sarif.required.set(false)
        }
      }
    }
  }
}
