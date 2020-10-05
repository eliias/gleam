package at.hannesmoser.gleam.sources

import org.apache.beam.sdk.io.GenerateSequence
import org.apache.beam.sdk.metrics.Metrics
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.transforms.View
import org.apache.beam.sdk.transforms.WithKeys
import org.apache.beam.sdk.values.PBegin
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row
import org.joda.time.Duration
import org.joda.time.Instant

class ExtractUnbounded(
  private val source: PCollection<Row>,
  private val numElements: Long = 1L,
  private val periodLength: Duration = Duration.standardSeconds(1)
) : PTransform<PBegin, PCollection<Row>>() {
  override fun expand(input: PBegin): PCollection<Row> {
    val sourceAsSideInput = source
      .apply("key by id", WithKeys.of { it.getInt64("id")!! })
      .apply(View.asMap())

    return input
      .apply(
        "generate sequence",
        GenerateSequence
          .from(1)
          .withRate(numElements, periodLength)
      )
      .apply("create row from side input", ParDo.of(object : DoFn<Long, Row>() {
        private val counter by lazy {
          Metrics.counter("counter", "elements")
        }

        @ProcessElement
        fun process(context: ProcessContext) {
          val id = context.element()
          val sourceMap = context.sideInput(sourceAsSideInput)

          if (sourceMap.containsKey(id)) {
            val element = sourceMap[id]
            counter.inc()
            context.outputWithTimestamp(element, Instant.now())
          }
        }
      }).withSideInputs(sourceAsSideInput))
      .setRowSchema(source.schema)
  }
}
