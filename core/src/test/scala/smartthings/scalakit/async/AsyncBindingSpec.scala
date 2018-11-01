package smartthings.scalakit.async

import cats.effect.Async
import org.scalatest.{AsyncFunSpec, FunSpec}

import scala.util.Success

class AsyncBindingSpec extends AsyncFunSpec {

  describe("Future Async Binding") {

    import Implicits.asyncForFuture

    it("should add numbers using binding on Future") {
      Test.addSometime(1, 2).map { sum =>
        assert(sum == 3)
      }
    }
  }

  describe("IO Async Binding") {

    import smartthings.scalakit.async.catseffect.Implicits.asyncForIO

    it("should add numbers using binding for IO") {
      Test.addSometime(1, 2).map { sum =>
        assert(sum == 3)
      }.unsafeToFuture()
    }
  }

}

class SyncBindingSpec extends FunSpec {

  describe("Sync Binding") {

    import Implicits.asyncFoId

    it("should add numbers synchronously") {
      assert(Test.addSometime(1, 2) == 3)
    }
  }

  describe("Sync Try Binding") {

    import Implicits.asyncForTry

    it("should add numbers for synchronous try") {
      assert(Test.addSometime(1, 2) == Success(3))
    }
  }

}


object Test {
  def addSometime[F[_]](a: Int, b: Int)(implicit async: Async[F]): F[Int] = {
    async.delay(a + b)
  }
}
