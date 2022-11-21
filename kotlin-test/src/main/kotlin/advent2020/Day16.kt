package advent2020

object Day16 : Solver {

    data class Field(val name: String, val ranges: List<Pair<Int, Int>>) {
        fun match(v: Int): Boolean = ranges.find { range -> v >= range.first && v <= range.second } != null
    }

    fun parseRange(i: String): Pair<Int, Int> {
        val parts = i.split("-")
        return Pair(parts[0].toInt(), parts[1].toInt())
    }

    fun parseField(input: String): Field {
        //departure location: 35-796 or 811-953
        val allParts = input.split(":")
        val name = allParts[0]
        val parts = allParts[1].trim().split(" ") //35-796 or 811-953
        return Field(name, listOf(parseRange(parts[0]), parseRange(parts[2])))
    }

    override fun solve(input: List<String>): Any {
        val fields = input.map { it.trim() }.takeWhile { it.isNotEmpty() }.map { parseField(it) }
        val myTicket = input.drop(fields.size+1).drop(1)[0].split(",").map { it.toInt() }
        val otherTickets = input.drop(fields.size).drop(5).map { it.split(",").map { p -> p.toInt() } }
        return otherTickets.flatMap { ticket ->
            ticket.filter { fieldValue -> fields.none { it.match(fieldValue) } }
        }.sum()
    }

}

fun main() {
    println(Day16.solve("""
        class: 1-3 or 5-7
        row: 6-11 or 33-44
        seat: 13-40 or 45-50

        your ticket:
        7,1,14

        nearby tickets:
        7,3,47
        40,4,50
        55,2,20
        38,6,12
    """.trimIndent()))


    println(Day16.solve("day16.txt"))
}