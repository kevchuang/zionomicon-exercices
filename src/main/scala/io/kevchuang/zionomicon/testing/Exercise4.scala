package io.kevchuang.zionomicon.testing

import scala.annotation.tailrec
import zio.*
import zio.test.*
import zio.test.Assertion.*

object Exercise4 extends ZIOSpecDefault:
  def reverse[A](list: List[A]): UIO[List[A]] =
    @tailrec
    def loop(remaining: List[A], returning: List[A]): List[A] =
      remaining match
        case Nil    => returning
        case h :: t => loop(t, h +: returning)
    ZIO.succeed(loop(list, List.empty))

  val genList: Gen[Any, List[Int]] = Gen.listOf(Gen.int)

  def spec = suite("Exercise4")(
    test("reverse should return the list with reversed elements"):
      check(genList): list =>
        for
          reversed <- reverse(list)
          result   <- reverse(reversed)
        yield assertTrue(result == list)
  )
end Exercise4
