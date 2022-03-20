package at.hannesmoser.gleam.flow.other

import org.apache.beam.sdk.transforms.Partition
import org.apache.beam.sdk.transforms.windowing.Window
import org.apache.beam.sdk.transforms.windowing.WindowFn
import org.apache.beam.sdk.values.PCollection

fun <T> PCollection<T>.window(
    fn: WindowFn<in T, *>,
    configure: (window: Window<T>) -> Window<T> = { it }
): PCollection<T> = apply(configure(Window.into(fn)))

fun <T> PCollection<T>.partition(numPartitions: Int, partitionFn: Partition.PartitionFn<T>) = apply(
    Partition.of(numPartitions, partitionFn)
)
