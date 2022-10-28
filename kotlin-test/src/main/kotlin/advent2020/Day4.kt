package advent2020

import java.util.stream.Collectors

object Day4 {

    val sample:List<List<Pair<String, String>>> = """
        ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
        byr:1937 iyr:2017 cid:147 hgt:183cm

        iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
        hcl:#cfa07d byr:1929

        hcl:#ae17e1 iyr:2013
        eyr:2024
        ecl:brn pid:760753108 byr:1931
        hgt:179cm

        hcl:#cfa07d eyr:2025 pid:166559648
        iyr:2011 ecl:brn hgt:59in
    """.trimIndent().split("\n\n").map { it.split("\\s")  }.map { toFields(it)}.toList()

    fun toFields(parts: List<String>): List<Pair<String, String>> {
        return parts.map {
            val a = it.split(":")
            Pair(a[0], a[1])
        }
    }
    //byr (Birth Year)
    //iyr (Issue Year)
    //eyr (Expiration Year)
    //hgt (Height)
    //hcl (Hair Color)
    //ecl (Eye Color)
    //pid (Passport ID)
    //cid (Country ID)

    val required = setOf("byr", "iyr", "hgt", "eyr", "hcl", "ecl", "pid")

    fun valid(fields: List<Pair<String, String>>): Boolean {
        val set = fields.map { it.first }.toSet()
        return set.containsAll(required)
//        return required.stream().allMatch { fieldName ->
//            fields.firstOrNull { it.first.equals(fieldName) } != null
//        }
    }

    //byr (Birth Year) - four digits; at least 1920 and at most 2002.
    //iyr (Issue Year) - four digits; at least 2010 and at most 2020.
    //eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
    //hgt (Height) - a number followed by either cm or in:
    //If cm, the number must be at least 150 and at most 193.
    //If in, the number must be at least 59 and at most 76.
    //hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
    //ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
    //pid (Passport ID) - a nine-digit number, including leading zeroes.
    //cid (Country ID) - ignored, missing or not.
    fun validField(pair: Pair<String, String>): Boolean {
        try {
            return when(pair.first) {
                "byr" -> pair.second.matches("[0-9]{4}".toRegex()) && Integer.parseInt(pair.second) in 1920..2002
                "iyr" -> pair.second.matches("[0-9]{4}".toRegex()) && Integer.parseInt(pair.second) in 2010..2020
                "eyr" -> pair.second.matches("[0-9]{4}".toRegex()) && Integer.parseInt(pair.second) in 2020..2030
                "hgt" -> {
                    val m = "([0-9]+)(in|cm)".toRegex().matchEntire(pair.second)
                    if(m != null) {
                        if(m.groupValues[2].equals("cm")) Integer.parseInt(m.groupValues[1]) in 150..193
                        else if(m.groupValues[2].equals("in")) Integer.parseInt(m.groupValues[1]) in 59..76
                        else false
                    } else false;
                }
                "hcl" -> pair.second.matches("#[0-9a-f]{6}".toRegex())
                "ecl" -> pair.second in "amb blu brn gry grn hzl oth".split(" ").toSet()
                "pid" -> pair.second.matches("[0-9]{9}".toRegex())
                else -> true
            }
        } catch (e: Exception) {
            return false;
        }
    }

    fun fieldsValid(entry: List<Pair<String, String>>): Boolean = entry.all { validField(it) }

    fun allValid(entry: List<Pair<String, String>>): Boolean = valid(entry) && fieldsValid(entry)

}
fun main() {
    val entry = """
        pid:087499704 hgt:74in ecl:grn iyr:2012 eyr:2030 byr:1980
        hcl:#623a2f

        eyr:2029 ecl:blu cid:129 byr:1989
        iyr:2014 pid:896056539 hcl:#a97842 hgt:165cm

        hcl:#888785
        hgt:164cm byr:2001 iyr:2015 cid:88
        pid:545766238 ecl:hzl
        eyr:2022

        iyr:2010 hgt:158cm hcl:#b6652a ecl:blu byr:1944 eyr:2021 pid:093154719
    """.trimIndent()
    fun parse(input: String): List<List<Pair<String, String>>> = input.split("\n\n").map { it.replace('\n', ' ').split(" ")}.map { Day4.toFields(it) }

    parse(entry).stream().forEach { entry ->
        entry.forEach{ pair ->
            if(!Day4.validField(pair)) {
                println("wtf? $pair")
            }
        }
    }


    println(parse(entry).count { Day4.valid(it)})
    assert(!Day4.valid(listOf(Pair("byr", "a"), Pair("ecl", "a"), Pair("hcl", "a"), Pair("hgt", "a"), Pair("iyr", "a"), Pair("pid", "a"))))
    val input = Help.read("2020/day4.txt").collect(Collectors.joining("\n"))
    val parsed = parse(input)
    println(parsed.count { Day4.valid(it)})
    println(parsed.count { Day4.valid(it) && Day4.fieldsValid(it)})


}