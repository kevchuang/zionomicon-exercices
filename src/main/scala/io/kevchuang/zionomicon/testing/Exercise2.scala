package io.kevchuang.zionomicon.testing

import zio.*
import zio.test.*

object Exercise2 extends ZIOSpecDefault:

  private def createCache: UIO[Ref[Option[String]]] =
    Ref.make(Option.empty[String])

  private def insert[A](ref: Ref[Option[A]], value: A, ttl: Duration): UIO[Unit] =
    for
      _ <- ref.set(Some(value))
      _ <- ZIO.sleep(ttl)
      _ <- ref.set(None)
    yield ()

  def spec = suite("Exercise2")(
    test("cache should return the result before expiration and none after expiration"):
      for
        cache            <- createCache
        fiber            <- insert(cache, "hello", 2.seconds).fork
        _                <- TestClock.adjust(1.second)
        beforeExpiration <- cache.get
        _                <- TestClock.adjust(1.second)
        afterExpiration  <- cache.get
        _                <- fiber.join
      yield assertTrue(
        beforeExpiration.contains("hello"),
        afterExpiration.isEmpty
      )
  )

end Exercise2
