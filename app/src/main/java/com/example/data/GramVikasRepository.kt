package com.example.data

import kotlinx.coroutines.flow.Flow

class GramVikasRepository(private val dao: GramVikasDao) {
    val allProjects: Flow<List<Project>> = dao.getAllProjects()
    val allProjectsWithDetails: Flow<List<ProjectWithDetails>> = dao.getAllProjectsWithDetails()
    val allContractors: Flow<List<Contractor>> = dao.getAllContractors()

    fun getProjectWithDetails(projectId: Int): Flow<ProjectWithDetails?> {
        return dao.getProjectWithDetails(projectId)
    }

    suspend fun insertProject(project: Project): Long {
        return dao.insertProject(project)
    }

    suspend fun updateProject(project: Project) {
        dao.updateProject(project)
    }

    suspend fun insertMilestone(milestone: Milestone) {
        dao.insertMilestone(milestone)
    }

    suspend fun updateMilestone(milestone: Milestone) {
        dao.updateMilestone(milestone)
    }

    suspend fun insertExpense(expense: Expense) {
        dao.insertExpense(expense)
    }

    suspend fun insertContractor(contractor: Contractor) {
        dao.insertContractor(contractor)
    }
}
