package at.hannesmoser.gleam.store.backend

import at.hannesmoser.gleam.store.Store
import com.google.cloud.bigtable.beam.CloudBigtableTableConfiguration
import com.google.cloud.bigtable.hbase.BigtableConfiguration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.client.Result
import java.io.IOException

class Bigtable(
  private val config: CloudBigtableTableConfiguration
) : Store.Backend {
  override val valueCoder: Store.ValueCoder
    get() = TODO("Not yet implemented")

  private val connection by lazy {
    BigtableConfiguration.connect(config.toHBaseConfig())
  }

  private val table by lazy {
    connection.getTable(TableName.valueOf(config.tableId))
  }

  override fun <KeyT: Store.Key> has(key: KeyT): Boolean {
    return false
  }

  override fun <KeyT : Store.Key, ValueT> read(key: KeyT): Pair<ValueT?, Store.Backend.Failure<KeyT>?> {
    val results = read<KeyT, ValueT>(listOf(key))
    return results.iterator().next()
  }

  override fun <KeyT : Store.Key, ValueT> read(vararg keys: KeyT) = read<KeyT, ValueT>(keys.toList())


  override fun <KeyT : Store.Key, ValueT> read(keys: Iterable<KeyT>): Iterable<Pair<ValueT?, Store.Backend.Failure<KeyT>?>> {
    val request = keys.map { Get(it.toByteArray()) }
    val response = arrayOfNulls<Result>(keys.count())

    try {
      table.batch(request, response)
    } catch (e: IOException) {
      return keys.map { Pair(null, Store.Backend.Failure(it, e)) }
    } catch(e: InterruptedException) {
      return keys.map { Pair(null, Store.Backend.Failure(it, e)) }
    }

    return response.map { result ->
      if (result == null) {
        TODO("Not yet implemented")
      }

      Pair(valueCoder.decode(result), null)
    }
  }

  override fun <KeyT : Store.Key, ValueT> write(item: Pair<KeyT, ValueT>): Pair<Boolean, Store.Backend.Failure<KeyT>> {
    TODO("Not yet implemented")
  }

  override fun <KeyT : Store.Key, ValueT> write(items: Iterable<Pair<KeyT, ValueT>>): Iterable<Pair<Boolean, Store.Backend.Failure<KeyT>>> {
    TODO("Not yet implemented")
  }
}
