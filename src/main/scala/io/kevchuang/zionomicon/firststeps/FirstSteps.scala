package io.kevchuang.zionomicon.firststeps

import java.io.IOException
import scala.io.Source
import scala.io.StdIn
import zio.*

object FirstSteps:
  object Exercise1:
    def readFile(file: String): String =
      val source = Source.fromFile(file)
      try source.getLines().mkString
      finally source.close()

    def readFileZio(file: String): Task[String] =
      ZIO.attempt(readFile(file))
  end Exercise1

  object Exercise2:
    def writeFile(file: String, text: String): Unit =
      import java.io.*
      val pw = new PrintWriter(new File(file))
      try pw.write(text)
      finally pw.close()

    def writeFileZio(file: String, text: String): Task[Unit] =
      ZIO.attempt(writeFile(file, text))
  end Exercise2

  object Exercise3:
    import Exercise1.*
    import Exercise2.*
    def copyFile(source: String, dest: String): Unit =
      val contents = readFile(source)
      writeFile(dest, contents)

    def copyFileZio(source: String, dest: String): Task[Unit] =
      for
        contents <- readFileZio(source)
        _        <- writeFileZio(dest, contents)
      yield ()
  end Exercise3

  object Exercise4:
    def printLine(line: String): Task[Unit] =
      ZIO.attempt(println(line))
    val readLine: Task[String] = ZIO.attempt(StdIn.readLine())

    def nameProgram: ZIO[Any, Throwable, Unit] =
      for
        _    <- printLine("What is your name?")
        name <- readLine
        _    <- printLine(s"Hello, $name!")
      yield ()
  end Exercise4

  object Exercise5:
    import Exercise4.*
    val random: Task[Int] = ZIO.attempt(scala.util.Random.nextInt(3) + 1)

    def randomProgram: ZIO[Any, Throwable, Unit] =
      for
        number   <- random
        _        <- printLine("Guess a number 1 to 3:")
        response <- readLine
        _ <-
          if response == number.toString then printLine("You guessed right!")
          else printLine(s"Wrong! The number was $number.")
      yield ()
  end Exercise5

  object Exercise6:
    final case class ZIO[-R, +E, +A](run: R => Either[E, A])

    def zipWith[R, E, A, B, C](
        self: ZIO[R, E, A],
        that: ZIO[R, E, B]
    )(f: (A, B) => C): ZIO[R, E, C] =
      ZIO(r =>
        for
          a <- self.run(r)
          b <- that.run(r)
        yield f(a, b)
      )
  end Exercise6

  object Exercise7:
    import Exercise6.*

    def succeed[A](value: A): ZIO[Any, Nothing, A] = ZIO(_ => Right(value))

    def collectAll[R, E, A](in: Iterable[ZIO[R, E, A]]): ZIO[R, E, List[A]] =
      if in.isEmpty then succeed(List.empty[A])
      else zipWith(in.head, collectAll(in.tail))(_ :: _)
  end Exercise7

  object Exercise8:
    import Exercise6.*
    import Exercise7.*
    def foreach[R, E, A, B](
        in: Iterable[A]
    )(f: A => ZIO[R, E, B]): ZIO[R, E, List[B]] =
      collectAll(in.map(f))
  end Exercise8

  object Exercise9:
    import Exercise6.*

    def orElse[R, E1, E2, A](
        self: ZIO[R, E1, A],
        that: ZIO[R, E2, A]
    ): ZIO[R, E2, A] =
      ZIO(r =>
        self.run(r) match
          case Right(value) => Right(value)
          case Left(_)      => that.run(r)
      )
  end Exercise9

  object Exercise10:
    import Exercise1.*
    import Exercise4.*

    object Cat extends ZIOAppDefault:
      def run =
        for
          args <- getArgs
          _    <- cat(args)
        yield ()

      def cat(files: Iterable[String]): Task[Unit] =
        ZIO
          .foreach(files): file =>
            for
              content <- readFileZio(file)
              _       <- printLine(content)
            yield ()
          .unit
    end Cat
  end Exercise10

  object Exercise11:
    def eitherToZIO[E, A](either: Either[E, A]): ZIO[Any, E, A] =
      either match
        case Right(value) => ZIO.succeed(value)
        case Left(error)  => ZIO.fail(error)
  end Exercise11

  object Exercise12:
    def listToZIO[A](list: List[A]): ZIO[Any, None.type, A] =
      list match
        case Nil       => ZIO.fail(None)
        case head :: _ => ZIO.succeed(head)
  end Exercise12

  object Exercise13:
    def currentTime(): Long = java.lang.System.currentTimeMillis()

    lazy val currentTimeZIO: ZIO[Any, Nothing, Long] = ZIO.succeed(currentTime())
  end Exercise13

  object Exercise14:
    def getCacheValue(key: String, onSuccess: String => Unit, onFailure: Throwable => Unit): Unit = ???

    def getCacheValueZio(key: String): ZIO[Any, Throwable, String] =
      ZIO.async: callback =>
        getCacheValue(key, s => callback(ZIO.succeed(s)), e => callback(ZIO.fail(e)))
  end Exercise14

  object Exercise15:
    trait User

    def saveUserRecord(
        user: User,
        onSuccess: () => Unit,
        onFailure: Throwable => Unit
    ): Unit = ???

    def saveUserRecordZio(user: User): ZIO[Any, Throwable, Unit] =
      ZIO.async: callback =>
        saveUserRecord(user, () => callback(ZIO.unit), e => callback(ZIO.fail(e)))
  end Exercise15

  object Exercise16:
    import scala.concurrent.ExecutionContext
    import scala.concurrent.Future

    trait Query
    trait Result
    def doQuery(query: Query)(using ec: ExecutionContext): Future[Result] = ???
    def doQueryZio(query: Query): ZIO[Any, Throwable, Result] =
      ZIO.fromFuture(ec => doQuery(query)(ec))
  end Exercise16

  object Exercise17:
    object HelloHuman extends ZIOAppDefault:
      val run =
        for
          name <- Console.readLine("What's your name ?")
          _    <- Console.printLine(s"Hello $name !")
        yield ()
    end HelloHuman
  end Exercise17

  object Exercise18:
    object NumberGuessing extends ZIOAppDefault:
      val run =
        for
          number   <- Random.nextIntBounded(2).map(_ + 1)
          response <- Console.readLine("Guess a number 1 to 3:")
          _ <-
            if number.toString == response then Console.printLine("You guessed it right !")
            else Console.printLine("Wrong number !")
        yield ()
    end NumberGuessing
  end Exercise18

  object Exercise19:
    def readUntil(acceptInput: String => Boolean): ZIO[Any, IOException, String] =
      Console.readLine.flatMap: response =>
        if acceptInput(response) then ZIO.succeed(response) else readUntil(acceptInput)
  end Exercise19

  object Exercise20:
    def doWhile[R, E, A](body: ZIO[R, E, A])(condition: A => Boolean): ZIO[R, E, A] =
      body.flatMap: a =>
        if condition(a) then ZIO.succeed(a) else doWhile(body)(condition)
  end Exercise20

end FirstSteps
