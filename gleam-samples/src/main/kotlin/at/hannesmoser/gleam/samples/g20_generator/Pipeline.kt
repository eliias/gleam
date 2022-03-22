package at.hannesmoser.gleam.samples.g20_generator

import at.hannesmoser.gleam.transforms.Log
import at.hannesmoser.gleam.transforms.generator.Generator
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.schemas.Schema

object Pipeline {
  @JvmStatic
  fun main(args: Array<String>) {
    val pipeline = Pipeline.create()

    val schema = Generator.schema(
      allowedTypes = arrayOf(
        Schema.TypeName.INT64,
        Schema.TypeName.STRING,
        Schema.TypeName.FLOAT
      )
    )

    pipeline
      .apply(Generator.rows(schema = schema))
      .apply(Log.info())

    pipeline.run()
  }
}
