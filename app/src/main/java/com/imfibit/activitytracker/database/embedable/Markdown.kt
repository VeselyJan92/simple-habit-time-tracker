package com.imfibit.activitytracker.database.embedable

@JvmInline
value class Markdown(val value: String) {
    
    fun isEmpty() = value.isEmpty()
    
    fun isNotEmpty() = value.isNotEmpty()
    
    /**
     * Strips basic markdown syntax to provide a clean text preview.
     */
    fun toPlainText(): String {
        return value
            .replace(Regex("[#*_~`\\[\\]]"), "") // Remove formatting chars
            .replace(Regex("\\(.*?\\)"), "") // Remove link targets
            .replace(Regex("\n+"), " ") // Replace newlines with spaces
            .trim()
    }

    /**
     * Returns a shortened plain text version of the markdown content, appended with "...".
     */
    fun takeShortened(length: Int = 100): String {
        val plain = toPlainText()
        return if (plain.length > length) "${plain.take(length)}..." else plain
    }
}
