import MonadicLogging.FreeMonad.PlainInterpreter

object MonadicLogging {


  object FreeMonad {

    case class MMap[I,O](val from: Step[I], f: I => O) extends Step[O]
    case class FlatMap[I,O](val from: Step[I], f: I => Step[O]) extends Step[O]

    case class Loop[T](iterations: Int, zero: T, step: Step[T]) extends Step[T]
    case class Fold[T](iterations: Int, zero: T, func: (Int, T) => T) extends Step[T]

    case class Pure[T](val v: T) extends Step[T]

    trait Step[T] {
      def map[O](f: T => O): Step[O] = MMap(this, f)
      def flatMap[O](f: T => Step[O]): Step[O] = FlatMap(this, f)
    }

    trait Interpreter {
      def run[T](step: Step[T]): T
      def runwith[T](interpreter: Interpreter)(step: Step[T]) = {
        step match {
          case FlatMap(from, monad) => {
            val inter = interpreter.run(from)
            val r = monad(inter)
            interpreter.run(r)
          }
          case Pure(t) => t
          case Fold(its, zero, func) => {
            0.until(its).foldRight(zero)(func)
          }
          case Loop(its, zero, step) => {
            0.until(its).map( _ => run(step)).headOption.getOrElse(zero)
          }
          case MMap(from, functor) => functor(run(from))
          case _ => ???
        }
      }
    }

    object PlainInterpreter {
      def run[T](step: Step[T]): T = {
        step match {
          case FlatMap(from, monad) => {
            val inter = run(from)
            val r = monad(inter)
            run(r)
          }
          case Pure(t) => t
          case Fold(its, zero, func) => {
            0.until(its).foldRight(zero)(func)
          }
          case Loop(its, zero, step) => {
            0.until(its).map( _ => run(step)).headOption.getOrElse(zero)
          }
          case MMap(from, functor) => functor(run(from))
          case _ => ???
        }
      }
    }

    def program(start: Int, iterations: Int): Step[Int] = {
      for {
        initial <- Pure(start)
        loopEnd <- Fold(iterations, initial, (i, pre) => pre + 2022)
      } yield loopEnd
    }
  }

  def main(args: Array[String]): Unit = {
    println(PlainInterpreter.run(FreeMonad.program(1,5)))
  }

  object Plain {
    def log(s: String *): Unit = println(s.mkString(" "))
    def progress(i: Int, max: Int): Unit = {
      val perc = 100.0 * i.toDouble / max
      println(s"progress: $perc")
    }

    def program(start: Int, iterations: Int): Int = {
      log("beggining")
      var loop = start;
      progress(0, iterations)
      for(i <- start.until(iterations)) {
        loop += 2022
        progress(i, iterations)
      }
      progress(iterations, iterations)
      log("done it")
      return loop
    }
  }

}
