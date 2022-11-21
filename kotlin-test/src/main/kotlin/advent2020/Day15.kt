package advent2020

object Day15 : Solver {

    override fun solve(input: List<String>): Any {
        val map = HashMap<Long, Long>()
        val indexed = input[0].split(",").map { it.toLong() }
        indexed.take(indexed.size-1).withIndex().forEach {  map.put(it.value, (it.index+1).toLong()) }
        var turn = (indexed.size +1).toLong()
        var last = indexed[indexed.size-1]
        val limit = 30000000
        while (turn <= limit) {
            val lastTurn = map[last]
            map[last] = turn-1
            if(lastTurn == null) {
                last = 0
            } else {
                last = turn-1 - lastTurn
            }
            turn++;
        }
        return last
    }

}

fun main() {
    println(Day15.solve("0,3,6"))
    println(Day15.solve("1,2,3"))
    println(Day15.solve("2,1,3"))
    println(Day15.solve("14,1,17,0,3,20"))
}