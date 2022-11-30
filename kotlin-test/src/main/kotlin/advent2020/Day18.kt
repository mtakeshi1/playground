package advent2020

object Day18 : Solver {

    data class Symbol(val s: String)

    fun parseSymbols(input: String): List<Symbol> {
        val re = Regex("[0-9]+|[ ()+*]")
        return re.findAll(input).map { it.value.trim() }.filter { it.isNotEmpty() }.map { Symbol(it) }.toList()
    }

    interface Exp {
        fun eval(): Long
    }

    data class Lit(val v: Long) : Exp {
        override fun eval(): Long = v
    }

    data class Add(val left: Exp, val right: Exp) : Exp {
        override fun eval(): Long = left.eval() + right.eval()
    }

    data class Mul(val left: Exp, val right: Exp) : Exp {
        override fun eval(): Long = left.eval() * right.eval()
    }

    fun parseExp(symbols: List<Symbol>, from: Int = 0, leftExp: Exp? = null): Pair<Exp, Int> {
        if (from >= symbols.size) return Pair(Lit(0), symbols.size)
        when (symbols[from]) {
            Symbol("(") -> {
                var r = parseExp(symbols, from + 1, leftExp)
                while (symbols[r.second] != Symbol(")")) {
                    r = parseExp(symbols, r.second, r.first)
                }
                if (symbols[r.second] != Symbol(")")) throw RuntimeException("expected ')' but was: " + symbols[r.second])
                return Pair(r.first, r.second + 1)
            }
            Symbol("*") -> {
                val r = parseExp(symbols, from + 1)
                return Pair(Mul(leftExp!!, r.first), r.second)
            }
            Symbol("+") -> {
                val r = parseExp(symbols, from + 1)
                return Pair(Add(leftExp!!, r.first), r.second)
            }
            else -> {
                return Pair(Lit(symbols[from].s.toLong()), from + 1)
            }
        }
    }

    fun parseExaustive(line: String): Exp {
        val symbols = parseSymbols(line)
        var r = parseExp(symbols)
        while (r.second < symbols.size) {
            r = parseExp(symbols, r.second, r.first)
        }
        return r.first
    }

    override fun solve(input: List<String>): Any {
        return input.map { parseExaustive(it) }.sumOf { it.eval() }
    }

    fun <A> replaceSlice(input: List<A>, from: Int, to: Int, element: A): List<A> {
        return input.take(from) + listOf(element) + input.drop(to+1)
    }

    fun findMatchingPair(input: List<Symbol>): Pair<Int, Int>? {
        var from: Int? = null;
        var to: Int? = null;
        for(i in input.indices) {
            if(input[i].s == "(") from = i
            else if(input[i].s == ")") return Pair(from!!, i)
        }
        return null;
    }

    fun parseEvalB(input: List<Symbol>): Long {
        var toReduce = input;
        while (toReduce.size > 1) {
            val indices = findMatchingPair(toReduce)
            if (indices != null) {
                val inner = parseEvalB(toReduce.subList(indices.first+1, indices.second))
                toReduce = replaceSlice(toReduce, indices.first, indices.second, Symbol(inner.toString()))
            } else if (toReduce.indexOf(Symbol("+")) != -1) {
                val from = toReduce.indexOf(Symbol("+"))
                val r = toReduce[from-1].s.toLong() + toReduce[from+1].s.toLong()
                toReduce = replaceSlice(toReduce, from-1, from+1, Symbol(r.toString()))
            } else if (toReduce.indexOf(Symbol("*")) != -1) {
                val from = toReduce.indexOf(Symbol("*"))
                val r = toReduce[from-1].s.toLong() * toReduce[from+1].s.toLong()
                toReduce = replaceSlice(toReduce, from-1, from+1, Symbol(r.toString()))
            }
        }
        return toReduce[0].s.toLong()
    }

    fun parseExaustiveB(line: String): Long {
        val symbols = parseSymbols(line)
        return parseEvalB(symbols)
    }

    override fun solveb(input: List<String>): Any {
        return input.sumOf { parseExaustiveB(it) }
    }


}


fun main() {
//    println(Day18.solve("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2"))
//    println(Day18.solve("day18.txt"))
//    println(Day18.solveb("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))"))
    println(Day18.solveb("day18.txt"))
//    println(Day18.solveb("6 + (3 + 6 + 5 * 4) * (4 + 3) * 8 + 6 + 6"))
}