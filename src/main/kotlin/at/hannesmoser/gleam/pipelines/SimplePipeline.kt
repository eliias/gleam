package at.hannesmoser.gleam.pipelines

import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.GenerateSequence
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.ParDo
import org.joda.time.Duration

open class SimplePipeline {
  fun run(args: Array<String>) {
    val options = PipelineOptionsFactory
      .fromArgs(*args)
      .withValidation()
      .create()

    val pipeline = Pipeline.create(options)

    pipeline
      .apply(
        GenerateSequence
          .from(0L)
          .withRate(1, Duration.millis(1000L))
      )
      .apply(ParDo.of(object : DoFn<Long, Long>() {
        @ProcessElement
        fun process(context: ProcessContext) {
          val value = context.element()
          println(value)
          context.output(value)
        }
      }))

    pipeline
      .run()
      .waitUntilFinish()
  }
}
