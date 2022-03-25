package at.hannesmoser.gleam.schema.extensions

import com.google.common.primitives.Longs
import com.google.protobuf.ByteString
import org.apache.beam.sdk.values.Row

typealias KeyFn = (row: Row) -> ByteString

fun createInt64KeyFn(fieldName: String): KeyFn =
  { ByteString.copyFrom(Longs.toByteArray(it.getInt64(fieldName)!!)) }

/**
 * Convenience alias for INT64 based keys where the key value is stored in the
 * "id" field.
 */
val ID = createInt64KeyFn("id")
