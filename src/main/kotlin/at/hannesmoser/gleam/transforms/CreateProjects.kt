package at.hannesmoser.gleam.transforms

import at.hannesmoser.gleam.entities.User
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.values.PCollection

class CreateProjects<T : User> : PTransform<PCollection<T>, PCollection<T>>() {
  override fun expand(input: PCollection<T>): PCollection<T> {
    return input.apply("debug", ParDo.of(CreateProject()))
  }
}

private class CreateProject<T : User> : DoFn<T, T>() {
  @ProcessElement
  fun process(@Element element: T, out: OutputReceiver<T>) {
    out.output(element)
  }
}
