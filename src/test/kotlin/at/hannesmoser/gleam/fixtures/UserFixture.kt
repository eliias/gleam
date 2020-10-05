package at.hannesmoser.gleam.fixtures

import at.hannesmoser.gleam.entities.User
import io.github.serpro69.kfaker.Faker

class UserFixture {
  companion object {
    fun one() = create(1)[0]

    fun create(num: Int): List<User> {
      val faker = Faker()

      return (1..num).map {
        User(
          it.toLong(),
          "${faker.name.firstName()} ${faker.name.lastName()}"
        )
      }
    }
  }
}
