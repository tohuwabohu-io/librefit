package io.tohuwabohu.crud

import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import io.tohuwabohu.crud.converter.CalorieTrackerCategoryConverter
import io.tohuwabohu.crud.error.ErrorDescription
import io.tohuwabohu.crud.error.ValidationError
import jakarta.enterprise.context.ApplicationScoped
import java.io.File
import java.lang.Float.parseFloat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

private const val defaultDatePattern = "d-MMM-yyyy"
private const val defaultHeaderLength = 2

private val converter = CalorieTrackerCategoryConverter()

@ApplicationScoped
class ImportHelper(val calorieTrackerRepository: CalorieTrackerRepository, val weightTrackerRepository: WeightTrackerRepository) {
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

    private fun collectWeightEntries(userId: UUID, csvData: CsvData): List<WeightTrackerEntry> {
        val dateFormatter = DateTimeFormatter.ofPattern(csvData.config.datePattern)

        return csvData.csv.map { line ->
            val weightTrackerEntry = WeightTrackerEntry(
                amount = parseFloat(line.weight)
            )

            weightTrackerEntry.userId = userId
            weightTrackerEntry.added = LocalDate.parse(line.date, dateFormatter)

            weightTrackerEntry
        }
    }

    private fun collectCalorieTrackerEntries(userId: UUID, csvData: CsvData): List<CalorieTrackerEntry> {
        val dateFormatter = DateTimeFormatter.ofPattern(csvData.config.datePattern)

        return csvData.csv.flatMap { line ->
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
            }.filterNotNull()
        }
    }

    @WithTransaction
    fun import(userId: UUID, csvData: CsvData): Uni<Void> {
        val calorieTrackerEntries = collectCalorieTrackerEntries(userId, csvData)
        val weightTrackerEntries = collectWeightEntries(userId, csvData)

        return if (csvData.config.updateCalorieTracker && csvData.config.updateWeightTracker) {
            calorieTrackerRepository.importBulk(calorieTrackerEntries, csvData.config)
                .chain { _ -> weightTrackerRepository.importBulk(weightTrackerEntries, csvData.config) }
        } else if (csvData.config.updateCalorieTracker) {
            calorieTrackerRepository.importBulk(calorieTrackerEntries, csvData.config)
        } else if (csvData.config.updateWeightTracker) {
            weightTrackerRepository.importBulk(weightTrackerEntries, csvData.config)
        } else {
            return Uni.createFrom().failure(ValidationError(listOf(
                ErrorDescription("importer", "At least one importer must be selected.")
            )))
        }
    }
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
    var datePattern: String = defaultDatePattern,
    var headerLength: Int = defaultHeaderLength,
    var drop: Boolean = false,
    var updateCalorieTracker: Boolean = true,
    var updateWeightTracker: Boolean = true
)

data class CsvData (
    val config: ImportConfig,
    val csv: List<Line>
)
