package com.hasan0525.hteacher.data.repository

import com.hasan0525.hteacher.data.local.dao.CurriculumDao
import com.hasan0525.hteacher.data.local.dao.GradeDao
import com.hasan0525.hteacher.data.local.dao.QuestionDao
import com.hasan0525.hteacher.data.local.dao.SubjectDao
import com.hasan0525.hteacher.data.local.entity.CurriculumEntity
import com.hasan0525.hteacher.data.local.entity.GradeEntity
import com.hasan0525.hteacher.data.local.entity.QuestionEntity
import com.hasan0525.hteacher.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

class OfflineTeacherRepository(
    private val subjectDao: SubjectDao,
    private val gradeDao: GradeDao,
    private val curriculumDao: CurriculumDao,
    private val questionDao: QuestionDao
) {
    val subjects: Flow<List<SubjectEntity>> = subjectDao.observeAll()
    val grades: Flow<List<GradeEntity>> = gradeDao.observeAll()
    val curricula: Flow<List<CurriculumEntity>> = curriculumDao.observeAll()
    val questions: Flow<List<QuestionEntity>> = questionDao.observeAll()

    suspend fun addSubject(name: String): Long =
        subjectDao.insert(SubjectEntity(name = name.trim()))

    suspend fun addGrade(name: String, sortOrder: Int = 0): Long =
        gradeDao.insert(GradeEntity(name = name.trim(), sortOrder = sortOrder))

    suspend fun addCurriculum(
        subjectId: Long,
        gradeId: Long,
        title: String,
        localFileUri: String? = null
    ): Long = curriculumDao.insert(
        CurriculumEntity(
            subjectId = subjectId,
            gradeId = gradeId,
            title = title.trim(),
            localFileUri = localFileUri
        )
    )

    suspend fun addQuestion(question: QuestionEntity): Long =
        questionDao.insert(question)
}
