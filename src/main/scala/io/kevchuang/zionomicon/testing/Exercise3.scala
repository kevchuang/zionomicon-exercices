package io.kevchuang.zionomicon.testing

import zio.*
import zio.test.*
import zio.test.Assertion.*

object Exercise3 extends ZIOSpecDefault:
  class RateLimiter(queue: Queue[Unit]):
    def rateLimit[R, E, A](effect: => ZIO[R, E, A]): ZIO[R, E, A] =
      queue.offer(()).delay(1.second) *> effect
  end RateLimiter

  object RateLimiter:
    def make: ZIO[Any, Nothing, RateLimiter] =
      for
        queue <- Queue.bounded[Unit](1)
        _     <- queue.take.repeat(Schedule.fixed(1.second)).fork
      yield RateLimiter(queue)
  end RateLimiter

  def spec = suite("Exercise3")(
    test("rateLimit should allow 1 request per second"):
      for
        rateLimiter <- RateLimiter.make
        _           <- ZIO.foreachDiscard(1 to 3)(i => rateLimiter.rateLimit(Console.print(i))).fork
        _           <- TestClock.adjust(1.second)
        first       <- TestConsole.output
        _           <- TestClock.adjust(1.second)
        second      <- TestConsole.output
        _           <- TestClock.adjust(1.second)
        third       <- TestConsole.output
      yield assert(first)(hasSameElements(Vector("1"))) &&
        assert(second)(hasSameElements(Vector("1", "2"))) &&
        assert(third)(hasSameElements(Vector("1", "2", "3")))
  )
end Exercise3
