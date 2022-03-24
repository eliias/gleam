package at.hannesmoser.gleam.resource

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class WorkerConfiguration(
  val resources: List<ResourceConfig>
)
