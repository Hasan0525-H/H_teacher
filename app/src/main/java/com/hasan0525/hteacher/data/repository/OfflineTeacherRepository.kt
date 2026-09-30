package com.hasan0525.hteacher.data.repository

import com.hasan0525.hteacher.data.local.dao.CurriculumDao
import com.hasan0525.hteacher.data.local.dao.GradeDao
import com.hasan0525.hteacher.data.local.dao.PortfolioDao
import com.hasan0525.hteacher.data.local.dao.QuestionDao
import com.hasan0525.hteacher.data.local.dao.SubjectDao
import com.hasan0525.hteacher.data.local.entity.CurriculumEntity
import com.hasan0525.hteacher.data.local.entity.GradeEntity
import com.hasan0525.hteacher.data.local.entity.PortfolioAttachmentEntity
import com.hasan0525.hteacher.data.local.entity.PortfolioItemEntity
import com.hasan0525.hteacher.data.local.entity.QuestionEntity
import com.hasan0525.hteacher.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

class OfflineTeacherRepository(
    private val subjectDao: SubjectDao,
    private val gradeDao: GradeDao,
    private val curriculumDao: CurriculumDao,
    private val questionDao: QuestionDao,
    private val portfolioDao: PortfolioDao
) {
    val subjects: Flow<List<SubjectEntity>> = subjectDao.observeAll()
    val grades: Flow<List<GradeEntity>> = gradeDao.observeAll()
    val curricula: Flow<List<CurriculumEntity>> = curriculumDao.observeAll()
    val questions: Flow<List<QuestionEntity>> = questionDao.observeAll()
    val portfolioItems: Flow<List<PortfolioItemEntity>> = portfolioDao.observeItems()
    val portfolioAttachments: Flow<List<PortfolioAttachmentEntity>> =
        portfolioDao.observeAttachments()

    suspend fun addSubject(name: String): Long =
        subjectDao.insert(SubjectEntity(name = name.trim()))

    suspend fun updateSubject(subject: SubjectEntity) =
        subjectDao.update(subject)

    suspend fun deleteSubject(subject: SubjectEntity) =
        subjectDao.delete(subject)

    suspend fun addGrade(name: String, sortOrder: Int = 0): Long =
        gradeDao.insert(
            GradeEntity(
                name = name.trim(),
                sortOrder = sortOrder
            )
        )

    suspend fun updateGrade(grade: GradeEntity) =
        gradeDao.update(grade)

    suspend fun deleteGrade(grade: GradeEntity) =
        gradeDao.delete(grade)

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

    suspend fun updateCurriculum(curriculum: CurriculumEntity) =
        curriculumDao.update(curriculum)

    suspend fun deleteCurriculum(curriculum: CurriculumEntity) =
        curriculumDao.delete(curriculum)

    suspend fun addQuestion(question: QuestionEntity): Long =
        questionDao.insert(question)

    suspend fun updateQuestion(question: QuestionEntity) =
        questionDao.update(question)

    suspend fun deleteQuestion(question: QuestionEntity) =
        questionDao.delete(question)

    suspend fun addPortfolioItem(item: PortfolioItemEntity): Long =
        portfolioDao.insertItem(item)

    suspend fun updatePortfolioItem(item: PortfolioItemEntity) =
        portfolioDao.updateItem(item)

    suspend fun deletePortfolioItem(item: PortfolioItemEntity) =
        portfolioDao.deleteItem(item)

    suspend fun addPortfolioAttachment(
        attachment: PortfolioAttachmentEntity
    ): Long = portfolioDao.insertAttachment(attachment)

    suspend fun deletePortfolioAttachment(
        attachment: PortfolioAttachmentEntity
    ) = portfolioDao.deleteAttachment(attachment)
}
