package at.hannesmoser.gleam.store

import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.values.PCollection
import java.lang.IllegalArgumentException

class Store {
  companion object {
    fun <KeyT, OutputT> read(backend: Backend) = Read<KeyT, OutputT>(backend)
    fun <KeyT, Output> write(backend: Backend) = Write<KeyT, Output>(backend)
  }

  interface Key {
    fun toByteArray(): ByteArray
  }

  interface Backend {
    class Failure<KeyT : Key>(val key: KeyT, val reason: Exception)

    fun <KeyT: Key> has(key: KeyT): Boolean

    fun <KeyT: Key, ValueT> read(key: KeyT): Pair<ValueT?, Failure<KeyT>?>
    fun <KeyT: Key, ValueT> read(vararg keys: KeyT): Iterable<Pair<ValueT?, Failure<KeyT>?>>
    fun <KeyT: Key, ValueT> read(keys: Iterable<KeyT>): Iterable<Pair<ValueT?, Failure<KeyT>?>>

    fun <KeyT: Key, ValueT> write(item: Pair<KeyT, ValueT>): Pair<Boolean, Failure<KeyT>>
    fun <KeyT: Key, ValueT> write(items: Iterable<Pair< KeyT, ValueT>>): Iterable<Pair<Boolean, Failure<KeyT>>>

    val valueCoder: ValueCoder
  }

  interface ValueCoder {
    @Throws(IllegalArgumentException::class)
    fun <ValueT> encode(value: ValueT): ByteArray

    @Throws(IllegalArgumentException::class)
    fun <ValueT, InputT> decode(input: InputT): ValueT
  }

  abstract class Operation<Key, T>(
    private val backend: Backend
  ) : PTransform<PCollection<Key>, PCollection<T>>()

  class Read<KeyT, OutputT>(backend: Backend): Operation<KeyT, OutputT>(backend) {
    override fun expand(input: PCollection<KeyT>): PCollection<OutputT> {
      TODO("Not yet implemented")
    }
  }

  class Write<KeyT, InputT>(backend: Backend): Operation<KeyT, InputT>(backend) {
    override fun expand(input: PCollection<KeyT>): PCollection<InputT> {
      TODO("Not yet implemented")
    }
  }
}
