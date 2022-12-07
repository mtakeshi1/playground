package advent2020

object D23 : Solver {

    fun round(cupOrder: List<Int>): List<Int> {
        val pickup = cupOrder.drop(1).take(3)
        val nextOrder = listOf(cupOrder.first()) + cupOrder.drop(4)
        var destinationCup = cupOrder.first() - 1
        while (!nextOrder.contains(destinationCup)) {
            if (destinationCup <= 0) destinationCup = 9
            else destinationCup--
        }
        val destinationIndex = nextOrder.indexOf(destinationCup) + 1
        val next = nextOrder.take(destinationIndex) + pickup + nextOrder.drop(destinationIndex)
        return next.drop(1) + next.first()
    }

    override fun solve(input: List<String>): Any {
        val i = input.first().map { it - '0' }
        val r = (0 until 100).fold(i) { a, _ -> round(a) }
//        return r.joinToString(separator = "")
        return (r + r).dropWhile { it != 1 }.take(9).drop(1).joinToString(separator = "")
    }

    override fun solveb(input: List<String>): Any {
        val i = input.first().map { it - '0' } + (10..1000000)
        val r = (0 until 10000000).fold(i) { a, c -> round(a) }
//        return r.joinToString(separator = "")
        return (r + r).dropWhile { it != 1 }.take(3).drop(1).map { it.toLong() }.reduce{a, b -> a * b }
    }


}

fun main() {
    println(D23.solve("389125467") == "67384529")
    println(D23.solveb("925176834"))
}
