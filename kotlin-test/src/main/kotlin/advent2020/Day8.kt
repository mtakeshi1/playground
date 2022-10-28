package advent2020

object Day8 {

    fun runInstructions(ins: List<Pair<String, Int>>): Int {
        var accumulator = 0
        var i = 0
        val visited = Array(ins.size) { false }
        while (!visited[i]) {
            visited[i] = true
            when (ins[i].first) {
                "nop" -> i++
                "acc" -> {
                    accumulator += ins[i].second; i++
                }
                "jmp" -> i += ins[i].second
                else -> throw java.lang.RuntimeException("? " + ins[i].first)
            }
        }
        return accumulator
    }


    fun runInstructionsB(ins: List<Pair<String, Int>>): Int? {
        var accumulator = 0
        var i = 0
        val visited = Array(ins.size) { false }
        while (i < ins.size) {
            if (visited[i]) return null
            visited[i] = true
            when (ins[i].first) {
                "nop" -> i++
                "acc" -> {
                    accumulator += ins[i].second; i++
                }
                "jmp" -> i += ins[i].second
                else -> throw java.lang.RuntimeException("? " + ins[i].first)
            }
        }
        return accumulator
    }

    fun swap(p: Pair<String, Int>) = when (p.first) {
        "jmp" -> Pair("nop", p.second)
        "nop" -> Pair("jmp", p.second)
        else -> p
    }

    fun b(ins: List<Pair<String, Int>>): Int {
        val copy = ArrayList(ins)
        for (i in ins.indices) {
            val current = ins[i]
            if (current.first == "nop" || current.first == "jmp") {
                copy[i] = swap(current)
                val t = runInstructionsB(copy)
                if(t != null) return t
                copy[i] = current
            }
        }
        throw java.lang.RuntimeException("? ")
    }


    fun parse(line: String): Pair<String, Int> {
        val parts = line.split(" ")
        return Pair(parts[0], Integer.parseInt(parts[1]))
    }

}

fun main() {
    val sample = """
        nop +0
        acc +1
        jmp +4
        acc +3
        jmp -3
        acc -99
        acc +1
        jmp -4
        acc +6
    """.trimIndent().split("\n")
    println(Day8.runInstructions(sample.map { Day8.parse(it) }))


    println(Day8.runInstructions(Help.read("2020/day8.txt").map { Day8.parse(it) }.toList()))
    println(Day8.b(Help.read("2020/day8.txt").map { Day8.parse(it) }.toList()))
}