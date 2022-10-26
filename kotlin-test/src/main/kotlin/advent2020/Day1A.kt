package advent2020

import java.io.BufferedReader
import java.io.FileReader
import java.util.*
import java.util.function.BiFunction
import java.util.function.IntPredicate
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream

fun read(file: String): IntStream {
    val reader = BufferedReader(FileReader(file))
    return reader.lines().map { it.trim() }.filter { it.isNotEmpty() }.mapToInt() { Integer.parseInt(it) }
}

data class Pair(val x: Int, val y: Int)

fun filterNot(x: Int): Predicate<Int> {
    return Predicate { y -> x != y }
}


fun allPairs(x: Int, stream: Stream<Int>): Stream<Pair> {
    return stream.filter(filterNot(x)).map { Pair(x, it) }
}


fun find(list: List<Int>): Int {
    val pairs: Stream<Pair> = list.stream().flatMap { allPairs(it, list.stream()) }
    return pairs.filter { it.x + it.y == 2020 }.findAny().map { it.x * it.y }.orElseThrow()
}

fun main(args: Array<String>) {
    val source = """
            1721
            979
            366
            299
            675
            1456
        """.trimIndent()
    val split: List<String> = source.split("\n")
    val stream = split.stream().map { it.trim() }.map { Integer.parseInt(it) }.toList()
    print(find(stream))
}
