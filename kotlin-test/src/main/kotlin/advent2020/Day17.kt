package advent2020

import advent2020.Day17.neighbours

object Day17 : Solver {

    fun Triple<Int,Int,Int>.neighbours(): List<Triple<Int, Int, Int>> {
        return (-1 until 2).map { first + it }.flatMap { nx ->
            (-1 until 2).map { second + it }.flatMap { ny ->
                (-1 until 2).map { third + it }.map { nz -> Triple(nx, ny, nz) }
            }
        } .filter {it != this}
    }

    fun Triple<Int,Int,Int>.next(activeCubes: Set<Triple<Int, Int, Int>>): Boolean {
        val active = this.neighbours().count { activeCubes.contains(it) }
        return (activeCubes.contains(this) && (active == 2 || active == 3)) || (!activeCubes.contains(this) && active == 3)
    }

    override fun solve(input: List<String>): Any {
        var active = input.withIndex().flatMap {
            val y = it.index
            it.value.withIndex().filter { it.value == '#' }.map { x -> Triple(x.index, y, 0) }
        }.toSet()
        for(i in 0 until 6) {
            active = active.flatMap { it.neighbours() }.filter { it.next(active) }.toSet()
        }
        return active.size
    }

    data class Quad(val x: Int, val y: Int, val z: Int, val w: Int) {

        fun neighbours(): List<Quad> {
            return (-1 until 2).map { this.x + it }.flatMap { nx ->
                (-1 until 2).map { this.y + it }.flatMap { ny ->
                    (-1 until 2).map { this.z + it }.flatMap { nz ->
                        (-1 until 2).map { Quad(nx, ny, nz, this.w + it) }
                    }
                }
            } .filter {it != this}
        }

        fun next(activeCubes: Set<Quad>): Boolean {
            val active = this.neighbours().count { activeCubes.contains(it) }
            return (activeCubes.contains(this) && (active == 2 || active == 3)) || (!activeCubes.contains(this) && active == 3)
        }

    }

    override fun solveb(input: List<String>): Any {
        var active = input.withIndex().flatMap {
            val y = it.index
            it.value.withIndex().filter { it.value == '#' }.map { x -> Quad(x.index, y, 0, 0) }
        }.toSet()
        for(i in 0 until 6) {
            active = active.flatMap { it.neighbours() }.filter { it.next(active) }.toSet()
        }
        return active.size
    }

}

fun main() {
    println(Day17.solveb("""
        .#.
        ..#
        ###
    """.trimIndent()))

    println(Day17.solveb("""
        .##..#.#
        ##.#...#
        ##.#.##.
        ..#..###
        ####.#..
        ...##..#
        #.#####.
        #.#.##.#
    """.trimIndent()))

}