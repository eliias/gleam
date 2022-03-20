package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.transforms.Combine
import org.apache.beam.sdk.transforms.CombineFnBase
import org.apache.beam.sdk.values.PCollection

fun <InputT, AccumT, OutputT> PCollection<InputT>.combine(combiner: CombineFnBase.GlobalCombineFn<InputT, AccumT, OutputT>): PCollection<OutputT> =
  apply(Combine.globally(combiner))
