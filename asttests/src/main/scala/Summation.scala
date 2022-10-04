object Summation {

  trait Monad[F[_]] {
    def pure[A](a: A): F[A]

    def flatMap[A, B](a: F[A])(f: A => F[B]): F[B]
  }

  object OptionMonad extends Monad[Option] {

    override def pure[A](a: A): Option[A] = Some(a)

    override def flatMap[A, B](a: Option[A])(f: A => Option[B]): Option[B] = a match {
      case Some(v) => f(v)
      case None => None
    }
  }

  object ListMonad extends Monad[List] {
    override def pure[A](a: A): List[A] = List(a)

    override def flatMap[A, B](a: List[A])(f: A => List[B]): List[B] = a match {
      case head :: tail => f(head) ::: flatMap(tail)(f)
      case List() => List()
    }
  }




}
