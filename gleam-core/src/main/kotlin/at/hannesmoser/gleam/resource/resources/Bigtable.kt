package at.hannesmoser.gleam.resource.resources

import at.hannesmoser.gleam.resource.Resource
import at.hannesmoser.gleam.resource.ResourceConfig
import com.fasterxml.jackson.annotation.JsonTypeName
import org.apache.beam.sdk.io.gcp.bigtable.BigtableIO
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf

class Bigtable(private val gcp: GCP) : Resource {
  @JsonTypeName("Bigtable")
  class Config : ResourceConfig("Bigtable") {
    override fun register(
      id: String,
      module: Module
    ) {
      if (factory == true) {
        module.factoryOf(::Bigtable)
      } else {
        module.singleOf(::Bigtable)
      }
    }
  }

  fun write() = BigtableIO.write()
    .withProjectId(gcp.projectId)
    .withInstanceId(gcp.instanceId)

  fun read() = BigtableIO.read()
    .withProjectId(gcp.projectId)
    .withInstanceId(gcp.instanceId)
}
