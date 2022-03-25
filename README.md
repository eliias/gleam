# gleam

> Fun with Apache Beam.

## gleam-core

Extends the Apache Beam library with convenience functions, mostly to better
utilize type inference in Kotlin.

### `transform`

A convenience function to create a new transform with one or more steps.

```kotlin
val newTransform = transform {
  it.apply(SomeTransform())
}
```

A default implementation without this helper would look more like this:

```kotlin
val newTransform = object : PTransform<PCollection<T>, POutput>() {
  override fun expand(input: PCollection<T>) = input.apply(SomeTransform)
}
```

## gleam-flow

A DSL that utilizes Generics, Scope Functions, and Type Inference to provide the
transforms from the Apache Beam library.

```kotlin
val pipeline = Pipeline.create()

pipeline
  .sequence()
  .window(FixedWindows.of(Duration.standardSeconds(1)))
  .map { it * 2 }
  .filter { it % 2 == 0L }
  .key { it / 10 }
  .groupByKey()
  .sink(Log.info())

pipeline.run()
```

## gleam-samples

### 1. Batch

This demo's batching of all elements of a bundle. This is a very useful
technique for IO heavy DoFn's.

Example: An upstream transform provides a collection of record keys, and the
downstream transform needs to fetch the records from a persistent datastore, it
is usually better to batch multiple keys into a single request.

### 2. DSL

A Kotlin specific Apache Beam DSL that allows applying transforms in a direct
way onto collections.

### 3. Schema Extensions

Add metadata to schemas or single fields. The metadata and hints are stored as
schema options. Directly with the schema and field definition.

```kotlin
val schema = schema {
  version("1.0")

  // base
  field("id", INT64.withNullable(true)) {
    // adds the "version" extension
    version("1.0")
  }
  fields(
    Field.of("title", STRING),
    Field.of("description", STRING)
  ) {
    // adds the "compress" extension to all fields!
    compress()
  }
}
```

### 4. POJO's

Demo's the usage of POJO's (Beans) and field schemas in Beam with Kotlin

### 6. SDF

A running version of the SDF example from the Apache Beam website.

### 14. Resources

Inversion of Control and Dependency Injection with Apache Beam, Kotlin and Koin.

### 20. Generator

A demo of the `gleam-core` Generator to create fake data for a pipeline.

### 23. Bigtable

A demo of the `BigtableIO.write` transform.
