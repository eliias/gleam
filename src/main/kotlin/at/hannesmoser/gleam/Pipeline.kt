package at.hannesmoser.gleam

import at.hannesmoser.log.Logger
import org.apache.beam.sdk.PipelineResult
import org.apache.beam.sdk.PipelineRunner
import org.apache.beam.sdk.options.PipelineOptions
import org.apache.beam.sdk.util.UserCodeException
import org.apache.beam.sdk.Pipeline as BeamPipeline

class Pipeline(options: PipelineOptions) : BeamPipeline(options) {
  companion object {
    private val log by Logger()

    fun create(options: PipelineOptions) = Pipeline(options)
  }

  override fun run(options: PipelineOptions): PipelineResult {
    val runner = PipelineRunner.fromOptions(options)
    log.debug("Running {} via {}", this, runner)

    return try {
      runner.run(this)!!
    } catch (e: UserCodeException) {
      throw PipelineExecutionException(e.cause!!)
    }
  }
}
