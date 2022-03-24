package at.hannesmoser.gleam.resource.resources

import at.hannesmoser.gleam.resource.Resource
import at.hannesmoser.gleam.resource.ResourceConfig
import com.fasterxml.jackson.annotation.JsonTypeName
import org.koin.core.module.Module

class GCP(
  val projectId: String,
  val instanceId: String
) : Resource {
  @JsonTypeName("GCP")
  data class Config(
    val projectId: String,
    val instanceId: String
  ) : ResourceConfig("GCP") {
    override fun register(
      id: String,
      module: Module
    ) {
      if (factory == true) {
        module.factory { GCP(projectId, instanceId) }
      } else {
        module.single { GCP(projectId, instanceId) }
      }
    }
  }
}
