package at.hannesmoser.gleam.resource

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal object WorkerApplication {
  private lateinit var container: KoinApplication
  private const val configPath = "config"
  private const val defaultConfigFile = "$configPath/default.yml"
  private val environmentConfigFiles = mapOf(
    "development" to "$configPath/environments/development.yml",
    "staging" to "$configPath/environments/staging.yml",
    "production" to "$configPath/environments/production.yml",
  )

  fun getInstance() = if (::container.isInitialized) {
    container
  } else {
    container = koinApplication {
      modules(initializeModules())
    }
    container
  }

  private fun initializeModules(): List<Module> {
    val config = parse()

    val applicationModule = module {
      config.resources?.forEach { it.value.register(id, this) }
    }

    return listOf(
      applicationModule
    )
  }

  @Suppress("ThrowsCount")
  private fun parse(): WorkerConfiguration {
    val defaultUrl = javaClass.classLoader.getResource(defaultConfigFile)
      ?: throw IllegalStateException("Missing default config file: $defaultConfigFile")

    val environmentUrl =
      javaClass.classLoader.getResource(environmentConfigFiles["development"])
        ?: throw IllegalStateException("Missing environment config file: ${environmentConfigFiles["development"]}")

    val mapper = ObjectMapper(YAMLFactory())
    mapper
      .registerModule(
        KotlinModule.Builder()
          .build()
      )
      .registerModule(
        ResourceModule("at.hannesmoser.gleam.resource.resources")
      )
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE

    return try {
      val config = mapper.readValue(defaultUrl, WorkerConfiguration::class.java)
      val reader = mapper.readerForUpdating(config)
      reader.readValue(environmentUrl)
    } catch (exception: MissingKotlinParameterException) {
      throw IllegalStateException("Could not read config YAML file!", exception)
    }
  }
}
