package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.schemas.transforms.Join
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row

fun PCollection<Row>.innerJoin(rhs: PCollection<Row>) = apply(
  Join.innerJoin(rhs)
)

fun PCollection<Row>.fullOuterJoin(rhs: PCollection<Row>) = apply(
  Join.fullOuterJoin(rhs)
)

fun PCollection<Row>.leftOuterJoin(rhs: PCollection<Row>) = apply(
  Join.leftOuterJoin(rhs)
)

fun PCollection<Row>.rightOuterJoin(rhs: PCollection<Row>) = apply(
  Join.rightOuterJoin(rhs)
)
