package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

data class ProjectWithDetails(
    @Embedded val project: Project,
    @Relation(
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val milestones: List<Milestone>,
    @Relation(
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val expenses: List<Expense>
)

@Dao
interface GramVikasDao {
    @Query("SELECT * FROM projects ORDER BY estimatedEndDate ASC")
    fun getAllProjects(): Flow<List<Project>>

    @Transaction
    @Query("SELECT * FROM projects ORDER BY estimatedEndDate ASC")
    fun getAllProjectsWithDetails(): Flow<List<ProjectWithDetails>>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectWithDetails(projectId: Int): Flow<ProjectWithDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: Milestone)

    @Update
    suspend fun updateMilestone(milestone: Milestone)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM contractors ORDER BY rating DESC")
    fun getAllContractors(): Flow<List<Contractor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContractor(contractor: Contractor)

    @Update
    suspend fun updateProject(project: Project)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublicIssue(issue: PublicIssue)

    @Query("SELECT * FROM public_issues ORDER BY timestamp DESC")
    fun getAllPublicIssues(): Flow<List<PublicIssue>>

    @Query("SELECT * FROM daily_reports ORDER BY date DESC")
    fun getAllDailyReports(): Flow<List<DailyReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyReport(report: DailyReport)
}
