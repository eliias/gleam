package at.hannesmoser.gleam.pipelines

import at.hannesmoser.gleam.sources.ExtractUsers
import at.hannesmoser.gleam.transforms.Debug
import at.hannesmoser.log.Logger
import org.apache.beam.runners.direct.DirectOptions
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.options.PipelineOptionsFactory

class GleamPipeline {
  companion object {
    private val logger by Logger()
  }
  fun run(args: Array<String>) {
    val options = PipelineOptionsFactory
      .fromArgs(*args)
      .withValidation()
      .`as`(DirectOptions::class.java)

    val p = Pipeline.create(options)

    p
      .apply("extract users", ExtractUsers("./src/test/fixtures/users.csv"))
      .apply("debug", Debug())

    p
      .run()
      .waitUntilFinish()
  }
}
