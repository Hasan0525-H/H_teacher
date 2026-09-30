package com.hasan0525.hteacher.data

import android.content.Context
import com.hasan0525.hteacher.data.local.HTeacherDatabase
import com.hasan0525.hteacher.data.repository.AppSettingsRepository
import com.hasan0525.hteacher.data.repository.OfflineTeacherRepository

class AppContainer(context: Context) {
    private val database = HTeacherDatabase.getInstance(context)

    val teacherRepository = OfflineTeacherRepository(
        subjectDao = database.subjectDao(),
        gradeDao = database.gradeDao(),
        curriculumDao = database.curriculumDao(),
        questionDao = database.questionDao()
    )

    val settingsRepository = AppSettingsRepository(context)
}
