package com.example.twsestockapp.data.local.json

import android.content.Context
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileStockJsonCache(
    context: Context,
) : StockJsonCache {

    private val cacheDirectory = File(
        context.filesDir,
        DIRECTORY_NAME,
    )

    override suspend fun save(
        date: LocalDate,
        type: StockJsonType,
        json: String,
    ) = withContext(Dispatchers.IO) {
        val dateDirectory = directoryFor(date)

        if (!dateDirectory.exists()) {
            check(dateDirectory.mkdirs() || dateDirectory.isDirectory) {
                "Unable to create stock JSON cache directory: ${dateDirectory.absolutePath}"
            }
        }

        fileFor(
            date = date,
            type = type,
        ).writeText(
            text = json,
            charset = Charsets.UTF_8,
        )
    }

    override suspend fun exists(
        date: LocalDate,
        type: StockJsonType,
    ): Boolean = withContext(Dispatchers.IO) {
        fileFor(
            date = date,
            type = type,
        ).isFile
    }

    override suspend fun read(
        date: LocalDate,
        type: StockJsonType,
    ): String? = withContext(Dispatchers.IO) {
        val file = fileFor(
            date = date,
            type = type,
        )

        if (!file.isFile) {
            return@withContext null
        }

        file.readText(Charsets.UTF_8)
    }

    override suspend fun readLatest(
        type: StockJsonType,
    ): String? = withContext(Dispatchers.IO) {
        val date = findLatestDate(type)
            ?: return@withContext null

        val file = fileFor(
            date = date,
            type = type,
        )

        file.readText(Charsets.UTF_8)
    }

    override suspend fun latestDate(
        type: StockJsonType,
    ): LocalDate? = withContext(Dispatchers.IO) {
        findLatestDate(type)
    }

    private fun directoryFor(
        date: LocalDate,
    ): File {
        return File(
            cacheDirectory,
            date.format(DATE_FORMATTER),
        )
    }

    private fun fileFor(
        date: LocalDate,
        type: StockJsonType,
    ): File {
        return File(
            directoryFor(date),
            type.fileName,
        )
    }

    private fun findLatestDate(
        type: StockJsonType,
    ): LocalDate? {
        if (!cacheDirectory.isDirectory) {
            return null
        }

        return cacheDirectory
            .listFiles()
            ?.asSequence()
            ?.filter { directory ->
                directory.isDirectory
            }
            ?.mapNotNull { directory ->
                val date = parseDate(directory.name)
                    ?: return@mapNotNull null

                val dataFile = File(
                    directory,
                    type.fileName,
                )

                if (!dataFile.isFile) {
                    return@mapNotNull null
                }

                date
            }
            ?.maxOrNull()
    }

    private fun parseDate(
        value: String,
    ): LocalDate? {
        return runCatching {
            LocalDate.parse(
                value,
                DATE_FORMATTER,
            )
        }.getOrNull()
    }

    private companion object {
        const val DIRECTORY_NAME = "stock-json"

        val DATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ISO_LOCAL_DATE
    }
}
