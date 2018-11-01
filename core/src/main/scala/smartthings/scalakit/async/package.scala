package smartthings.scalakit

import cats.Id
import cats.effect.{Async, ExitCase, IO}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

package object async {

  object AsyncForTry extends Async[Try] {
    override def async[A](k: (Either[Throwable, A] => Unit) => Unit): Try[A] = {
      val promise = Promise[A]()
      k {
        case Left(e) => promise.failure(e)
        case Right(x) => promise.success(x)
      }
      Try(Await.result(promise.future, Duration.Inf))
    }

    override def asyncF[A](k: (Either[Throwable, A] => Unit) => Try[Unit]): Try[A] = {
      val promise = Promise[A]()
      k {
        case Left(e) => promise.failure(e)
        case Right(x) => promise.success(x)
      }
      Try(Await.result(promise.future, Duration.Inf))
    }

    override def suspend[A](thunk: => Try[A]): Try[A] = thunk

    override def bracketCase[A, B](acquire: Try[A])(use: A => Try[B])(release: (A, ExitCase[Throwable]) => Try[Unit]): Try[B] = ???

    override def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => Try[Either[A, B]]): Try[B] = {
      flatMap(f(a)) {
        case Right(b) => pure(b)
        case Left(nextA) => tailRecM(nextA)(f)
      }
    }

    override def raiseError[A](e: Throwable): Try[A] = Failure(e)

    override def handleErrorWith[A](fa: Try[A])(f: Throwable => Try[A]): Try[A] =
      fa.recoverWith {
        case NonFatal(e) => f(e)
      }

    override def pure[A](x: A): Try[A] = Success(x)
  }

  object AsyncForIO extends Async[IO] {
    override def async[A](k: (Either[Throwable, A] => Unit) => Unit): IO[A] = IO.async(k)

    override def asyncF[A](k: (Either[Throwable, A] => Unit) => IO[Unit]): IO[A] = IO.asyncF(k)

    override def suspend[A](thunk: => IO[A]): IO[A] = thunk

    override def bracketCase[A, B](acquire: IO[A])(use: A => IO[B])(release: (A, ExitCase[Throwable]) => IO[Unit]): IO[B] = ???

    override def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] =
      fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => IO[Either[A, B]]): IO[B] = {
      flatMap(f(a)) {
        case Right(b) => pure(b)
        case Left(nextA) => tailRecM(nextA)(f)
      }
    }

    override def raiseError[A](e: Throwable): IO[A] = IO.raiseError(e)

    override def handleErrorWith[A](fa: IO[A])(f: Throwable => IO[A]): IO[A] =
      fa.handleErrorWith(f)

    override def pure[A](x: A): IO[A] = IO.pure(x)
  }

  class AsyncForFuture(implicit ec: ExecutionContext) extends Async[Future] {
    override def async[A](k: (Either[Throwable, A] => Unit) => Unit): Future[A] = {
      val promise = Promise[A]()
      k {
        case Left(e) => promise.failure(e)
        case Right(x) => promise.success(x)
      }
      promise.future
    }

    override def asyncF[A](k: (Either[Throwable, A] => Unit) => Future[Unit]): Future[A] = {
      val promise = Promise[A]()
      k {
        case Left(e) => promise.failure(e)
        case Right(x) => promise.success(x)
      }
      promise.future
    }

    override def suspend[A](thunk: => Future[A]): Future[A] = thunk

    override def bracketCase[A, B](acquire: Future[A])(use: A => Future[B])(release: (A, ExitCase[Throwable]) => Future[Unit]): Future[B] = ???

    override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = {
      fa.flatMap(f)
    }

    override def tailRecM[A, B](a: A)(f: A => Future[Either[A, B]]): Future[B] = {
      flatMap(f(a)) {
        case Right(b) => pure(b)
        case Left(nextA) => tailRecM(nextA)(f)
      }
    }

    override def raiseError[A](e: Throwable): Future[A] = Future.failed(e)

    override def handleErrorWith[A](fa: Future[A])(f: Throwable => Future[A]): Future[A] = {
      fa.recoverWith {
        case NonFatal(e) => f(e)
      }
    }

    override def pure[A](x: A): Future[A] = Future.successful(x)
  }

  object AsyncForId extends Async[Id] {
    override def async[A](k: (Either[Throwable, A] => Unit) => Unit): Id[A] = {
      val promise = Promise[A]()
      k {
        case Left(e) => promise.failure(e)
        case Right(x) => promise.success(x)
      }
      Await.result(promise.future, Duration.Inf)
    }

    override def asyncF[A](k: (Either[Throwable, A] => Unit) => Id[Unit]): Id[A] = async(k)

    override def suspend[A](thunk: => Id[A]): Id[A] = thunk

    override def bracketCase[A, B](acquire: Id[A])(use: A => Id[B])(release: (A, ExitCase[Throwable]) => Id[Unit]): Id[B] = ???

    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = f(fa)

    override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] = {
      flatMap(f(a)) {
        case Right(b) => pure(b)
        case Left(nextA) => tailRecM(nextA)(f)
      }
    }

    override def raiseError[A](e: Throwable): Id[A] = throw e

    override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] = {
      try {
        fa
      } catch {
        case NonFatal(e) => f(e)
      }
    }

    override def pure[A](x: A): Id[A] = x
  }

}
