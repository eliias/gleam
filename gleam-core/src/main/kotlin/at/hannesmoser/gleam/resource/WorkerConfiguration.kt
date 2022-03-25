package at.hannesmoser.gleam.resource

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonMerge

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class WorkerConfiguration(
  @JsonMerge
  val resources: Map<String, ResourceConfig>?
)
