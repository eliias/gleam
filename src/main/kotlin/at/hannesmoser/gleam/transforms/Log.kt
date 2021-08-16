package at.hannesmoser.gleam.transforms

import at.hannesmoser.log.Logger
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.ParDo
import org.apache.logging.log4j.core.net.Severity

class Log {
  companion object {
    fun <T> debug(msg: String = "") = ParDo.of<T, T>(WriterFn(msg, Severity.DEBUG))
    fun <T> info(msg: String = "") = ParDo.of<T, T>(WriterFn(msg, Severity.INFO))
    fun <T> error(msg: String = "") = ParDo.of<T, T>(WriterFn(msg, Severity.ERROR))
  }
}

private class WriterFn<T>(
  private val msg: String,
  private val severity: Severity) : DoFn<T, T>() {
  companion object {
    val logger by Logger()
  }

  @ProcessElement
  fun process(context: ProcessContext) {
    val fn: (msg: String) -> Unit = when (severity) {
      Severity.DEBUG -> logger::debug
      Severity.INFO -> logger::info
      Severity.ERROR -> logger::error
      else -> logger::info
    }

    val element = context.element()
    fn("$msg: $element")
    context.output(element)
  }
}
