package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.transforms.Partition
import org.apache.beam.sdk.values.PCollection

fun <T> PCollection<T>.partition(
  numPartitions: Int,
  partitionFn: Partition.PartitionFn<T>
) = apply(
  Partition.of(numPartitions, partitionFn)
)
