package at.hannesmoser.gleam.resource

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.koin.core.module.Module

@Suppress("UnnecessaryAbstractClass")
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "type"
)
abstract class ResourceConfig(
  val type: String,
  val name: String? = null,
  val factory: Boolean? = null
) {
  abstract fun register(id: String, module: Module)
}
