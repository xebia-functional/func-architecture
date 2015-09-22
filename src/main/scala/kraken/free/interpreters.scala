package kraken.free

import kraken.free.ops.Op
import kraken.free.ops.Op.{Ask, Async, Tell}

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import scalaz.concurrent.Task


object interpreters {

  object ProdInterpreter extends (Op ~> Task) {
    def apply[A](op: Op[A]) = {
      println(s"Running ProdInterpreter interpreter")
      op match {
        case Ask(a) => Task.now(a())
        case Async(a) => Task.fork(Task.delay(a()))
        case Tell(a) => Task.now(a())
      }
    }
  }

  class UnsafeFutureInterpreter(ec : ExecutionContext) extends (Op ~> Future) {
    def apply[A](op: Op[A]) = {
      println(s"Running ProdInterpreter interpreter")
      op match {
        case Ask(a) => Future.successful(a())
        case Async(a) => Future(a())(ec)
        case Tell(a) => Future.successful(a())
      }
    }
  }

  object TestInterpreter extends (Op ~> Id.Id) {
    def apply[A](op: Op[A]) = {
      println(s"Running Test interpreter")
      op match {
        case Ask(a) => a()
        case Async(a) => a()
        case Tell(a) => a()
      }
    }
  }

}
