package at.hannesmoser.gleam.transforms.generator

import org.apache.beam.sdk.coders.BigDecimalCoder
import org.apache.beam.sdk.coders.BigEndianIntegerCoder
import org.apache.beam.sdk.coders.BigEndianLongCoder
import org.apache.beam.sdk.coders.BigEndianShortCoder
import org.apache.beam.sdk.coders.BooleanCoder
import org.apache.beam.sdk.coders.ByteArrayCoder
import org.apache.beam.sdk.coders.ByteCoder
import org.apache.beam.sdk.coders.Coder
import org.apache.beam.sdk.coders.DoubleCoder
import org.apache.beam.sdk.coders.FloatCoder
import org.apache.beam.sdk.coders.InstantCoder
import org.apache.beam.sdk.coders.IterableCoder
import org.apache.beam.sdk.coders.MapCoder
import org.apache.beam.sdk.coders.RowCoder
import org.apache.beam.sdk.coders.StringUtf8Coder
import org.apache.beam.sdk.schemas.Schema

@Suppress("ReturnCount")
internal fun <T> findCoderForFieldType(type: Schema.FieldType): Coder<T> {
  if (type.typeName.isPrimitiveType) {
    return findCoderForPrimitiveFieldType(type)
  }

  if (type.typeName.isCollectionType) {
    return findCoderForCollectionFieldType(type)
  }

  if (type.typeName.isCompositeType) {
    return findCoderForCompositeFieldType(type)
  }

  if (type.typeName.isLogicalType) {
    return findCoderForLogicalFieldType(type)
  }

  // catch the case of Java theoretically allowing null values for typeName
  throw IllegalArgumentException("type.typeName is null")
}

@Suppress("UNCHECKED_CAST")
private fun <T> findCoderForPrimitiveFieldType(type: Schema.FieldType): Coder<T> {
  return when (type.typeName) {
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
    else -> throw IllegalArgumentException("not a scalar type: ${type.typeName.name}")
  } as Coder<T>
}

@Suppress("UNCHECKED_CAST")
private fun <T> findCoderForCollectionFieldType(type: Schema.FieldType): Coder<T> {
  return when (type.typeName) {
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
    else -> throw IllegalArgumentException("not a collection type: ${type.typeName.name}")
  } as Coder<T>
}

@Suppress("UNCHECKED_CAST")
private fun <T> findCoderForCompositeFieldType(type: Schema.FieldType): Coder<T> {
  return when (type.typeName) {
    Schema.TypeName.ROW -> RowCoder.of(type.rowSchema)
    else -> throw IllegalArgumentException("not a composite type: ${type.typeName.name}")
  } as Coder<T>
}

private fun <T> findCoderForLogicalFieldType(type: Schema.FieldType): Coder<T> {
  return when (type.typeName) {
    Schema.TypeName.LOGICAL_TYPE -> findCoderForFieldType(type.logicalType!!.baseType)
    else -> throw IllegalArgumentException("not a logical type: ${type.typeName.name}")
  }
}
