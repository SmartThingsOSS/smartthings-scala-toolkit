package smartthings.scalakit.async.catseffect

import cats.effect.{Async, IO}
import smartthings.scalakit.async.AsyncForIO

object Implicits {

  implicit val asyncForIO: Async[IO] = AsyncForIO

}
