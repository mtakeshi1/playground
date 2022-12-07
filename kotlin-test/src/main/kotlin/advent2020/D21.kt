package advent2020

object D21 : Solver {

    fun parse(line: String): Pair<Set<String>, Set<String>> {
        //mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
        return line.split("(").let {
            Pair(it[0].split(" ").filter { it.isNotEmpty() }.toSet(), it[1].replace(Regex("[,)]"), "").split(" ").drop(1).toSet())
        }
    }

    override fun solve(input: List<String>): Any {
        val ingAller = input.map { parse(it) }
        val allAlergens = ingAller.flatMap { it.second }.toSet()
        var allIng = ingAller.flatMap { it.first }.toSet()
        var r = allAlergens.map { allergen ->
            val ingContainingAllergen = ingAller.filter { it.second.contains(allergen) }.map { it.first }.toSet()
            val ings = ingContainingAllergen.reduce { a, b -> a.intersect(b) }
            Pair(allergen, ings)
        }
        while (r.any { it.second.size == 1 }) {
            val toRemove = r.find { it.second.size == 1 }!!
            val ingToRemove = toRemove.second.first()
            r = r.filterNot { it == toRemove }.map { pair -> Pair(pair.first, (pair.second - ingToRemove)) }
            allIng = allIng - ingToRemove
        }
        println(allIng)
        return ingAller.flatMap { it.first.intersect(allIng) }.size
    }


    override fun solveb(input: List<String>): Any {
        val ingAller = input.map { parse(it) }
        val allAlergens = ingAller.flatMap { it.second }.toSet()
        var allIng = ingAller.flatMap { it.first }.toSet()
        var r = allAlergens.map { allergen ->
            val ingContainingAllergen = ingAller.filter { it.second.contains(allergen) }.map { it.first }.toSet()
            val ings = ingContainingAllergen.reduce { a, b -> a.intersect(b) }
            Pair(allergen, ings)
        }
        val ingAllerMap: MutableMap<String, String> = HashMap()
        while (r.any { it.second.size == 1 }) {
            val toRemove = r.find { it.second.size == 1 }!!
            val ingToRemove = toRemove.second.first()
            ingAllerMap[toRemove.first] = ingToRemove
            r = r.filterNot { it == toRemove }.map { pair -> Pair(pair.first, (pair.second - ingToRemove)) }
            allIng = allIng - ingToRemove
        }

        return ingAllerMap.entries.map { e -> Pair(e.key, e.value) }.toList().sortedBy { it.first }.map{it.second}.joinToString(separator = ",")
    }
}

fun main() {
    println(
        D21.solveb(
            """
        mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
        trh fvjkl sbzzf mxmxvkd (contains dairy)
        sqjhc fvjkl (contains soy)
        sqjhc mxmxvkd sbzzf (contains fish)
    """.trimIndent()
        )
    )

    println(D21.solveb("day21.txt"))

}