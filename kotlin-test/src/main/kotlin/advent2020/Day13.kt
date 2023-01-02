package advent2020

import java.math.BigInteger

object Day13 : Solver {

    fun minTime(time: Int, busId: Int): Int {
        val div = time / busId
        return ((div + 1) * busId) - time
    }

    override fun solve(input: List<String>): Any {
        val time = input[0].toInt()
        val departures = input[1].split(",").filter { it != "x" }.map { it.toInt() }
//        val selected = departures.map { Pair(it, minTime(time, it)) }.minBy { it.second }
//        return selected.first * selected.second
        return ""
    }


    fun valid(time: Long, times: Iterable<IndexedValue<Int>>): Boolean {
        return times.all { ((time + it.index) % it.value) == 0L }
    }

    data class Entry(val initial: BigInteger, val factor: BigInteger)

    fun mod(v: BigInteger, m: BigInteger): BigInteger {
        if(v < BigInteger.ZERO) {
            return mod(v + m, m)
        } else if(v >= m) {
            return mod(v - m, m)
        } else return v
    }

    fun reduce(one: Entry, two: Entry): Entry {
        if (one.initial < BigInteger.ZERO || two.initial < BigInteger.ZERO) {
            throw RuntimeException()
        }
        var r = one.initial
        while ((r % two.factor) != two.initial) {
            r += one.factor
        }
        return Entry(r, one.factor * two.factor)
    }

    fun reduce2(one: Entry, two: Entry): Entry {
        var r = one.initial
        while ((r % two.factor) != two.initial) {
            r += one.factor
        }
        return Entry(r, one.factor * two.factor)
    }

    override fun solveb(input: List<String>): Any {
        val departures = input[1].split(",")
            .asSequence()
            .map { if (it == "x") 0 else it.toInt() }
            .withIndex()
            .filter { it.value != 0 }
            .sortedBy { it.value }
            .map {
                Entry(
                    mod(BigInteger.valueOf((it.value.toLong() - it.index)), BigInteger.valueOf(it.value.toLong())),
                    BigInteger.valueOf(it.value.toLong())
                )
            }
            .toList()
        val r = departures.drop(1).fold(departures.first()) { a, b -> reduce(a, b) }
        return r.initial
    }


}

fun main() {
//    println(Day13.solveb(listOf("939", "7,13,x,x,59,x,31,19" )))
    println(Day13.solveb(listOf("939", "67,7,59,61")))
    println(
        Day13.solveb(
            """
        1000655
        17,x,x,x,x,x,x,x,x,x,x,37,x,x,x,x,x,571,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,13,x,x,x,x,23,x,x,x,x,x,29,x,401,x,x,x,x,x,x,x,x,x,41,x,x,x,x,x,x,x,x,19
    """.trimIndent()
        )
    )
}