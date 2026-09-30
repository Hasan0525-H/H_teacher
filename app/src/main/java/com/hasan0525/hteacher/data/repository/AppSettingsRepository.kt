package com.hasan0525.hteacher.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.settingsDataStore by preferencesDataStore(
    name = "teacher_settings"
)

data class AppSettings(
    val teacherName: String = "",
    val schoolName: String = "",
    val specialization: String = "",
    val jobTitle: String = "",
    val useDarkTheme: Boolean = false
)

class AppSettingsRepository(
    private val context: Context
) {
    private object Keys {
        val TeacherName = stringPreferencesKey("teacher_name")
        val SchoolName = stringPreferencesKey("school_name")
        val Specialization = stringPreferencesKey("specialization")
        val JobTitle = stringPreferencesKey("job_title")
        val UseDarkTheme = booleanPreferencesKey("use_dark_theme")
        val InstallId = stringPreferencesKey("install_id")
    }

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            teacherName = preferences[Keys.TeacherName].orEmpty(),
            schoolName = preferences[Keys.SchoolName].orEmpty(),
            specialization = preferences[Keys.Specialization].orEmpty(),
            jobTitle = preferences[Keys.JobTitle].orEmpty(),
            useDarkTheme = preferences[Keys.UseDarkTheme] ?: false
        )
    }

    suspend fun updateTeacherProfile(
        teacherName: String,
        schoolName: String,
        specialization: String,
        jobTitle: String
    ) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.TeacherName] = teacherName.trim()
            preferences[Keys.SchoolName] = schoolName.trim()
            preferences[Keys.Specialization] = specialization.trim()
            preferences[Keys.JobTitle] = jobTitle.trim()
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.UseDarkTheme] = enabled
        }
    }

    suspend fun getOrCreateInstallId(): String {
        val existing = context.settingsDataStore.data
            .first()[Keys.InstallId]
            ?.trim()
            .orEmpty()

        if (existing.isNotEmpty()) return existing

        val created = UUID.randomUUID().toString()
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.InstallId] = created
        }
        return created
    }
}
