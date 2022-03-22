package at.hannesmoser.gleam.samples.g01_batch

import at.hannesmoser.gleam.transforms.Log
import at.hannesmoser.gleam.transforms.generator.Generator
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.coders.IterableCoder
import org.apache.beam.sdk.coders.VarLongCoder
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.transforms.windowing.BoundedWindow
import org.apache.beam.sdk.values.PCollection

object Pipeline {
  @JvmStatic
  fun main(args: Array<String>) {
    val pipeline = Pipeline.create()

    pipeline
      .apply(Generator.longs(numElements = 20))
      .apply(Batcher())
      .apply(Log.info())

    pipeline.run()
  }

  private class Batcher :
    PTransform<PCollection<Long>, PCollection<Iterable<@JvmWildcard Long>>>() {
    override fun expand(input: PCollection<Long>): PCollection<Iterable<Long>> =
      input
        .apply(ParDo.of(object : DoFn<Long, Iterable<@JvmWildcard Long>>() {
          private val maxBatchSize = 5
          private val batch = mutableListOf<Long>()
          private lateinit var lastWindow: BoundedWindow

          @ProcessElement
          fun process(context: ProcessContext, window: BoundedWindow) {
            batch.add(context.element())
            lastWindow = window

            if (batch.size == maxBatchSize) {
              context.output(batch.toList())
              batch.clear()
            }
          }

          @FinishBundle
          fun finish(context: FinishBundleContext) {
            context.output(
              batch.toList(),
              lastWindow.maxTimestamp(),
              lastWindow
            )
            batch.clear()
          }
        }))
        .setCoder(IterableCoder.of(VarLongCoder.of()))
  }
}
