package advent2020

object Day10 {

    fun prepare(list: Iterable<String>): List<Int> {
        val intList = list.map { Integer.parseInt(it) }.sorted()
        println(intList.size == list.toSet().size) // no repeats
        return intList
    }

    fun partB(input: List<Int>): Long {
        return solveB(0, 0, input)
    }

    fun isValid(prev: Int, input: List<Int>): Boolean {
        var last = prev;
        for(v in input) {
            if(v > last + 3) return false
            last = v
        }
        return true;
    }

    fun allCombinations(prev: Int, input: List<Int>, visited: MutableSet<List<Int>> = HashSet()): Collection<List<Int>> {
        if(input.isNotEmpty() && isValid(prev, input) && visited.add(input)) {
            for(v in input) {
                if(v != input.last()) {
                    val copy = input.filter { it != v }
                    allCombinations(prev, copy, visited)
                }
            }
        }
        return visited
    }

    fun allCombinations2(prev: Int, input: List<Int>, cache: MutableMap<Int, Long> = HashMap()): Long {
        if(input.isEmpty()) return 0
        else if(input.size == 1) return 1
        else if(input[0] - prev <= 3) {

            return 1
        } else return 0
    }

    fun solveB(prev: Int, from: Int, input: List<Int>, memory: MutableMap<Int, Long> = HashMap()): Long {
        if (from + 1 >= input.size) {
            return 1L
        }
        val cached = memory[from]
        if (cached != null) return cached
        val next = input[from + 1]
        if (next <= prev + 3) {
            val nextR = solveB(input[from], from + 1, input, memory)
            val nextNextR = solveB(prev, from + 1, input, memory)
            var repeated = 0L
            if(from + 2 < input.size && input[from+2] <= prev + 3) {
                repeated = solveB(prev, from+2, input, memory)
            }
            memory[from] = nextR + nextNextR - repeated
            return nextR + nextNextR - repeated;
        } else {
            val nextR = solveB(input[from], from + 1, input, memory)
            memory[from] = nextR
            return nextR;
        }
    }


}

fun main() {
    val list = Help.read("2020/day10.txt").toList()

    val sample = """
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
    """.trimIndent().split("\n")

//    println(Day10.partB(listOf(1, 2, 3, 4)))
        println(Day10.allCombinations(0, listOf(1, 2, 3, 4)))
//    println(Day10.partB(Day10.prepare(sample)))

}