import TypeSystemNumber.{::, Sum}

import java.lang.annotation.Native
import scala.annotation.targetName

object TypeSystemNumber {


  trait Natural

  class Zero extends Natural

  class Next[N <: Natural] extends Natural

  type _1 = Next[Zero]
  type _2 = Next[_1]
  type _3 = Next[_2]
  type _4 = Next[_3]
  type _5 = Next[_4]
  type _6 = Next[_5]

  @targetName("<")
  trait <[A <: Natural, B <: Natural]

  object LT {
    given lessThan[B <: Natural]: <[Zero, Next[B]] with {}

    given genericLessThan[A <: Natural, B <: Natural] (using <[A, B]): <[Next[A], Next[B]] with {}

    def apply[A <: Natural, B <: Natural](using lt: <[A, B]): A < B = lt
  }

  @targetName("<=")
  trait <=[A <: Natural, B <: Natural]

  object LE {
    given lessThan[B <: Natural]: <=[Zero, B] with {}

    given genericLessThan[A <: Natural, B <: Natural] (using <=[A, B]): <=[Next[A], Next[B]] with {}

    def apply[A <: Natural, B <: Natural](using lt: <=[A, B]): A <= B = lt
  }

  trait Sum[A <: Natural, B <: Natural, R <: Natural]

  object Sum {
    given zeroSum: Sum[Zero, Zero, Zero] with {}

    given zeroSum1[A <: Natural] (using <[Zero, A]): Sum[Zero, A, A] with {}

    given zeroSum2[A <: Natural] (using <[Zero, A]): Sum[A, Zero, A] with {}

    given genericSum[A <: Natural, B <: Natural, S <: Natural] (using Sum[A, B, S]): Sum[Next[A], Next[B], Next[Next[S]]] with {}

    def apply[A <: Natural, B <: Natural, R <: Natural](using sum: Sum[A, B, R]): Sum[A, B, R] = sum
  }


  trait Minus[A <: Natural, B <: Natural, R <: Natural]

  object Minus {
    given zeroMinus: Minus[Zero, Zero, Zero] with {}

    given zeroMinus1[A <: Natural] (using <[Zero, A]): Minus[A, Zero, A] with {}

    given genericMinus[A <: Natural, B <: Natural, S <: Natural] (using Minus[A, B, S]): Minus[Next[Next[A]], Next[B], Next[S]] with {}

    def apply[A <: Natural, B <: Natural, R <: Natural](using min: Minus[A, B, R]): Minus[A, B, R] = min

  }


  trait HList

  class EmptyList extends HList

  @targetName("::")
  infix class ::[V <: Natural, T <: HList] extends HList

  trait Length[H <: HList, A <: Natural]

  object Length {
    given emptyList: Length[EmptyList, Zero] with {}

    given inductive[A <: Natural, H <: HList, L <: Natural] (using Length[H, L]): Length[A :: H, Next[L]] with {}

    def apply[H <: HList, A <: Natural](using len: Length[H, A]): Length[H, A] = len
  }

  trait Sorted[A <: HList]

  object Sorted {
    given emptySorted: Sorted[EmptyList] with {}

    given singleElementSorted[A <: Natural]: Sorted[A :: EmptyList] with {}

    given inductive[A <: Natural, B <: Natural, T <: HList] (using <[A, B], Sorted[B :: T]): Sorted[A :: B :: T] with {}

    def apply[A <: HList](using sorted: Sorted[A]): Sorted[A] = sorted
  }

  trait Split[IN <: HList, L <: HList, R <: HList]

  object Split {

    import LT.given
    import LE.given
    import Length.given

    given emptySplit: Split[EmptyList, EmptyList, EmptyList] with {}

    given singleList[A <: Natural]: Split[A :: EmptyList, A :: EmptyList, EmptyList] with {}

    given inductive[EL1 <: Natural, EL2 <: Natural, L <: HList, R <: HList, T <: HList] (using Split[T, L, R]): Split[EL1 :: EL2 :: T, EL1 :: L, EL2 :: R] with {}

//    val test0 = summon[Split[Zero :: EmptyList, Zero :: EmptyList, EmptyList]]
//    val test1 = summon[Split[Zero :: Zero :: Zero :: EmptyList, Zero :: Zero :: EmptyList, Zero :: EmptyList]]
  }

  trait Merge[L <: HList, R <: HList, OUT <: HList]

  object Merge {

    import LT.given
    import LE.given

    given empty: Merge[EmptyList, EmptyList, EmptyList] with {}

    given singleLeft[A <: HList]: Merge[A, EmptyList, A] with {}

    given singleRight[A <: HList]: Merge[EmptyList, A, A] with {}

    given fromLeft[LH <: Natural, RH <: Natural, L <: HList, R <: HList, T <: HList]
    (using
     LH <= RH,
     Merge[L, RH :: R, T]
    ): Merge[LH :: L, RH :: R, LH :: T] with {}

    given fromRight[LH <: Natural, RH <: Natural, L <: HList, R <: HList, T <: HList]
    (using
     RH < LH,
     Merge[LH :: L, R, T]
    ): Merge[LH :: L, RH :: R, RH :: T] with {}

    def letsTry(): Unit = {
      import LT.given
      import LE.given
      import Length.given
      val le = LE.genericLessThan[_1, _2]
      val rsplit: Merge[EmptyList, _2 :: EmptyList, _2 :: EmptyList] = singleRight[_2 :: EmptyList]
      val ll = fromLeft[_1, _2, EmptyList, EmptyList, _2 :: EmptyList]
      val auto = summon[Merge[_1 :: EmptyList, _2 :: EmptyList, _1 :: _2 :: EmptyList]]
    }

  }

  trait MergeSort[I <: HList, O <: HList]

  object MergeSort {
    given emptyList: MergeSort[EmptyList, EmptyList] with {}

    given singleElement[A <: Natural]: MergeSort[A :: EmptyList, A :: EmptyList] with {}

    given inductive[I <: HList, L <: HList, SortedL <: HList, SortedR <: HList, R <: HList, O <: HList]
    (
      using Split[I, L, R],
      MergeSort[L, SortedL],
      MergeSort[R, SortedR],
      Merge[SortedL, SortedR, O]
    ): MergeSort[I, O] with {}

  }

  @main
  def main(): Unit = {
    import LT.given
    import Sum.given
    import Length.given

    Sum.apply[Zero, Zero, Zero]
    Sum.apply[Zero, _1, _1]
    Sum.apply[_2, _1, _3]

    Minus.apply[_3, _1, _2]

    Length.apply[EmptyList, Zero]
    Length.apply[Zero :: EmptyList, _1]
    Length.apply[Zero :: Zero :: EmptyList, _2]
    Length.apply[Zero :: Zero :: _1 :: EmptyList, _3]

    Sorted.apply[Zero :: _1 :: EmptyList]

    //    val b = summon[Sum[_1, _1, _2]]
    //    val c = summon[Sum[_2, _2, _4]]
    //    val lt = summon[<[Zero, _1]]
    //    val lt1 = summon[<[_1, _2]]
    //
    //    val minus1 = summon[Minus[_1, Zero, _1]]
    //    val minus2 = summon[Minus[_2, _1, _1]]
    //
    //    val sortedList = summon[Sorted[Zero :: _1 :: EmptyList]]
  }


}
