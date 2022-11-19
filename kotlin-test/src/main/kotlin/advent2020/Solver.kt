package advent2020

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

interface Solver {

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