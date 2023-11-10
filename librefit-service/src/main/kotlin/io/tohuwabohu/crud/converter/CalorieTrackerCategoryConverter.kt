package io.tohuwabohu.crud.converter

import io.tohuwabohu.crud.Category
import jakarta.persistence.AttributeConverter

class CalorieTrackerCategoryConverter : AttributeConverter<Category, String> {
    override fun convertToDatabaseColumn(attribute: Category?): String {
        return when (attribute) {
            Category.BREAKFAST -> "b"
            Category.LUNCH -> "l"
            Category.DINNER -> "d"
            Category.SNACK -> "s"
            else -> ""
        }
    }

    override fun convertToEntityAttribute(dbData: String?): Category {
        return when (dbData) {
            "b" -> Category.BREAKFAST
            "l" -> Category.LUNCH
            "d" -> Category.DINNER
            "s" -> Category.SNACK
            else -> Category.UNSET
        }
    }
}