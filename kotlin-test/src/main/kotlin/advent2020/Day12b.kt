package advent2020

import kotlin.math.abs

object Day12b : Solver {

    data class State(val x: Int, val y: Int, val waypoint: Pair<Int, Int> = Pair(1, 10)) {
        fun north(amount: Int) = copy(waypoint = waypoint.copy(first = waypoint.first + amount))
        fun south(amount: Int) = copy(waypoint = waypoint.copy(first = waypoint.first - amount))
        fun east(amount: Int) = copy(waypoint = waypoint.copy(second = waypoint.second + amount))
        fun west(amount: Int) = copy(waypoint = waypoint.copy(second = waypoint.second - amount))
        fun fwd(amount: Int) = copy(x = x + waypoint.first * amount, y = y + waypoint.second * amount)
        fun rotateR() = this.copy(waypoint = Pair(-waypoint.second, waypoint.first))
        fun rotateL() = this.copy(waypoint = Pair(waypoint.second, -waypoint.first))
    }

    data class Command(val ins: Char, val amount: Int) {
        fun apply(state: State): State {
            println(state)
            return when (ins) {
                'N' -> state.north(amount)
                'S' -> state.south(amount)
                'E' -> state.east(amount)
                'W' -> state.west(amount)
                'F' -> state.fwd(amount)
                'R' -> (0 until amount / 90).fold(state) { s, _ -> s.rotateR() }
                'L' -> (0 until amount / 90).fold(state) { s, _ -> s.rotateL() }
                else -> TODO()
            }
        }
    }

    fun parse(line: String): Command = Command(line[0], line.substring(1).toInt())

    override fun solve(input: List<String>): Any {
        val end = input.map { parse(it) }.fold(State(0, 0)) { state, ins -> ins.apply(state) }
        println(end)
        return abs(end.x) + abs(end.y)
    }

}

fun main() {
    println(
        Day12b.solve(
            """
        F10
        N3
        F7
        R90
        F11
    """.trimIndent()
        )
    )

    println(Day12b.solve("day12.txt"))

}
