package at.hannesmoser.gleam.transforms

import at.hannesmoser.log.Logger
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.values.PCollection

class Debug<T>(private val msg: String = "") : PTransform<PCollection<T>, PCollection<T>>() {
  override fun expand(input: PCollection<T>): PCollection<T> {
    return input.apply("debug", ParDo.of(Log(msg)))
  }
}

private class Log<T>(private val msg: String) : DoFn<T, T>() {
  companion object {
    private val logger by Logger()
  }

  @ProcessElement
  fun process(@Element element: T, out: OutputReceiver<T>) {
    logger.info(msg + ": " + element.toString())
    out.output(element)
  }
}
