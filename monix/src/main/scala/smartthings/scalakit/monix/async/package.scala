/*
 * Copyright 2018 SmartThings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package smartthings.scalakit.monix

import cats.effect.{Async, ExitCase}
import monix.eval.Task

package object async {

  object AsyncForTask extends Async[Task] {
    override def async[A](k: (Either[Throwable, A] => Unit) => Unit): Task[A] = Task.async(k)

    override def asyncF[A](k: (Either[Throwable, A] => Unit) => Task[Unit]): Task[A] = Task.asyncF(k)

    override def suspend[A](thunk: =>Task[A]): Task[A] = Task.suspend(thunk)

    override def bracketCase[A, B](acquire: Task[A])(use: A => Task[B])(release: (A, ExitCase[Throwable]) => Task[Unit]): Task[B] =
      acquire.bracketCase(use)(release)

    override def raiseError[A](e: Throwable): Task[A] = Task.raiseError(e)

    override def handleErrorWith[A](fa: Task[A])(f: Throwable => Task[A]): Task[A] =
      fa.onErrorHandleWith(f)

    override def flatMap[A, B](fa: Task[A])(f: A => Task[B]): Task[B] = fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => Task[Either[A, B]]): Task[B] =
      flatMap(f(a)) {
        case Right(b) => pure(b)
        case Left(nextA) => tailRecM(nextA)(f)
      }

    override def pure[A](x: A): Task[A] = Task.pure(x)
  }

}
