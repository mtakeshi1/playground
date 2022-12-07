package advent2020

object D22 : Solver {

    fun round(a: List<Int>, b: List<Int>): Pair<List<Int>, List<Int>> {
        return if (a.first() > b.first()) {
            Pair(a.drop(1) + a.first() + b.first(), b.drop(1))
        } else {
            Pair(a.drop(1), b.drop(1) + b.first() + a.first())
        }
    }

    fun score(cards: List<Int>): Long {
        return if (cards.isEmpty()) 0
        else cards.size.toLong() * cards.first() + score(cards.drop(1))
    }

    override fun solve(input: List<String>): Any {
        var players = input.splitOnEmpty().let { Pair(it.first.drop(1).map { it.toInt() }, it.second.drop(1).map { it.toInt() }) }
        while (players.first.isNotEmpty() && players.second.isNotEmpty()) {
            players = round(players.first, players.second)
        }
        return score(players.first) + score(players.second)
    }

    fun recursiveGame(a: List<Int>, b: List<Int>, statesSeen: MutableSet<Pair<List<Int>, List<Int>>> = HashSet()): Pair<Long, Long> {
        var pa = a
        var pb = b
        while (pa.isNotEmpty() && pb.isNotEmpty()) {
            if(statesSeen.contains(Pair(pa, pb))) {
                return Pair(score(a), 0)
            }
            statesSeen.add(Pair(pa, pb))
            val fa = pa.first()
            val fb = pb.first()
            if(pa.size > fa && pb.size > fb) {
                val na = pa.drop(1).take(fa)
                val nb = pb.drop(1).take(fb)
                val nc = recursiveGame(na, nb)
                if(nc.first > nc.second) {
                    pa = pa.drop(1) + fa + fb
                    pb = pb.drop(1)
                } else {
                    pb = pb.drop(1) + fb + fa
                    pa = pa.drop(1)
                }
                // recursively determine subgame
            } else {
                val pp = round(pa, pb)
                pa = pp.first
                pb = pp.second
            }
        }
        return Pair(score(pa), score(pb))
    }

    override fun solveb(input: List<String>): Any {
        var players = input.splitOnEmpty().let { Pair(it.first.drop(1).map { it.toInt() }, it.second.drop(1).map { it.toInt() }) }
        val finalScores = recursiveGame(players.first, players.second)
        return finalScores.first + finalScores.second
    }

}

fun main() {
    println(
        D22.solveb(
            """
        Player 1:
        9
        2
        6
        3
        1

        Player 2:
        5
        8
        4
        7
        10
    """.trimIndent()
        )
    )

    println(D22.solveb("day22.txt"))

    println(D22.solveb("""
        Player 1:
        43
        19

        Player 2:
        2
        29
        14
    """.trimIndent()))

}