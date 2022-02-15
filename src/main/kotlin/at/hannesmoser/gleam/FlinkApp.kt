package at.hannesmoser.gleam

import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.flink.table.api.TableEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.filesystem.FileSystemTableSink
import org.apache.flink.table.sinks.CsvTableSink
import java.nio.file.Path

object FlinkApp {
  @JvmStatic
  fun main(args: Array<String>) {
    val backend = EmbeddedRocksDBStateBackend(true)

    val env = StreamExecutionEnvironment.getExecutionEnvironment()
    env.stateBackend = backend
    env.checkpointConfig.setCheckpointStorage("file:///Users/hannesmoser/src/github.com/eliias/gleam/storage")
    env.checkpointConfig.checkpointInterval = 10000

    val tEnv: TableEnvironment = StreamTableEnvironment.create(env)
    val sink = tEnv.executeSql(sinkDefinition)
    val products = tEnv.executeSql(productTableDefinition)
    val orders = tEnv.executeSql(orderTableDefinition)

//    val view = tEnv.sqlQuery(
//      """
//      select
//        p.id, p.title
//      from Products as p
//      """.trimIndent()
//    )

    val view = tEnv.sqlQuery(
      """
      select
        p.id, p.title, o.product_id
      from Products as p
        inner join Orders o
          on p.id = o.product_id
      """.trimIndent()
    )

    view.executeInsert("Sink")
  }

  private val productTableDefinition = """
      CREATE TABLE Products (
          id         BIGINT,
          title      STRING,
          created_at TIMESTAMP(3)
      ) WITH (
        'connector' = 'datagen',
        'rows-per-second' = '1',
        'fields.id.min' = '1',
        'fields.id.max' = '10'
      )
    """.trimIndent()

  private val orderTableDefinition = """
      CREATE TABLE Orders (
          id         BIGINT,
          product_id BIGINT,
          created_at TIMESTAMP(3)
      ) WITH (
        'connector' = 'datagen',
        'rows-per-second' = '1',
        'fields.id.min' = '1',
        'fields.id.max' = '10',
        'fields.product_id.min' = '1',
        'fields.product_id.max' = '10'
      )
    """.trimIndent()

  private val sinkDefinition = """
    CREATE TABLE Sink (
      id          BIGINT,
      title       STRING,
      product_id  BIGINT
    ) PARTITIONED BY (id) WITH (
      'connector'='filesystem',
      'path'='file:///Users/hannesmoser/src/github.com/eliias/gleam/tables',
      'format'='json'
    )
  """.trimIndent()
}
