package at.hannesmoser.gleam.transforms.generator

import org.apache.beam.sdk.schemas.Schema

internal val ALL_TYPENAMES = arrayOf(
  // scalar types
  Schema.TypeName.BOOLEAN,
  Schema.TypeName.BYTE,
  Schema.TypeName.BYTES,
  Schema.TypeName.DATETIME,
  Schema.TypeName.DECIMAL,
  Schema.TypeName.DOUBLE,
  Schema.TypeName.FLOAT,
  Schema.TypeName.INT16,
  Schema.TypeName.INT32,
  Schema.TypeName.INT64,
  Schema.TypeName.STRING,

  // aggregate types
  Schema.TypeName.ARRAY,
  Schema.TypeName.ITERABLE,
  Schema.TypeName.MAP,

  // complex types
  Schema.TypeName.ROW,
  Schema.TypeName.LOGICAL_TYPE
)
