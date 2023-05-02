package io.tohuwabohu.csv

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main() {
    import()
}

fun import() {
    val csv = readCsv("in.csv")

    println(makeWeightSql(csv))
    println(makeKcalSql(csv))
}

fun readCsv(fileName: String): List<Line> {
    val reader = File(fileName).bufferedReader()

    // 2 header lines
    reader.readLine()
    reader.readLine()

    return reader.lineSequence()
        .filter { it.isNotBlank() }
        .map {
            val line = it.split(',')

            Line(line[0].trim(), line[1].trim(), line[2].trim(), line[3].trim(), line[4].trim(), line[5].trim(), line[11].trim())
        }.toList()
}

fun makeWeightSql(csv: List<Line>): String {
    val sql = "insert into weight_tracker_entry (added, id, user_id, amount) values "

    val parseFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d-MMM-yyyy")
    val writeFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    val values = csv.map { line ->
        val parsedDate: LocalDate = LocalDate.parse(line.date, parseFormat)

        "('${writeFormat.format(parsedDate)}', 1, 1, ${line.weight})"
    }.joinToString (separator = ",\n")

    return sql + values
}

fun makeKcalSql(csv: List<Line>): String {
    val sql = "insert into calorie_tracker_entry (added, id, user_id, amount, category) values "

    val parseFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d-MMM-yyyy")
    val writeFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    val values = csv.map { line ->
        val parsedDate: LocalDate = LocalDate.parse(line.date, parseFormat)

        var i = 1

        mapOf(
            "b" to line.breakfast,
            "l" to line.lunch,
            "d" to line.dinner,
            "u" to line.shakes,
            "s" to line.snacks
        ).map { entry ->
            if (entry.value.isNotEmpty()) {
                "('${writeFormat.format(parsedDate)}', ${i++}, 1, ${entry.value}, '${entry.key}')"
            } else null
        }.filterNotNull().joinToString(separator = ",")


    }.joinToString (separator = ",\n")

    return sql + values
}

data class Line(
    val date: String,
    val breakfast: String,
    val lunch: String,
    val dinner: String,
    val shakes: String,
    val snacks: String,
    val weight: String
)