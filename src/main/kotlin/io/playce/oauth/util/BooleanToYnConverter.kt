package io.playce.oauth.util

import javax.persistence.AttributeConverter
import javax.persistence.Converter


@Converter
class BooleanToYNConverter : AttributeConverter<Boolean, String> {
    override fun convertToDatabaseColumn(attribute: Boolean?): String {
        return if (attribute != null && attribute) "Y" else "N"
    }
    override fun convertToEntityAttribute(yn: String): Boolean {
        return "Y".equals(yn, ignoreCase = true)
    }
}