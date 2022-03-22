@file:Suppress("PackageNaming")

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
      .apply(Generator.longs(numElements = 2000))
      .apply(Batcher())
      .apply(Log.info())

    pipeline.run()
  }

  /**
   * This transform batches all elements of a bundle into a list. This is
   * a very useful technique for IO heavy DoFn's.
   *
   * Example: An upstream transform provides a collection of record keys, and
   * the downstream transform needs to fetch the records from a persistent
   * datastore, it is usually better to batch multiple keys into a single
   * request.
   */
  private class Batcher :
    PTransform<PCollection<Long>, PCollection<Iterable<@JvmWildcard Long>>>() {
    override fun expand(input: PCollection<Long>): PCollection<Iterable<Long>> =
      input
        .apply(
          ParDo.of(
            object : DoFn<Long, Iterable<@JvmWildcard Long>>() {
              @Suppress("MagicNumber")
              private val maxBatchSize = 5
              private val batch = mutableListOf<Pair<BoundedWindow, Long>>()

              @ProcessElement
              fun process(context: ProcessContext, window: BoundedWindow) {
                batch.add(Pair(window, context.element()))

                if (batch.size == maxBatchSize) {
                  context.output(batch.map { it.second }.toList())
                  batch.clear()
                }
              }

              @FinishBundle
              fun finish(context: FinishBundleContext) {
                batch
                  .groupBy(keySelector = { it.first })
                  .forEach { (window, elements) ->
                    context.output(
                      elements.map { it.second }.toList(),
                      window.maxTimestamp(),
                      window
                    )
                  }
                batch.clear()
              }
            })
        )
        .setCoder(IterableCoder.of(VarLongCoder.of()))
  }
}
