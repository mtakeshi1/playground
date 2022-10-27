package advent2020

import java.io.BufferedReader
import java.io.FileReader
import java.util.stream.IntStream
import java.util.stream.Stream


object Day1B {
    fun read(file: String): IntStream {
        val reader = BufferedReader(FileReader(file))
        return reader.lines().map { it.trim() }.filter { it.isNotEmpty() }.mapToInt() { Integer.parseInt(it) }
    }


    fun findBetter(list: List<Int>): Int {
        return list.stream().flatMap { x ->
            list.stream().flatMap { y ->
                list.stream().map { z -> Triple(x, y, z) }
            }
        }.filter { it.first + it.second + it.third == 2020 }.mapToInt { it.first * it.second * it.third }.findFirst().orElseThrow()
    }

    fun find(list: List<Int>): Int {
        for (xi in 1 until list.size - 2) {
            for (yi in xi until (list.size - 1)) {
                for (zi in yi until (list.size)) {
                    val x = list[xi]
                    val y = list[yi]
                    val z = list[zi]
                    if (x + y + z == 2020) {
                        return x * y * z;
                    }
                }
            }
        }
        throw RuntimeException("not found")
    }

}

fun main(args: Array<String>) {
    val input = Day1B.read("kotlin-test/2020/day1a.txt").mapToObj { Integer.valueOf(it) }.toList()
//    val input = """
//        1721
//        979
//        366
//        299
//        675
//        1456
//    """.trimIndent().split("\n").map { Integer.parseInt(it) }.toList()
    print(Day1B.find(input))
//    print(File(".").absolutePath)
}
