package advent2020

import java.util.*
import kotlin.collections.ArrayList

object Day10 {

    data class Node(val jolts: Int, val next: Node?) {
        constructor(j: Int): this(j, null)
        fun preprend(v: Int): Node = Node(v, this)

        fun toList(l: MutableList<Int> = ArrayList()): List<Int> {
            l.add(jolts)
            if(next == null) {
                return l
            }
            return next.toList(l)
        }

    }

    fun findCombination(inputs: List<Int>, last: Int = 0, acc: Node? = Node(0)): Node? {
        val next = inputs.filter { j -> last in j-3..j }
        next.forEach { j ->
            val filtered = inputs.minus(j)
            val nacc = Node(j, acc)
            if(filtered.isEmpty()) {
                return nacc
            }
            val nn = findCombination(filtered, j, nacc)
            if(nn != null) {
                return nn
            }
        }
        return null
    }

    fun collect(n: Node?, results: Array<Int> = Array(4){if(it == 3) 1 else 0}): Array<Int> {
        if(n?.next == null) {
            return results
        }
        val diff = Math.abs(n.jolts - n.next.jolts)
        results[diff]++
        return collect(n.next, results)
    }

    fun resolveA(input: List<String>): Int {
        val r = findCombination(input.map { Integer.parseInt(it) })
        val rr = collect(r)
        return rr[1] * rr[3]
    }

    fun resolveA2(input: List<String>): Int {
        val ll = input.map { Integer.parseInt(it) }.sorted()
        val results =Array(4){if(it == 3) 1 else 0}
        var last = 0
        for (i in ll) {
            val diff = Math.abs(last - i)
            results[diff]++
            last = i
        }
        return results[1] * results[3]
    }

}

fun main() {
    val sample = """
        16
        10
        15
        5
        1
        11
        7
        19
        6
        12
        4
    """.trimIndent().split("\n")
    println(Day10.resolveA2(sample))

    val sample2 = """
        28
        33
        18
        42
        31
        14
        46
        20
        48
        47
        24
        23
        49
        45
        19
        38
        39
        11
        1
        32
        25
        35
        8
        17
        7
        9
        4
        2
        34
        10
        3
    """.trimIndent().split("\n")
    println(Day10.resolveA2(sample2))

    println(Day10.resolveA2(Help.read("2020/day10.txt").toList()))

}