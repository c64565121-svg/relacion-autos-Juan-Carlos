package com.juancarlos.relacionautos

object OcrParser {
    private val vinRegex = Regex("""\b[A-HJ-NPR-Z0-9]{17}\b""", RegexOption.IGNORE_CASE)
    private val yearRegex = Regex("""\b(19[8-9]\d|20[0-3]\d)\b""")

    data class Result(
        val chassis: String,
        val model: String,
        val year: String
    )

    fun parse(text: String): Result {
        val normalized = text.uppercase()
        val chassis = vinRegex.find(normalized)?.value.orEmpty()
        val year = yearRegex.find(normalized)?.value.orEmpty()

        val lines = normalized.lines().map { it.trim() }.filter { it.isNotBlank() }
        val modelLine = lines.firstOrNull {
            it.contains("MODEL") || it.contains("MODELO") || it.contains("TYPE")
        }.orEmpty()

        val model = modelLine
            .replace("MODEL", "", ignoreCase = true)
            .replace("MODELO", "", ignoreCase = true)
            .replace("TYPE", "", ignoreCase = true)
            .replace(":", " ")
            .trim()

        return Result(chassis, model, year)
    }
}
