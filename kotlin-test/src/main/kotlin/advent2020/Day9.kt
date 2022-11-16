package advent2020

object Day9 {


    fun solveA(input: List<Long>, preAmble: Int = 25): Long {
        for (i in preAmble until input.size) {
            if (!existsSum(input, i - preAmble, i, input[i])) {
                return input[i]
            }
        }
        throw Error("?")
    }


    fun solveB(input: List<Long>, preAmble: Int = 25): Long {
        val toSearch = solveA(input, preAmble)
        var i = 0
        var j = 1
        while (true) {
            val selectedSet = input.slice(i..j)
            val sumRange = selectedSet.sum()
            if(sumRange == toSearch) {
//                return selectedSet.min() + selectedSet.max()
                return 1
            } else if(sumRange < toSearch) {
                j++
            } else {
                i++
            }
        }
    }


    private fun existsSum(input: List<Long>, from: Int, to: Int, sum: Long): Boolean {
        for (x in from until to - 1) {
            for (y in (x + 1) until to) {
                if(input[x] + input[y] == sum) {
                    return true
                }
            }
        }
        return false
    }


}

fun main() {
    val sample = """
        35
        20
        15
        25
        47
        40
        62
        55
        65
        95
        102
        117
        150
        182
        127
        219
        299
        277
        309
        576
    """.trimIndent().split("\n").map { java.lang.Long.parseLong(it) }

    println(Day9.solveB(sample, 5))

    println(Day9.solveB(Help.read("2020/day9.txt").toList().map { java.lang.Long.parseLong(it) }))

}