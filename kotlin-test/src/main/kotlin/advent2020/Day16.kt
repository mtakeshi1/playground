package advent2020

import java.lang.RuntimeException

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
        }.map { println(it); it }.sum()
    }

    fun noneMatch(fields: List<Field>, values: List<Int>): Boolean = fields.find { field -> values.find { field.match(it) } != null } == null

    fun valid(fields: List<Field>, ticket: List<Int>): Boolean = ticket.all { v ->
//        if(v == 975) {
//            println(fields.filter { it.match(v) })
//        }
//        fields.any { field -> field.match(v) }
        fields.any { it.match(v) }
    }

    fun invalid(fields: List<Field>, ticket: List<Int>): Boolean = !valid(fields, ticket)

    override fun solveb(input: List<String>): Any {
        val fields = input.map { it.trim() }.takeWhile { it.isNotEmpty() }.map { parseField(it) }
        val myTicket = input.drop(fields.size+1).drop(1)[0].split(",").map { it.toInt() }
        val otherTickets = input.drop(fields.size).drop(5).map { it.split(",").map { p -> p.toInt() } }
        val validTickets = otherTickets.filter { ticket -> !invalid(fields, ticket) }
        val fieldCandidates = myTicket.map { HashSet<Field>(fields) }
        while (!fieldCandidates.all { it.size == 1 }) {
            fieldCandidates.withIndex().forEach { if(it.value.size == 0) throw RuntimeException("empty field: ${it.index}") }
            validTickets.forEach { ticket ->
                ticket.withIndex().forEach { entry ->
                    fieldCandidates[entry.index].removeIf { f -> !f.match(entry.value) }
                }
            }
            fieldCandidates.withIndex().filter { it.value.size == 1 }.forEach { s -> fieldCandidates.withIndex().filter { it.index != s.index }.forEach { it.value.removeAll(s.value) } }
            fieldCandidates.withIndex().forEach{ println("index ${it.index} size: ${it.value.size}") }
        }
        return fieldCandidates.asSequence().map { it.first() }.withIndex().filter { it.value.name.startsWith("departure") }.map { myTicket[it.index].toLong() }.fold(1L){ a, b -> a*b}
    }

}

fun main() {
//    println(Day16.solveb("""
//        class: 0-1 or 4-19
//        row: 0-5 or 8-19
//        seat: 0-13 or 16-19
//
//        your ticket:
//        11,12,13
//
//        nearby tickets:
//        3,9,18
//        15,1,5
//        5,14,9
//    """.trimIndent()))


    println(Day16.solveb("day16.txt"))
}