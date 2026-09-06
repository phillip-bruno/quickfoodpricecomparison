package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.data

import android.content.Context
import com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.model.FoodDensity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class FoodDensityRepository(private val context: Context) : FoodDensitySource {

    override fun loadFoodDensities(): List<FoodDensity> {
        return parseCsv(context.assets.open("food_density.csv"))
    }

    companion object {
        fun parseCsv(inputStream: InputStream): List<FoodDensity> {
            val results = mutableListOf<FoodDensity>()
            BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                val headerLine = reader.readLine()?.trimStart('\uFEFF') ?: return results
                val headers = parseCsvLine(headerLine)
                val headerMap = headers.withIndex().associate { (i, h) -> h.trim() to i }

                val foodNameIdx = headerMap["food_name"] ?: return results
                val gMlIdx = headerMap["g_ml"] ?: return results
                val sgIdx = headerMap["specific_gravity"]
                val biblioIdx = headerMap["biblio_id"]
                val categoryIdx = headerMap["category"]

                var line = reader.readLine()
                while (line != null) {
                    try {
                        val fields = parseCsvLine(line)
                        val gMl = fields[gMlIdx].toDoubleOrNull()
                        if (gMl != null) {
                            val sgRaw = sgIdx?.let { fields.getOrNull(it) } ?: ""
                            results.add(
                                FoodDensity(
                                    foodName = fields[foodNameIdx],
                                    gMl = gMl,
                                    specificGravity = sgRaw.toDoubleOrNull(),
                                    biblioId = biblioIdx?.let { fields.getOrNull(it) } ?: "",
                                    category = categoryIdx?.let { fields.getOrNull(it) } ?: ""
                                )
                            )
                        }
                    } catch (_: Exception) {
                        // Skip malformed rows
                    }
                    line = reader.readLine()
                }
            }
            return results
        }

        fun parseCsvLine(line: String): List<String> {
            val fields = mutableListOf<String>()
            val current = StringBuilder()
            var inQuotes = false
            var i = 0
            while (i < line.length) {
                val c = line[i]
                when {
                    c == '"' && !inQuotes -> inQuotes = true
                    c == '"' && inQuotes -> {
                        if (i + 1 < line.length && line[i + 1] == '"') {
                            current.append('"')
                            i++
                        } else {
                            inQuotes = false
                        }
                    }
                    c == ',' && !inQuotes -> {
                        fields.add(current.toString().trim())
                        current.clear()
                    }
                    else -> current.append(c)
                }
                i++
            }
            fields.add(current.toString().trim())
            return fields
        }

        fun getFoodCategories(items: List<FoodDensity>): List<String> {
            return items.mapNotNull { it.category.takeIf { c -> c.isNotEmpty() } }
                .distinct()
                .sorted()
        }
    }
}
