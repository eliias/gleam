package at.hannesmoser.gleam.transforms

import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.transforms.windowing.BoundedWindow
import org.joda.time.Instant

// Per element of bundle transforms
class Collection {
  companion object {
    fun <In> forEach(fn: (In) -> Unit) = ParDo.of(EachFn(fn))

    private class EachFn<T>(private val fn: (T) -> Unit) : DoFn<T, T>() {
      @ProcessElement
      fun process(context: ProcessContext) {
        val element = context.element()
        fn(element)
        context.output(element)
      }
    }

    fun <In, Out> map(fn: (In) -> Out) = ParDo.of(MapFn(fn))

    private class MapFn<In, Out>(private val fn: (In) -> Out) : DoFn<In, Out>() {
      @ProcessElement
      fun process(context: ProcessContext) {
        context.output(fn(context.element()))
      }
    }

    fun <T> reduce(iteratee: (T, MutableList<T>) -> MutableList<T>): ParDo.SingleOutput<T, MutableList<T>> =
      ParDo.of(ReduceFn(iteratee))

    private class ReduceFn<T>(
      private val fn: (T, MutableList<T>) -> MutableList<T>
    ) : DoFn<T, MutableList<T>>() {
      var accumulator = mutableListOf<T>()
      var currentWindow: BoundedWindow? = null
      var maxTimestamp: Instant? = null

      @StartBundle
      fun startBundle() {
        accumulator = mutableListOf()
      }

      @FinishBundle
      fun finishBundle(context: FinishBundleContext) {
        if (accumulator.size == 0) {
          return
        }
        print("commit ${accumulator.size} elements")
        if (currentWindow == null || maxTimestamp == null) {
          return
        }

        context.output(accumulator, maxTimestamp!!, currentWindow!!)
      }

      @ProcessElement
      fun process(context: ProcessContext, window: BoundedWindow) {
        currentWindow = window
        if (maxTimestamp == null || context.timestamp().isAfter(maxTimestamp)) {
          maxTimestamp = context.timestamp()
        }
        fn(context.element(), accumulator)
      }
    }
  }
}

