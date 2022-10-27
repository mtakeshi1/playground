package advent2020

object Day3 {

    val open = '.'
    val tree = '#'

    fun count(input: List<String>): Int = count(input, Pair(3, 1))

    fun count(input: List<String>, slope: Pair<Int, Int>): Int {
        var pos = Pair(0, 0)
        val xmax = input[0].length
        fun move(p: Pair<Int, Int>) = Pair((p.first + slope.first) % xmax, p.second + slope.second)
        var c = 0;
        while (pos.second < input.size) {
            if (input[pos.second][pos.first] == tree) c++
            pos = move(pos)
        }
        return c
    }


}

fun main() {
    val input = """
            ..##.......
            #...#...#..
            .#....#..#.
            ..#.#...#.#
            .#...##..#.
            ..#.##.....
            .#.#.#....#
            .#........#
            #.##...#...
            #...##....#
            .#..#...#.#
        """.trimIndent().split("\n").toList()



    println(Day3.count(input))
//Right 1, down 1.
//Right 3, down 1. (This is the slope you already checked.)
//Right 5, down 1.
//Right 7, down 1.
//Right 1, down 2.
    println(Day3.count(Help.read("2020/day3.txt").toList()))
//    println(Day3.count(Help.read("2020/day3.txt").toList()))
//    println(Day3.count(Help.read("2020/day3.txt").toList()))
    val list = Help.read("2020/day3.txt").toList()
    println(listOf(Pair(1, 1), Pair(3, 1), Pair(5, 1), Pair(7, 1), Pair(1, 2)).map { p ->
        Day3.count(list, p)
    }.reduce {a, b -> a * b})


}