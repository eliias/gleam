@file:Suppress("PackageNaming")

package at.hannesmoser.gleam.samples.g23_bigtable

import at.hannesmoser.gleam.extensions.toEpochMicros
import at.hannesmoser.gleam.transforms.Log
import at.hannesmoser.gleam.transforms.generator.Generator
import com.google.bigtable.v2.Mutation
import com.google.common.primitives.Longs
import com.google.protobuf.ByteString
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.coders.IterableCoder
import org.apache.beam.sdk.coders.KvCoder
import org.apache.beam.sdk.coders.SerializableCoder
import org.apache.beam.sdk.extensions.protobuf.ByteStringCoder
import org.apache.beam.sdk.io.gcp.bigtable.BigtableIO
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.transforms.Wait
import org.apache.beam.sdk.transforms.windowing.AfterWatermark
import org.apache.beam.sdk.transforms.windowing.FixedWindows
import org.apache.beam.sdk.transforms.windowing.Repeatedly
import org.apache.beam.sdk.transforms.windowing.Window
import org.apache.beam.sdk.values.KV
import org.joda.time.Duration
import java.time.Instant

/**
 * You need to set up the Bigtable Emulator before running this pipeline:
 *
 * Setup:
 * - ./bin/up.sh
 * - brew install --cask google-cloud-sdk
 * - gcloud components update beta --quiet
 * - gcloud components install cbt
 * - BIGTABLE_EMULATOR_HOST=localhost:9035 cbt -project gleam -instance test createtable values
 * - BIGTABLE_EMULATOR_HOST=localhost:9035 cbt -project gleam -instance test createfamily values columnFamily
 * - BIGTABLE_EMULATOR_HOST=localhost:9035 cbt -project gleam -instance test ls # should print a single table "values"
 */
object Pipeline {
  @JvmStatic
  fun main(args: Array<String>) {
    val pipeline = Pipeline.create()

    val elements = pipeline
      .apply(Generator.longs(numElements = 10))
      .apply(
        Window.into<Long>(FixedWindows.of(Duration.standardSeconds(1)))
          .triggering(Repeatedly.forever(AfterWatermark.pastEndOfWindow()))
          .withAllowedLateness(Duration.standardSeconds(0))
          .accumulatingFiredPanes()
      )
      .apply(createMutations())
      .setCoder(
        KvCoder.of(
          ByteStringCoder.of(),
          IterableCoder.of(SerializableCoder.of(Mutation::class.java))
        )
      )

    val result = elements
      .apply(
        BigtableIO.write()
          .withEmulator("localhost:9035")
          .withProjectId("gleam")
          .withInstanceId("test")
          .withTableId("values")
          .withWriteResults()
      )

    elements
      .apply(Wait.on(result))
      .apply(Log.info("rows written: "))

    pipeline.run()
  }

  @Suppress("UNCHECKED_CAST")
  private fun createMutations() = ParDo.of(
    object : DoFn<Long, KV<ByteString, Iterable<@JvmWildcard Mutation>>>() {
      @ProcessElement
      fun process(context: ProcessContext) {
        val element = context.element()

        val setCell = Mutation.SetCell.newBuilder()
          .setFamilyName("columnFamily")
          .setColumnQualifier(ByteString.copyFromUtf8("columnName"))
          .setValue(ByteString.copyFrom(Longs.toByteArray(element)))
          .setTimestampMicros(Instant.now().toEpochMicros())
          .build()
        val mutation: Mutation =
          Mutation.newBuilder().setSetCell(setCell).build()

        val output =
          KV.of(
            padKey(element),
            listOf(mutation)
          )

        context.output(output as KV<ByteString, Iterable<Mutation>>)
      }
    })

  @Suppress("MagicNumber")
  private fun padKey(id: Long) = ByteString.copyFromUtf8(
    id
      .toString()
      .padStart(19, '0')
  )
}
