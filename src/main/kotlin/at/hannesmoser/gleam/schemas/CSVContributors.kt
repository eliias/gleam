package at.hannesmoser.gleam.schemas

import org.apache.beam.sdk.schemas.Schema

object CSVContributors {
  val schema: Schema = Schema.builder()
    .addInt64Field("project_id")
    .addInt64Field("user_id")
    .build()
}
