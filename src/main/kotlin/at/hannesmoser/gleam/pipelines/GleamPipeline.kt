package at.hannesmoser.gleam.pipelines

import at.hannesmoser.gleam.schemas.CSVProject
import at.hannesmoser.gleam.schemas.CSVUser
import at.hannesmoser.gleam.sources.ExtractRows
import at.hannesmoser.gleam.sources.ExtractUnbounded
import at.hannesmoser.gleam.transforms.Debug
import at.hannesmoser.log.Logger
import org.apache.beam.runners.direct.DirectOptions
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.schemas.transforms.Join
import org.apache.beam.sdk.transforms.windowing.AfterWatermark
import org.apache.beam.sdk.transforms.windowing.FixedWindows
import org.apache.beam.sdk.transforms.windowing.Repeatedly
import org.apache.beam.sdk.transforms.windowing.Window
import org.apache.beam.sdk.values.Row
import org.joda.time.Duration

class GleamPipeline {
  companion object {
    private val logger by Logger()
  }

  fun run(args: Array<String>) {
    val options = PipelineOptionsFactory
      .fromArgs(*args)
      .withValidation()
      .`as`(DirectOptions::class.java)

    options.targetParallelism = 1

    val pipeline = Pipeline.create(options)

    val window = Window
      .into<Row>(
        FixedWindows
          .of(Duration.standardSeconds(5))
      )
      .triggering(
        Repeatedly.forever(
          AfterWatermark
            .pastEndOfWindow()
        )
      )
      .withOnTimeBehavior(Window.OnTimeBehavior.FIRE_IF_NON_EMPTY)
      .withAllowedLateness(Duration.ZERO)
      .discardingFiredPanes()

    val csvUsers = pipeline
      .apply("extract users from CSV", ExtractRows("./src/test/fixtures/users.csv", CSVUser.schema))

    val users = pipeline
      .apply("generate user sequence", ExtractUnbounded(csvUsers))
      .apply("users window", window)

    val csvProjects = pipeline
      .apply("extract projects from CSV", ExtractRows("./src/test/fixtures/projects.csv", CSVProject.schema))

    val projects = pipeline
      .apply("generate project sequence", ExtractUnbounded(csvProjects))
      .apply("projects window", window)
      .apply("join on users", Join
        .innerJoin<Row, Row>(users)
        .on(Join.FieldsEqual
          .left("id")
          .right("project_id")
        )
      )
      .apply("log", Debug("joined project with user"))

    //val contributors = pipeline
    //  .apply("extract contributors", ExtractRows("./src/test/fixtures/contributors.csv", CSVContributors.schema))
    //
    //
    //val sqlCountProjectContributors = """
    //  SELECT
    //    projects.name AS project_name,
    //    COUNT(users.id) AS num_contributors
    //  FROM projects
    //  INNER JOIN users
    //    ON projects.id = users.project_id
    //  GROUP BY
    //    projects.name
    //""".trimIndent()
    //
    //PCollectionTuple.of(
    //  "projects", projects,
    //  "users", users
    //)
    //  .apply(
    //    "count contributors",
    //    SqlTransform.query(sqlCountProjectContributors)
    //  )
    //  .apply("log", Debug())

    pipeline
      .run()
      .waitUntilFinish()
  }
}
