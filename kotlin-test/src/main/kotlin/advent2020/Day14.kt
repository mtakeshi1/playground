package advent2020

import advent2020.Day14.Mask
import advent2020.Day14.Maskb

object Day14 : Solver {

    fun interface Mask {
        fun mask(input: Long): Long
    }

    fun applyMask(pair: Pair<Int, Boolean>): Mask {
        val mask = 1L shl pair.first
        if (pair.second) {
            return Mask { it or mask }
        } else {
            return Mask { it and mask.inv() }
        }
    }

    fun parseMask(s: String): Mask {
        val arr = s.split("=")[1].trim().toCharArray()
        if (arr.size != 36) throw RuntimeException("expected array size 36 but was: " + arr.size)
        val masks = arr.withIndex().filter { it.value != 'X' }.map { Pair(35 - it.index, it.value == '1') }.map { applyMask(it) }
        return Mask { masks.fold(it) { a, b -> b.mask(a) } }
    }

    fun parseNext(mask: Mask = Mask { it }, input: String, memory: MutableMap<Int, Long> = HashMap()): Pair<Mask, MutableMap<Int, Long>> {
        return if (input.startsWith("mask")) {
            Pair(parseMask(input), memory)
        } else {
            val r = Regex("mem\\[([0-9]+)] = ([0-9]+)").matchEntire(input)
            if (r != null) {
                val memIndex = r.groups[1]!!.value.toInt()
                val memValue = mask.mask(r.groups[2]!!.value.toLong())
                println("mem[$memIndex] = $memValue")
                memory[memIndex] = memValue
            }
            Pair(mask, memory)
        }
    }

    override fun solve(input: List<String>): Any {
        val memory =
            input.fold(Pair<Mask, MutableMap<Int, Long>>(Mask { it }, HashMap())) { pair, line -> parseNext(pair.first, line, pair.second) }.second
        return memory.values.sum()
    }

    /***************************************************************** B ********************************************************************/

    fun interface Maskb {
        fun apply(input: Long): List<Long>
    }

    fun applyMaskb(shift: Int, char: Char): Maskb {
        val mask = 1L shl shift
        return when (char) {
            '1' -> Maskb { listOf(it or mask) }
            '0' -> Maskb { listOf(it and mask.inv()) }
            else -> Maskb {
                listOf(it or mask, it and mask.inv())
            }
        }
    }

    fun parseMaskb(s: String): Maskb {
        val arr = s.split("=")[1].trim().toCharArray()
        if (arr.size != 36) throw RuntimeException("expected array size 36 but was: " + arr.size)
        val masks = arr.withIndex().filter { it.value != '0' }.map { applyMaskb(35 - it.index, it.value) }
        return Maskb { masks.fold(listOf(it)) { input, mask -> input.flatMap { m -> mask.apply(m) } } }
    }

    fun parseNextb(mask: Maskb, input: String, memory: MutableMap<Long, Long> = HashMap()): Pair<Maskb, MutableMap<Long, Long>> {
        return if (input.startsWith("mask")) {
            Pair(parseMaskb(input), memory)
        } else {
            val r = Regex("mem\\[([0-9]+)] = ([0-9]+)").matchEntire(input)
            if (r != null) {
                val memIndex = r.groups[1]!!.value.toLong()
                val memValue = r.groups[2]!!.value.toLong()
                val applied = mask.apply(memIndex)
                applied.forEach {
                    val bin = java.lang.Long.toBinaryString(it)
//                    println("mem[$bin] = $memValue")
                    memory[it] = memValue
                }
            }
            Pair(mask, memory)
        }
    }

    override fun solveb(input: List<String>): Any {
        val memory = input.fold(Pair<Maskb, MutableMap<Long, Long>>(Maskb { listOf(it) }, HashMap())) { pair, line ->
            parseNextb(
                pair.first,
                line,
                pair.second
            )
        }.second
        return memory.values.sum()
    }
}


fun main() {
//    println(
//        Day14.solve(
//            """
//        mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
//        mem[8] = 11
//        mem[7] = 101
//        mem[8] = 0
//    """.trimIndent()
//        )
//    )
//

    println(Day14.solveb("""
        mask = 000000000000000000000000000000X1001X
        mem[42] = 100
        mask = 00000000000000000000000000000000X0XX
        mem[26] = 1
    """.trimIndent()))

    println(Day14.solveb("day14.txt"))
}