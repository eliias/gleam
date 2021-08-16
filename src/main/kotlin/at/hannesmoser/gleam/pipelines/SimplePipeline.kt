package at.hannesmoser.gleam.pipelines

import at.hannesmoser.gleam.Pipeline
import org.apache.beam.sdk.coders.VarLongCoder
import org.apache.beam.sdk.io.GenerateSequence
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.transforms.Create
import org.apache.beam.sdk.transforms.MapElements
import org.apache.beam.sdk.transforms.SerializableFunction
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.TypeDescriptors
import org.joda.time.Duration

open class SimplePipeline {
  fun run(args: Array<String>) {
    val options = PipelineOptionsFactory
      .fromArgs(*args)
      .withValidation()
      .create()

    val pipeline = Pipeline.create(options)

    pipeline
      .apply(GenerateSequence
        .from(0L)
        .withRate(1, Duration.millis(1L))
      )

    pipeline
      .run()
      .waitUntilFinish()
  }
}
