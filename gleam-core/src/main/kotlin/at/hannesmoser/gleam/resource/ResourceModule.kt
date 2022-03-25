package at.hannesmoser.gleam.resource

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.jsontype.NamedType
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import kotlin.reflect.KClass

/**
 * Finds all children of types given in the constructor and registers them as
 * Jackson sub-types.
 *
 * Sub-types need to have the `@JsonTypeName` annotation.
 * It will register types that would normally not be registered by Jackson.
 * Specifically, subtypes can be put in any namespace and don't need to be sub
 * classes of a sealed class.
 */
class ResourceModule(
  private val prefix: String,
  private val parentTypes: List<KClass<*>> = listOf(ResourceConfig::class)
) : Module() {
  override fun getModuleName(): String = "SubType"

  override fun version(): Version =
    Version(1, 0, 0, "", "at.hannesmoser.jackson.subtype", "subtype")

  @Suppress("SpreadOperator")
  override fun setupModule(context: SetupContext) {
    context.registerSubtypes(*findJsonSubTypes().toTypedArray())
  }

  private fun findJsonSubTypes(): List<NamedType> {
    val classes: ScanResult = scanClasses()
    return parentTypes.flatMap { classes.filterJsonSubTypes(it) }
  }

  private fun scanClasses(): ScanResult =
    ClassGraph().enableClassInfo().acceptPackages(prefix).scan()

  private fun ScanResult.filterJsonSubTypes(type: KClass<*>): Iterable<NamedType> =
    getSubclasses(type.java.name)
      .map(ClassInfo::loadClass)
      .map {
        NamedType(it, it.getAnnotation(JsonTypeName::class.java).value)
      }
}
