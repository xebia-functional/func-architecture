import kraken.free.interpreters.{UnsafeFutureInterpreter, ProdInterpreter, TestInterpreter}
import kraken.free.ops._
import kraken.services.services._
import rapture.core.Result
import scala.concurrent.ExecutionContext
import scala.language.{higherKinds, implicitConversions, reflectiveCalls}

import Service._

case class Converter() {
  def convert(x: String): Int = x.toInt
}

case class Adder() {
  def add(x: Int): Int = x + 1
}

case class Config(converter: Converter, adder: Adder)

val system = Config(Converter(), Adder())

def service1(x : String): Service[Converter, Int, NumberFormatException] = Service { converter: Converter =>
  ask(Result.catching[NumberFormatException]{ println("serv1 invoked"); converter.convert(x)})
}

def service2: Service[Adder, String, IllegalArgumentException] = Service { adder: Adder =>
  ask(Result.catching[IllegalArgumentException] { println("serv2 invoked"); adder.add(22) + " added " })
}

val composed: Service[Config, String, NumberFormatException with IllegalArgumentException] = for {
  a <- service1("1").liftD[Config]
  b <- service2.liftD[Config]
} yield a + b

composed.exec(system)(TestInterpreter)
composed.exec(system)(ProdInterpreter)
val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
implicit val futureMonad = scalaz.std.scalaFuture.futureInstance(ec)
composed.exec(system)(new UnsafeFutureInterpreter(ec))

