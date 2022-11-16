package advent2020

object Day10B : Solver {

    fun valid(prev: Int, next: Int): Boolean = next - prev <= 3

    data class CacheKey(val prev: Int, val index: Int)

    fun solve(prev: Int, input: List<Int>, index: Int = 0, cache: MutableMap<CacheKey, Long> = HashMap()): Long {
        if (input.size <= index || !valid(prev, input[index])) return 0
        else if (input.size == index+1) { return 1 } else {
            val key = CacheKey(prev, index)
            val present = cache.get(key)
            if(present != null) return present
            val r = solve(prev, input, index+1, cache) + solve(input[index], input, index+1, cache)
            cache.put(key, r)
            return r
        }
    }

    override fun solveI(input: List<Int>): Any {
        return solve(0, input.sorted())
    }

}

fun main() {
    println(Day10B.solve(0, listOf(1, 2, 4)))

    val smallSample = """
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
    """.trimIndent().split("\n").toList().map { Integer.parseInt(it) }.sorted()
    println(Day10B.solve(0, smallSample))

    val largerSample = """
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
    """.trimIndent().split("\n").toList().map { Integer.parseInt(it) }.sorted()

    println(Day10B.solve(0, largerSample))

    val solver = Day10B
    println(solver.solve("day10.txt"))


}
