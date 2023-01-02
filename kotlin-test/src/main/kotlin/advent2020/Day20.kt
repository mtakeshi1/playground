package advent2020

object Day20 : Solver {



    data class Tile(val id: Long, val edges: List<String>) {
        fun rotations(): List<Tile>  {
            TODO()
        }
        fun matching(other: Tile): Int {
            return this.edges.count { other.edges.contains(it) } + this.edges.map { it.reversed() }.count{other.edges.contains(it)}
        }

        fun touches(other: Tile): Boolean = matching(other) > 0

        fun count(m:  Map<String, MutableSet<Long>>): Int {
            TODO()
        }
    }

    fun parseTiles(input: List<String>): List<Tile> {
        return if (input.isEmpty()) listOf()
        else {
            //Tile 2729:
            val id = input.first().split(" ")[1].replace(":", "").toLong()
            val tilePixels = input.drop(1).take(10)
            val north = tilePixels.first()
            val south = tilePixels.last()
            val east = (0 until 10).map { tilePixels[it][9] }.joinToString(separator = "")
            val west = (0 until 10).map { tilePixels[it][0] }.joinToString(separator = "")
            listOf(Tile(id, listOf(north, south, east, west))) + parseTiles(input.drop(12))
        }
    }

    fun index(tiles: List<Tile>): Map<String, MutableSet<Long>> {
        val map = HashMap<String, MutableSet<Long>>()
        tiles.forEach { tile ->
            tile.edges.forEach { edge ->
                map.computeIfAbsent(edge) { HashSet() } .add(tile.id)
                map.computeIfAbsent(edge.reversed()) { HashSet() } .add(tile.id)
            }
        }
        return map
    }

    override fun solve(input: List<String>): Any {
        val tiles = parseTiles(input)
        return tiles.map { t -> Pair(t.id, tiles.filter { t.touches(it) }.size) }
    }

    fun <A> allPermutations(input: List<A>, preffix: List<A> = listOf(), known: MutableSet<List<A>> = HashSet()): Set<List<A>> {
        if (input.isEmpty()) {
            known.add(preffix)
        }
        for (a in input) {
            val newPref = preffix + listOf(a)
            val newInput = input.toMutableList()
            newInput.remove(a)
            allPermutations(newInput, newPref, known)
        }
        return known
    }


}

fun main() {
    println(
        Day20.solve(
            """
        Tile 2311:
        ..##.#..#.
        ##..#.....
        #...##..#.
        ####.#...#
        ##.##.###.
        ##...#.###
        .#.#.#..##
        ..#....#..
        ###...#.#.
        ..###..###

        Tile 1951:
        #.##...##.
        #.####...#
        .....#..##
        #...######
        .##.#....#
        .###.#####
        ###.##.##.
        .###....#.
        ..#.#..#.#
        #...##.#..

        Tile 1171:
        ####...##.
        #..##.#..#
        ##.#..#.#.
        .###.####.
        ..###.####
        .##....##.
        .#...####.
        #.##.####.
        ####..#...
        .....##...

        Tile 1427:
        ###.##.#..
        .#..#.##..
        .#.##.#..#
        #.#.#.##.#
        ....#...##
        ...##..##.
        ...#.#####
        .#.####.#.
        ..#..###.#
        ..##.#..#.

        Tile 1489:
        ##.#.#....
        ..##...#..
        .##..##...
        ..#...#...
        #####...#.
        #..#.#.#.#
        ...#.#.#..
        ##.#...##.
        ..##.##.##
        ###.##.#..

        Tile 2473:
        #....####.
        #..#.##...
        #.##..#...
        ######.#.#
        .#...#.#.#
        .#########
        .###.#..#.
        ########.#
        ##...##.#.
        ..###.#.#.

        Tile 2971:
        ..#.#....#
        #...###...
        #.#.###...
        ##.##..#..
        .#####..##
        .#..####.#
        #..#.#..#.
        ..####.###
        ..#.#.###.
        ...#.#.#.#

        Tile 2729:
        ...#.#.#.#
        ####.#....
        ..#.#.....
        ....#..#.#
        .##..##.#.
        .#.####...
        ####.#.#..
        ##.####...
        ##..#.##..
        #.##...##.

        Tile 3079:
        #.#.#####.
        .#..######
        ..#.......
        ######....
        ####.#..#.
        .#...#.##.
        #.#####.##
        ..#.###...
        ..#.......
        ..#.###...
    """.trimIndent()
        )
    )
}
