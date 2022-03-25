package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.values.PCollection

fun <InputT, OutputT> PCollection<InputT>.parDo(doFn: DoFn<InputT, OutputT>): PCollection<OutputT> =
  apply(ParDo.of(doFn))
