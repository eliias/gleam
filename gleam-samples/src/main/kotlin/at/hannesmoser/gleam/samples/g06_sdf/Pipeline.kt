@file:Suppress("PackageNaming")

package at.hannesmoser.gleam.samples.g06_sdf

import at.hannesmoser.gleam.transforms.Log
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.FileIO
import org.apache.beam.sdk.io.range.OffsetRange
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

object Pipeline {
  @JvmStatic
  fun main(args: Array<String>) {
    val pipeline = Pipeline.create()

    pipeline
      .apply(
        FileIO
          .match()
          .filepattern("$filesPath/*.bin")
      )
      .apply(FileIO.readMatches())
      .apply(ParDo.of(ReadInChunks()))
      .apply(Log.info())

    pipeline.run()
  }

  private class ReadInChunks(
    private val chunkSize: Int = 512
  ) : DoFn<FileIO.ReadableFile, String>() {
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

  private val filesPath = Pipeline::class.java
    .getResource("/files")!!
    .path
}
