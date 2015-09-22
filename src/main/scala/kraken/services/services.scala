package kraken.services

import kraken.free.ops._
import rapture.core.Result
import rapture.core.scalazInterop.ResultT
import shapeless.ops.hlist.Selector

import scala.language.{higherKinds, implicitConversions, reflectiveCalls}
import scala.reflect.ClassTag
import scalaz._

object services {

  import shapeless._

  type ServiceDef[D, A, B <: Exception] = ResultT[({type λ[α] = ReaderT[OpMonad, D, α]})#λ, A, B]

  case class Service[D, A, B <: Exception : ClassTag](svc: ServiceDef[D, A, B]) {

    import Service._

    def exec[F[_] : Monad](dependencies: D)(interpreter: (Op ~> F)) = {
      Free.runFC(svc.run.run(dependencies))(interpreter)
    }

    def flatMap[AA, BB <: Exception : ClassTag](fa: A => Service[D, AA, BB]): Service[D, AA, B with BB] = {
      Service[D, AA, B with BB](svc.flatMap[AA, BB](a => fa(a).svc))
    }

    def map[AA](fn: A => AA): Service[D, AA, B] = Service[D, AA, B](svc map fn)

  }

  implicit class ServiceOps[D, A, B <: Exception : ClassTag, L <: HList](f : Service[D, A, B]) {

    def liftD[DD](implicit ga: Generic.Aux[DD, L], sel: Selector[L, D]): Service[DD, A, B] = {
      Service(ResultT[({type λ[α] = ReaderT[OpMonad, DD, α]})#λ, A, B](f.svc.run.local[DD]{ dd : DD =>
            sel.apply(ga.to(dd))
        }))
    }


  }

  object Service {

    def apply[D, A, B <: Exception : ClassTag](f: D => OpMonad[Result[A, B]]): Service[D, A, B] = {
      Service[D, A, B](ResultT[({type λ[α] = ReaderT[OpMonad, D, α]})#λ, A, B](Kleisli.kleisli[OpMonad, D, Result[A, B]](f)))
    }

    implicit def kleisliMonad[D]: Monad[({type λ[α] = ReaderT[OpMonad, D, α]})#λ] =
      Kleisli.kleisliMonadReader[OpMonad, D]

    implicit def kleisliFunctor[D]: Functor[({type λ[α] = ReaderT[OpMonad, D, α]})#λ] =
      Kleisli.kleisliFunctor[OpMonad, D]


    implicit def conversion[DD, D, A, B <: Exception : ClassTag, L <: HList]
      (s : Service[D, A, B])
          (implicit genericAux: Generic.Aux[DD, L], selector: Selector[L, D]): Service[DD, A, B] = {
      s.liftD[DD]
    }

  }


}
