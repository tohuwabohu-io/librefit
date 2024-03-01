package io.tohuwabohu.csv

import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.CalorieTrackerEntry
import io.tohuwabohu.crud.WeightTrackerEntry
import io.tohuwabohu.crud.converter.CalorieTrackerCategoryConverter
import java.io.File
import java.lang.Float.parseFloat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

private const val defaultDatePattern = "d-MMM-yyyy"
private const val defaultHeaderLength = 2
private val converter = CalorieTrackerCategoryConverter()

fun readCsv(file: File, importConfig: ImportConfig): Uni<CsvData> {
    return Uni.createFrom().item(
        CsvData(
            importConfig,
            file.bufferedReader()
            .lineSequence().drop(importConfig.headerLength)
            .filter { it.isNotBlank() }
            .map {
                val line = it.split(',')

                Line(
                    line[0].trim(),
                    line[1].trim(),
                    line[2].trim(),
                    line[3].trim(),
                    line[4].trim(),
                    line[5].trim(),
                    line[11].trim()
                )
            }.toList()
        )
    )
}

fun collectWeightEntries(userId: UUID, csvData: CsvData): Uni<List<WeightTrackerEntry>> {
    val dateFormatter = DateTimeFormatter.ofPattern(csvData.config.datePattern)

    return Uni.createFrom().item(csvData.csv.map { line ->
        val weightTrackerEntry = WeightTrackerEntry(
            amount = parseFloat(line.weight)
        )

        weightTrackerEntry.userId = userId
        weightTrackerEntry.added = LocalDate.parse(line.date, dateFormatter)

        weightTrackerEntry
    })
}

fun collectCalorieTrackerEntries(userId: UUID, csvData: CsvData): Uni<List<CalorieTrackerEntry>> {
    val dateFormatter = DateTimeFormatter.ofPattern(csvData.config.datePattern)

    return Uni.createFrom().item(csvData.csv.map { line ->
        val parsedDate: LocalDate = LocalDate.parse(line.date, dateFormatter)

        mapOf(
            "b" to line.breakfast,
            "l" to line.lunch,
            "d" to line.dinner,
            "u" to line.shakes,
            "s" to line.snacks
        ).map { entry ->
            if (entry.value.isNotEmpty()) {
                val calorieTrackerEntry = CalorieTrackerEntry(
                    amount = parseFloat(entry.value),
                    category = converter.convertToEntityAttribute(entry.key)
                )

                calorieTrackerEntry.added = parsedDate;
                calorieTrackerEntry.userId = userId;
                calorieTrackerEntry
            } else null
        }.filterNotNull().first()
    })
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

data class ImportConfig (
    val datePattern: String = defaultDatePattern,
    val headerLength: Int = defaultHeaderLength
)

data class CsvData (
    val config: ImportConfig,
    val csv: List<Line>
)
