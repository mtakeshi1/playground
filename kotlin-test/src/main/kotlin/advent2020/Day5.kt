package advent2020

object Day5 {
    fun lower(p: Pair<Int, Int>) = Pair(p.first, (p.first + p.second) / 2)

    fun higher(p: Pair<Int, Int>) = Pair((p.first + p.second) / 2 + 1, p.second)

    fun findRow(entry: String): Pair<Int, Int> {
        var hRange = Pair(0, 127)
        for (i in 0 until Math.min(7, entry.length)) {
            if (entry[i] == 'F') hRange = lower(hRange)
            else hRange = higher(hRange)
        }
        return hRange
    }

    fun findCol(entry: String): Pair<Int, Int> {
        var vRange = Pair(0, 7)
        for (i in (entry.length - 3) until entry.length) {
            if (entry[i] == 'L') vRange = lower(vRange)
            else vRange = higher(vRange)
        }
        return vRange
    }

    fun findSeat(entry: String): Int {
        val (row, _) = findRow(entry)
        val (col, _) = findCol(entry)
        return row * 8 + col
    }

}

fun main() {
    println(Day5.findRow("FBFBBFFRLR"))
    println(Day5.findSeat("FBFBBFFRLR"))

    println(Help.read("2020/day5.txt").mapToInt { Day5.findSeat(it) }.max())

    val maxId = 127 * 8
    val minID = 48

    val found = Help.read("2020/day5.txt").mapToInt { Day5.findSeat(it) }.toArray().toSortedSet()
//    found.forEach { println(it) }
    println((minID until maxId).find { !found.contains(it) })

}