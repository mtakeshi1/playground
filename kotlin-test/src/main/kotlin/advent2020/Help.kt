package advent2020

import java.io.BufferedReader
import java.io.FileReader
import java.util.stream.IntStream
import java.util.stream.Stream
import java.io.File

object Help {

    fun read(file: String): Stream<String> {
        if(File(file).exists()) {
            val reader = BufferedReader(FileReader(file))
            return reader.lines().map { it.trim() }
        }
        return read("kotlin-test/$file")
    }

    fun readInt(file: String): IntStream = read(file).mapToInt{Integer.parseInt(it)}


}