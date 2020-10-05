package at.hannesmoser.gleam.transforms

import org.apache.beam.sdk.transforms.Filter
import org.apache.beam.sdk.transforms.SerializableFunction

class Filters {
  companion object {
    fun <T> filter(comparator: (element: T) -> Boolean): Filter<T> = Filter
      .by<T, SerializableFunction<T, Boolean>>(SerializableFunction { comparator(it) })
  }
}
