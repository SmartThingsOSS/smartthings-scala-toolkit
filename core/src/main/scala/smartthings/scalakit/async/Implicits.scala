package smartthings.scalakit.async

import cats.Id
import cats.effect.Async

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object Implicits {

  implicit val asyncFoId: Async[Id] = AsyncForId

  implicit val asyncForTry: Async[Try] = AsyncForTry

  implicit def asyncForFuture(implicit ec: ExecutionContext): Async[Future] = new AsyncForFuture()

}
