package at.hannesmoser.gleam.entities

import org.apache.beam.sdk.schemas.JavaBeanSchema
import org.apache.beam.sdk.schemas.annotations.DefaultSchema

@DefaultSchema(JavaBeanSchema::class)
data class Project(
  var id: Long = -1,
  var name: String = "",
  var owner: Long = -1
)
