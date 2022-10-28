package advent2020

import java.util.stream.Collectors

object Day6 {

    fun count(group: List<String>): Int {
        val s = HashSet<Char>()
        group.forEach { it.forEach { s.add(it) }}
        return s.size
    }


    fun countAll(group: List<String>): Int {
        val s = HashSet<Char>()
        group.forEach { it.forEach { s.add(it) }}
        return s.filter { yes -> group.all { it.contains(yes) } }.count()
    }
}

fun main() {
    fun parse(input: String): List<List<String>> = input.split("\n\n").map { it.split("\n") }
    val sample = """
        abc

        a
        b
        c

        ab
        ac

        a
        a
        a
        a

        b
    """.trimIndent()
    println(parse(sample).sumOf { Day6.countAll(it) })

    val input = Help.read("2020/day6.txt").collect(Collectors.joining("\n"))
    println(parse(input).sumOf { Day6.countAll(it) })
}
