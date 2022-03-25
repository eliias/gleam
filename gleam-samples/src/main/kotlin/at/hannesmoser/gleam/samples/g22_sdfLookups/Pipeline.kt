@file:Suppress("PackageNaming")

package at.hannesmoser.gleam.samples.g22_sdfLookups

import at.hannesmoser.gleam.transforms.Log
import at.hannesmoser.gleam.transforms.generator.Generator
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.FileIO
import org.apache.beam.sdk.io.range.OffsetRange
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.GroupIntoBatches
import org.apache.beam.sdk.transforms.WithKeys
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker
import org.apache.beam.sdk.transforms.windowing.FixedWindows
import org.apache.beam.sdk.transforms.windowing.Window
import org.joda.time.Duration
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

@Suppress("MagicNumber")
object Pipeline {
  @JvmStatic
  fun main(args: Array<String>) {
    val pipeline = Pipeline.create()

    pipeline
      .apply(Generator.longs(numElements = 10))
      .apply(
        Window.into(
          FixedWindows.of(Duration.standardSeconds(1))
        )
      )
      .apply(WithKeys.of { it })
      .apply(GroupIntoBatches.ofSize(5))
      .apply(Log.info())

    pipeline.run()
  }

  @Suppress("UnusedPrivateClass")
  private class ReadInChunks(
    private val chunkSize: Int = 512
  ) : DoFn<Iterable<Long>, String>() {
    @ProcessElement
    fun process(
      @Element element: FileIO.ReadableFile,
      tracker: RestrictionTracker<OffsetRange, Long>,
      output: OutputReceiver<String>
    ) {
      // move to next offset (move file pointer to where we haven't read yet)
      val channel = element
        .openSeekable()
        .position(tracker.currentRestriction().from)

      // try to claim that offset on this worker
      while (tracker.tryClaim(channel.position())) {
        // allocate a buffer to read into
        val buffer = ByteBuffer.allocate(chunkSize)
        // read from offset into buffer
        channel.read(buffer)

        // reset buffer so that we can consume it
        buffer.position(0)

        // decode to string for the sake of this demo
        val str = StandardCharsets.UTF_8
          .decode(buffer)
          .toString()

        // output the "partial" content of the file
        output.output(str)
      }
    }

    /**
     * Get an initial restriction, this describes ALL of the work! In our case
     * we provide the full range from first byte of the file (0) until the last
     * byte of the file (size of file)
     */
    @GetInitialRestriction
    fun getInitialRestriction(@Element element: FileIO.ReadableFile): OffsetRange {
      return OffsetRange(0, element.metadata.sizeBytes())
    }

    /**
     * Help runner to split/parallelize right away. In addition to "dynamic"
     * splitting, we can help to pre-split the work if we already know how we
     * want it to be split up. In our case we just split the file into equal
     * chunks of chunkSize=512 and create offset ranges for each chunk. The last
     * chunk is the remainder (could be less than 512)
     */
    @SplitRestriction
    fun splitRestriction(
      @Restriction restriction: OffsetRange,
      splitReceiver: OutputReceiver<OffsetRange?>
    ) {
      val splitSize = chunkSize
      var i = restriction.from
      while (i < restriction.to - splitSize) {
        // Compute and output 512 B size ranges to process in parallel
        val end = i + splitSize
        splitReceiver.output(OffsetRange(i, end))
        i = end
      }

      // Output the last range
      splitReceiver.output(OffsetRange(i, restriction.to))
    }
  }

  @Suppress("UnusedPrivateMember")
  private val filesPath = Pipeline::class.java
    .getResource("/files")!!
    .path
}
