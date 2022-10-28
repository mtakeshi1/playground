package advent2020

object Day7 {

    fun parseLine(line: String): Pair<String, List<String>> {
        val parts = line.split(" ")
        var i = 0
        var bagFrom = "";
        while (!parts[i].startsWith("bag")) {
            bagFrom += " " + parts[i]
            i++
        }
        i++
        assert(parts[i] == "contain")
        i++;
        if (parts[i] == "no") {
            return Pair(bagFrom.trim(), listOf())
        }
        i++
        var currentBag = ""
        val bagsTo = ArrayList<String>()
        while (i < parts.size) {
            if (parts[i].startsWith("bag")) {
                bagsTo.add(currentBag.trim())
                currentBag = ""
                i++ // skip ,
            } else {
                currentBag += " " + parts[i]
            }
            i++;
        }
        return Pair(bagFrom.trim(), bagsTo)
    }

    fun buildGraph(entries: List<Pair<String, List<String>>>): Map<String, MutableList<String>> {
        val map = HashMap<String, MutableList<String>>()
        entries.forEach { e ->
            e.second.forEach { map.computeIfAbsent(it) { ArrayList() }.add(e.first) }
        }
        return map
    }
    fun parseLineB(line: String): Pair<String, List<Pair<Int, String>>> {
        val parts = line.split(" ")
        var i = 0
        var bagFrom = "";
        while (!parts[i].startsWith("bag")) {
            bagFrom += " " + parts[i]
            i++
        }
        i++
        assert(parts[i] == "contain")
        i++;
        if (parts[i] == "no") {
            return Pair(bagFrom.trim(), listOf())
        }

        var currentBag = ""
        var currentAmount = Integer.parseInt(parts[i]);
        i++
        val bagsTo = ArrayList<Pair<Int, String>>()
        while (i < parts.size) {
            if (parts[i].startsWith("bag")) {
                bagsTo.add(Pair(currentAmount, currentBag.trim()))
                currentBag = ""
                i++ // skip ,
                if(i < parts.size) currentAmount = Integer.parseInt(parts[i]);
            } else {
                currentBag += " " + parts[i]
            }
            i++;
        }
        return Pair(bagFrom.trim(), bagsTo)
    }


    fun buildGraphB(entries: List<Pair<String, List<Pair<Int, String>>>>): Map<String, List<Pair<Int, String>>> {
        val map = HashMap<String, List<Pair<Int, String>>>()
        entries.forEach { map[it.first] = it.second }
        return map
    }

    fun visit(
        from: String,
        graph: Map<String, MutableList<String>>,
        visited: MutableSet<String> = HashSet()
    ): Set<String> {
        if (visited.contains(from)) return visited
        visited.add(from)
        graph.getOrDefault(from, ArrayList()).forEach { visit(it, graph, visited) }
        return visited
    }
    fun visitB(from: String, graph: Map<String, List<Pair<Int, String>>>, visited: MutableSet<String> = HashSet()): Int {
        if (visited.contains(from)) return 0
        visited.add(from)
        var c = 0
        graph.getOrDefault(from, listOf()).forEach { pair ->
            c += pair.first * (1 + visitB(pair.second, graph, visited))
        }
        return c
    }
}

fun main() {
    val sample = """
        light red bags contain 1 bright white bag, 2 muted yellow bags.
        dark orange bags contain 3 bright white bags, 4 muted yellow bags.
        bright white bags contain 1 shiny gold bag.
        muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
        shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
        dark olive bags contain 3 faded blue bags, 4 dotted black bags.
        vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
        faded blue bags contain no other bags.
        dotted black bags contain no other bags.
    """.trimIndent().split("\n")
//    var graph = Day7.buildGraph(Help.read("2020/day7.txt") .map { Day7.parseLine(it) }.toList())
//    println(graph.get("shiny gold"))
//    println(Day7.visit("shiny gold", graph).size - 1)
    val graphb = Day7.buildGraphB(Help.read("2020/day7.txt") .map { Day7.parseLineB(it) }.toList())
    println(Day7.visitB("shiny gold", graphb))

}