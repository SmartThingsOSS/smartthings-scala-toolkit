package smartthings.scalakit.monix.async

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.duration._

class AsyncForTaskSpec extends FunSpec with Matchers {

  describe("AsyncForTask") {
    it("should create task from pure") {
      val result = AsyncForTask.pure(0)

      result.isInstanceOf[Task[Int]] shouldBe true
      result.runSyncUnsafe(1.second) shouldBe 0
    }

    it("should create task from async") {
      val result = AsyncForTask.async[Int] { cb =>
        cb(Right(1))
      }

      result.isInstanceOf[Task[Int]] shouldBe true
      result.runSyncUnsafe(1.second) shouldBe 1
    }

    it("should create task from asyncF") {
      val result = AsyncForTask.asyncF[Int] { cb =>
        Task.delay(cb(Right(2)))
      }

      result.isInstanceOf[Task[Int]] shouldBe true
      result.runSyncUnsafe(1.second) shouldBe 2
    }

    it("should create task from delay") {
      val result = AsyncForTask.delay[Int](3)

      result.isInstanceOf[Task[Int]] shouldBe true
      result.runSyncUnsafe(1.second) shouldBe 3
    }

    it("should create task from suspend") {
      val result = AsyncForTask.suspend[Int](Task.pure(4))

      result.isInstanceOf[Task[Int]] shouldBe true
      result.runSyncUnsafe(1.second) shouldBe 4
    }

    it("should create task from flatMap") {
      val result = AsyncForTask.flatMap(Task.pure(1)) { i =>
        Task.pure(i + 4)
      }

      result.isInstanceOf[Task[Int]] shouldBe true
      result.runSyncUnsafe(1.second) shouldBe 5
    }

    it("should create task from tailRecM") {
      val result = AsyncForTask.tailRecM(5) { i =>
        Task.pure(Right(i + 1))
      }

      result.isInstanceOf[Task[Int]] shouldBe true
      result.runSyncUnsafe(1.second) shouldBe 6
    }

    it("should create task from raiseError") {
      val result = AsyncForTask.raiseError[Int](new Exception("e"))

      result.isInstanceOf[Task[Int]] shouldBe true
      assertThrows[Exception](result.runSyncUnsafe(1.second))
    }

    it("should create task from handleErrorWith") {
      val result = AsyncForTask.handleErrorWith(Task.raiseError(new Exception("e"))) { t =>
        Task(7)
      }

      result.isInstanceOf[Task[Int]] shouldBe true
      result.runSyncUnsafe(1.second) shouldBe 7
    }

    it("should create task from bracketCase") {
      val result = AsyncForTask.bracketCase(Task(1)) { i =>
        Task(i + 7)
      } { (_, _) =>
        Task.unit
      }

      result.isInstanceOf[Task[Int]] shouldBe true
      result.runSyncUnsafe(1.second) shouldBe 8
    }
  }


}
