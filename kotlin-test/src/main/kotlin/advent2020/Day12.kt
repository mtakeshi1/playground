package advent2020

object Day12 : Solver {

    val north = Pair(1, 0)
    val east = Pair(0, 1)
    val south = Pair(-1, 0)
    val west = Pair(0, -1)

    val directions = listOf(north, east, south, west)

    data class State(val x: Int, val y: Int, val direction: Pair<Int, Int> = east) {
        fun right(degrees: Int):State {
            val curr = directions.indexOf(direction)
            if(curr < 0) throw java.lang.RuntimeException("?")
            var next = curr + degrees/90
            while (next >= directions.size) next -= directions.size
            while (next < 0) next += directions.size
            return State(x, y, directions[next])
        }
    }

    data class Command(val ins: Char, val amount: Int) {
        fun apply(state: State): State {
            return when(ins) {
                'N' -> State(state.x + amount, state.y, state.direction)
                'S' -> State(state.x - amount, state.y, state.direction)
                'E' -> State(state.x, state.y + amount, state.direction)
                'W' -> State(state.x, state.y - amount, state.direction)
                'F' -> State(state.x + amount*state.direction.first, state.y + amount*state.direction.second, state.direction)
                'R' -> state.right(amount)
                'L' -> state.right(-amount)
                else -> TODO()
            }
        }
    }

    fun parse(line: String): Command = Command(line[0], line.substring(1).toInt())

    override fun solve(input: List<String>): Any {
        val end = input.map { parse(it) }.fold(State(0, 0)) { state, ins -> ins.apply(state) }
        return Math.abs(end.x) + Math.abs(end.y)
    }

}

fun main() {
    println(Day12.solve("""
        F10
        N3
        F7
        R90
        F11
    """.trimIndent()))

    println(Day12.solve("day12.txt"))

}
