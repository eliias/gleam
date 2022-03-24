package at.hannesmoser.gleam.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal object WorkerApplication {
  private lateinit var container: KoinApplication
  private const val configFile = "config.yml"

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
      config.resources.forEach { it.register(id, this) }
    }

    return listOf(
      applicationModule
    )
  }

  private fun parse(): WorkerConfiguration {
    val url = javaClass.classLoader.getResource(configFile)
      ?: return WorkerConfiguration(resources = listOf())

    val mapper = ObjectMapper(YAMLFactory())
    mapper
      .findAndRegisterModules()
      .registerModule(
        ResourceModule("at.hannesmoser.gleam.resource.resources")
      )
      .propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE

    return try {
      val value = url.readText(Charsets.UTF_8)
      mapper.readValue(value, WorkerConfiguration::class.java)
    } catch (exception: MissingKotlinParameterException) {
      throw IllegalStateException("Could not read config YAML file!", exception)
    }
  }

//  @Suppress("ReturnCount")
//  private class ResolveResourceDependencies :
//    Comparator<Resource> {
//    override fun compare(
//      o1: Resource,
//      o2: Resource
//    ): Int {
//      if (o1.dependsOn == null || o2.dependsOn == null) {
//        return 0
//      }
//
//      if (o1.dependsOn.contains(o2.type) && o2.dependsOn.contains(o1.type)) {
//        throw IllegalStateException("resources cannot have cyclic dependencies: ${o1.type} <-> ${o2.type}")
//      }
//
//      if (o1.dependsOn.contains(o2.type)) {
//        return -1
//      } else if (o2.dependsOn.contains(o1.type)) {
//        return 1
//      }
//
//      return 0
//    }
//  }
}
