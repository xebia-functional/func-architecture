package kraken.free

import scala.language.reflectiveCalls
import scalaz.{Coyoneda, Free, Monad}

object ops {

  sealed trait Op[A]

  object Op {

    case class Ask[A](a: () => A) extends Op[A]

    case class Async[A](a: () => A) extends Op[A]

    case class Tell[A](a: () => A) extends Op[A]

  }

  import Op._

  type OpMonad[A] = Free.FreeC[Op, A]

  implicit val MonadOp: Monad[OpMonad] = Free.freeMonad[({type λ[α] = Coyoneda[Op, α]})#λ]

  def ask[A](a: => A): OpMonad[A] = Free.liftFC(Ask(() => a))

  def async[A](a: => A): OpMonad[A] = Free.liftFC(Async(() => a))

  def tell(a: => Unit): OpMonad[Unit] = Free.liftFC(Tell(() => a))

}
