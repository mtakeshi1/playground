package advent2020

import java.io.BufferedReader
import java.io.FileReader
import java.util.function.Predicate
import java.util.stream.IntStream
import java.util.stream.Stream

object Day1A {
    fun read(file: String): IntStream {
        val reader = BufferedReader(FileReader(file))
        return reader.lines().map { it.trim() }.filter { it.isNotEmpty() }.mapToInt() { Integer.parseInt(it) }
    }

    data class Pair(val x: Int, val y: Int)

    fun find(list: List<Int>): Int {
        val pairs: Stream<Pair> = list.stream().flatMap {
            x -> list.stream().filter { y -> x != y }.map { y -> Pair(x, y) }
        }
        return pairs.filter { it.x + it.y == 2020 }.findAny().map { it.x * it.y }.orElseThrow()
    }

}

fun main(args: Array<String>) {
    var input = Day1A.read("kotlin-test/2020/day1a.txt").mapToObj { Integer.valueOf(it) }.toList()
    print(Day1A.find(input))
//    print(File(".").absolutePath)
}
