package advent2020

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

interface Solver {

    fun List<String>.splitOnEmpty(): Pair<List<String>, List<String>> {
        val first = this.takeWhile { it.isNotEmpty() }
        val second = this.drop(first.size + 1)
        return Pair(first, second)
    }

    fun List<String>.partitionOnEmpty(): List<List<String>> {
        val head = listOf(this.takeWhile { it.isNotEmpty() })
        val rest = this.drop(head[0].size+1)
        return if(rest.isEmpty()) head else head + rest.partitionOnEmpty()
    }

    fun List<String>.ints(): List<Int> = intsN().map { it.first() }
    fun List<String>.ints2(): List<Pair<Int, Int>>  = intsN().map { Pair(it[0], it[1]) }
    fun List<String>.ints3(): List<Triple<Int, Int, Int>> = intsN().map { Triple(it[0], it[1], it[1]) }

    fun List<String>.intsN(): List<List<Int>> {
        val re = Regex("(-?[0-9]+)")
        return this.map { re.findAll(it).toList().map { found -> found.value.toInt() } }
    }

    fun <A> List<List<A>>.transposed(): List<List<A>> {
        return this[0].indices.map { col -> this.indices.map { row -> this[row][col] } }
    }

    fun List<String>.join(): String = this.joinToString(separator = "")

    fun <A> List<A>.splitAt(index: Int): List<List<A>> = listOf(this.take(index), this.drop(index))

    fun solveb(input: String): Any {
        if (File(input).exists()) {
            val reader = BufferedReader(FileReader(input))
            return solveb(reader.lines().map { it.trim() }.toList())
        } else if (File("2020", input).exists()) {
            val reader = BufferedReader(FileReader(File("2020", input)))
            return solveb(reader.lines().map { it.trim() }.toList())
        } else if (File("kotlin-test/2020", input).exists()) {
            val reader = BufferedReader(FileReader(File("kotlin-test/2020", input)))
            return solveb(reader.lines().map { it.trim() }.toList())
        }
        return solveb(input.split("\n").toList())
    }

    fun solveb(input: List<String>): Any = solveIb(input.map { Integer.parseInt(it) })
    fun solveIb(input: List<Int>): Any = TODO()

    fun solve(input: String): Any {
        if (File(input).exists()) {
            val reader = BufferedReader(FileReader(input))
            return solve(reader.lines().map { it.trim() }.toList())
        } else if (File("2020", input).exists()) {
            val reader = BufferedReader(FileReader(File("2020", input)))
            return solve(reader.lines().map { it.trim() }.toList())
        } else if (File("kotlin-test/2020", input).exists()) {
            val reader = BufferedReader(FileReader(File("kotlin-test/2020", input)))
            return solve(reader.lines().map { it.trim() }.toList())
        }
        return solve(input.split("\n").toList())
    }

    fun solve(input: List<String>): Any = solveI(input.map { Integer.parseInt(it) })
    fun solveI(input: List<Int>): Any = TODO()
}