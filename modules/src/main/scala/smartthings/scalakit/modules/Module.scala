package smartthings.scalakit.modules

import cats.effect.Async

trait Module {

  def startup[F[_]](implicit async: Async[F]): F[Unit] = async.unit

  def shutdown[F[_]](implicit async: Async[F]): F[Unit] = async.unit

}
