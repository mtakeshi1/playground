package advent2020

object Day2A {

    val sampleInput = """
        1-3 a: abcde
        1-3 b: cdefg
        2-9 c: ccccccccc
    """.trimIndent().split("\n").map { it.trim() }


    fun range(s: String): Pair<Int, Int> {
        val ss = s.split("-")
        return Pair(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]))
    }

    fun check(line: String): Boolean {
        val parts = line.split(" ").map { it.trim() }
        val (min, max) = range(parts[0])
        val target = parts[1][0]
        val c = parts[2].count { it == target }
        return c in min..max;
    }

    fun checkb(line: String): Boolean {
        val parts = line.split(" ").map { it.trim() }
        val (min, max) = range(parts[0])
        val target = parts[1][0]

        return (parts[2][min-1] == target) xor (parts[2][max-1] == target)
    }

}

fun main() {
//    print(Day2A.sampleInput.count { Day2A.check(it) })
//    println(Help.read("kotlin-test/2020/day2a.txt").filter{ Day2A.check(it) }.count())
    assert(Day2A.checkb("1-3 a: abcde"))
    assert(!Day2A.checkb("1-3 b: cdefg"))
    assert(!Day2A.checkb("2-9 c: ccccccccc"))
    println(Help.read("kotlin-test/2020/day2a.txt").filter{ Day2A.checkb(it) }.count())
}
