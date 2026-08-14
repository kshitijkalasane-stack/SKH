package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val location: String,
    val description: String,
    val status: String, // "Planning", "In Progress", "Delayed", "Completed"
    val plannedBudget: Double,
    val startDate: Long,
    val estimatedEndDate: Long
)

@Entity(
    tableName = "milestones",
    foreignKeys = [ForeignKey(entity = Project::class, parentColumns = ["id"], childColumns = ["projectId"], onDelete = ForeignKey.CASCADE)]
)
data class Milestone(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val title: String,
    val expectedCompletionDate: Long,
    val isCompleted: Boolean = false,
    val actualCompletionDate: Long? = null,
    val photoUri: String? = null,
    val locationCoordinates: String? = null
)

@Entity(
    tableName = "expenses",
    foreignKeys = [ForeignKey(entity = Project::class, parentColumns = ["id"], childColumns = ["projectId"], onDelete = ForeignKey.CASCADE)]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val amount: Double,
    val date: Long,
    val purpose: String
)

@Entity(tableName = "contractors")
data class Contractor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val rating: Float,
    val activeProjectsCount: Int = 0
)

@Entity(tableName = "public_issues")
data class PublicIssue(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val photoUri: String,
    val location: String?,
    val timestamp: Long,
    val status: String = "Open"
)
