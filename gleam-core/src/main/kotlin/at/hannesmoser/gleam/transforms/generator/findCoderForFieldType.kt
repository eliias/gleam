package at.hannesmoser.gleam.transforms.generator

import org.apache.beam.sdk.coders.*
import org.apache.beam.sdk.schemas.Schema

internal fun <T> findCoderForFieldType(type: Schema.FieldType): Coder<T> {
  return when (type.typeName) {
    // scalar types
    Schema.TypeName.BOOLEAN -> BooleanCoder.of()
    Schema.TypeName.BYTE -> ByteCoder.of()
    Schema.TypeName.BYTES -> ByteArrayCoder.of()
    Schema.TypeName.DATETIME -> InstantCoder.of()
    Schema.TypeName.DECIMAL -> BigDecimalCoder.of()
    Schema.TypeName.DOUBLE -> DoubleCoder.of()
    Schema.TypeName.FLOAT -> FloatCoder.of()
    Schema.TypeName.INT16 -> BigEndianShortCoder.of()
    Schema.TypeName.INT32 -> BigEndianIntegerCoder.of()
    Schema.TypeName.INT64 -> BigEndianLongCoder.of()
    Schema.TypeName.STRING -> StringUtf8Coder.of()

    // aggregate types
    Schema.TypeName.ARRAY -> IterableCoder.of(
      findCoderForFieldType<T>(type.collectionElementType!!)
    )
    Schema.TypeName.ITERABLE -> IterableCoder.of(
      findCoderForFieldType<T>(type.collectionElementType!!)
    )
    Schema.TypeName.MAP -> MapCoder.of(
      findCoderForFieldType<T>(type.mapKeyType!!),
      findCoderForFieldType<T>(type.mapValueType!!)
    )

    // complex types
    Schema.TypeName.ROW -> RowCoder.of(type.rowSchema)
    Schema.TypeName.LOGICAL_TYPE -> findCoderForFieldType(
      type.logicalType!!.baseType
    )

    // catch the case of Java theoretically allowing null values for typeName
    null -> throw IllegalArgumentException("type.typeName is null")
  } as Coder<T>
}
