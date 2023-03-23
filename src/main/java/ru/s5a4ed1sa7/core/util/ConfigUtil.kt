package ru.s5a4ed1sa7.core.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.apache.commons.io.FileUtils
import ru.s5a4ed1sa7.core.annotation.Config
import java.io.File
import java.nio.charset.StandardCharsets

object ConfigUtil {
    private val GSON: Gson = GsonBuilder().setPrettyPrinting().create()

    fun <T> loadConfig(configClass: Class<T>): T? {
        if (configClass.isAnnotationPresent(Config::class.java)) {
            val config = configClass.getAnnotation(
                Config::class.java
            )
            val configFileName = config.name
            val configFile = File(".", configFileName)
            return if (configFile.exists()) {
                val fileData = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8)
                GSON.fromJson(fileData, configClass)
            } else {
                FileUtils.writeStringToFile(configFile, "{}", StandardCharsets.UTF_8)
                val fileData = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8)
                GSON.fromJson(fileData, configClass)
            }
        }
        return null
    }

    fun saveConfig(configObject: Any) {
        val clazz: Class<*> = configObject.javaClass
        if (clazz.isAnnotationPresent(Config::class.java)) {
            val config = clazz.getAnnotation(
                Config::class.java
            )
            val configFileName = config.name
            val configFile = File(".", configFileName)
            FileUtils.writeStringToFile(configFile, GSON.toJson(configObject), StandardCharsets.UTF_8)
        }
    }
}