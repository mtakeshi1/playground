package advent2020

import java.lang.RuntimeException

object Day11b : Solver {

    enum class State(val c: Char) {
        FLOOR('.'), EMPTY('L'), OCCUPIED('#');

        companion object {
            fun parse(c: Char): State {
                return when (c) {
                    FLOOR.c -> FLOOR
                    EMPTY.c -> EMPTY
                    OCCUPIED.c -> OCCUPIED
                    else -> throw RuntimeException()
                }
            }

            fun parse(s: String): List<State> = s.toCharArray().map { parse(it) }
        }

        override fun toString(): String {
            return c.toString()
        }

    }

    fun countOccupied(input: List<List<State>>, row: Int, col: Int, direction: Pair<Int, Int>): Int {
        if (row < 0 || row >= input.size || col < 0 || col >= input[row].size) return 0
        return when (input[row][col]) {
            State.OCCUPIED -> 1
            State.EMPTY -> 0
            else -> countOccupied(input, row + direction.first, col + direction.second, direction)
        }
    }

    fun countOccupied(input: List<List<State>>, row: Int, col: Int): Int {
        val directions = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1),               Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )
        return directions.map { countOccupied(input, row+it.first, col+it.second, it) }.sum()
    }


    fun nextState(input: List<List<State>>, row: Int, col: Int): State {
        val current = input[row][col]
        val occ = countOccupied(input, row, col)
        return when (current) {
            State.EMPTY -> if (occ == 0) State.OCCUPIED else State.EMPTY
            State.OCCUPIED -> if (occ >= 5) State.EMPTY else State.OCCUPIED
            else -> current
        }
    }

    fun nextState(input: List<List<State>>): List<List<State>> {
        return (0 until input.size).map { row ->
            (0 until input[row].size).map { col ->
                nextState(input, row, col)
            }
        }
    }


    override fun solve(input: List<String>): Any {
        var state = input.map { State.parse(it) }
//        var state1 = nextState(state)
//        println(format(state1))
//        println("-----------------------------------------")
//        var state2 = nextState(state1)
//        println(format(state2))
//        println("-----------------------------------------")
        var next = nextState(state)
        while (next != state) {
            println("-----------------------------------------")
            println(format(next))
            state = next
            next = nextState(next)
        }

        println(format(next))

        return next.flatMap { it.filter { it == State.OCCUPIED } }.count()

    }

    fun format(input: List<List<State>>): String {
        return input.map { it.joinToString("") }.joinToString("\n")
    }

}

fun main() {
//    println(
//        Day11b.solve("""
//        L.LL.LL.LL
//        LLLLLLL.LL
//        L.L.L..L..
//        LLLL.LL.LL
//        L.LL.LL.LL
//        L.LLLLL.LL
//        ..L.L.....
//        LLLLLLLLLL
//        L.LLLLLL.L
//        L.LLLLL.LL
//    """.trimIndent()
//        )
//    )

    println(Day11b.solve("""
        .......#.
        ...#.....
        .#.......
        .........
        ..#L....#
        ....#....
        .........
        #........
        ...#.....
    """.trimIndent()))

    println(Day11b.solve("day11.txt"))
}