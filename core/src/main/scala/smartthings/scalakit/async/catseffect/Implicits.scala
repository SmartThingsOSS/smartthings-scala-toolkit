package smartthings.scalakit.async.catseffect

import cats.effect.{Async, IO}
import smartthings.scat.core.async.AsyncForIO

object Implicits {

  implicit val asyncForIO: Async[IO] = AsyncForIO

}
