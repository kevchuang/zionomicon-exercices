package io.kevchuang.zionomicon.testing

import java.io.IOException
import zio.*
import zio.test.assertCompletes
import zio.test.TestClock
import zio.test.ZIOSpecDefault

object Exercise1 extends ZIOSpecDefault:
  val countdown: ZIO[Any, IOException, List[Unit]] =
    ZIO.foreach(List(5, 4, 3, 2, 1))(countdown =>
      for
        _ <- Console.printLine(s"Countdown $countdown !")
        _ <- ZIO.sleep(1.second)
      yield ()
    )

  def spec = suite("Exercise1")(
    test("countdown should count 5 to 1 and sleep for 1 second between each interation"):
      for
        fiber <- countdown.fork
        _     <- TestClock.adjust(5.seconds)
        _     <- fiber.join
      yield assertCompletes
  )
end Exercise1
