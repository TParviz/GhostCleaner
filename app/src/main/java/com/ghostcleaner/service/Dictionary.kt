package com.ghostcleaner.service

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.MutableLiveData
import de.siegmar.fastcsv.reader.CsvReader
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.StringReader
import java.net.URL
import java.util.*
import kotlin.math.max

@SuppressLint("DefaultLocale")
object D : CoroutineScope {

    @Suppress("SpellCheckingInspection")
    private const val URL =
            "https://docs.google.com/spreadsheets/d/1jW8g4BFuy-v1HPfjGARr4UH2WQk4GYF0pcx4rAOig38/gviz/tq?tqx=out:csv"

    private val job = SupervisorJob()

    private val reader = CsvReader().apply {
        setContainsHeader(true)
    }

    private val map = Collections.synchronizedMap(hashMapOf<String, String>())

    val loading = MutableLiveData<Boolean>()

    fun initialize(context: Context) {
        val backup = File(context.filesDir, "net.csv")
        val lang = ConfigurationCompat.getLocales(context.resources.configuration)[0]
                ?.language?.toLowerCase() ?: "en"
        val defText = if (backup.exists()) {
            backup.readText()
        } else {
            context.assets.open("def.csv").use {
                it.bufferedReader().use(BufferedReader::readText)
            }
        }
        fillMap(lang, defText)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun download(context: Context) {
        job.cancelChildren()
        loading.value = true
        val backup = File(context.filesDir, "net.csv")
        val lang = ConfigurationCompat.getLocales(context.resources.configuration)[0]
                ?.language?.toLowerCase() ?: "en"
        launch {
            try {
                val text = URL(URL).openStream().use {
                    it.bufferedReader().use(BufferedReader::readText)
                }
                fillMap(lang, text)
                loading.postValue(false)
                backup.parentFile?.mkdirs()
                backup.writeText(text)
            } catch (e: Throwable) {
                Timber.e(e)
                loading.postValue(false)
            }
        }
    }

    private fun fillMap(lang: String, text: String) {
        val csv = reader.read(StringReader(text))
        val col = max(1, csv.header?.indexOfFirst { it.toLowerCase() == lang } ?: 1)
        csv.rows.forEach {
            if (it.fieldCount > col) {
                map[it.getField(0)] = it.getField(col)
            }
        }
    }

    operator fun get(key: String, vararg args: Any?): String {
        map[key]?.let {
            var value = it
            args.forEach { arg ->
                value = value.replaceFirst("<arg>", arg.toString())
            }
            return value.replace("<br>", "\n")
        }
        return ""
    }

    override val coroutineContext = Dispatchers.IO + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        loading.postValue(false)
    }
}