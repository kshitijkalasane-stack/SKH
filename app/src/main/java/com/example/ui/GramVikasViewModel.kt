package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

import kotlinx.coroutines.flow.MutableStateFlow
import com.example.ui.login.Role
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay

class GramVikasViewModel(private val repository: GramVikasRepository) : ViewModel() {

    private val _currentRole = MutableStateFlow(Role.ADMIN)
    val currentRole: StateFlow<Role> = _currentRole.asStateFlow()

    fun setCurrentRole(role: Role) {
        _currentRole.value = role
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _delayedMilestoneAlertsEnabled = MutableStateFlow(true)
    val delayedMilestoneAlertsEnabled: StateFlow<Boolean> = _delayedMilestoneAlertsEnabled.asStateFlow()

    private val _budgetOverrunAlertsEnabled = MutableStateFlow(true)
    val budgetOverrunAlertsEnabled: StateFlow<Boolean> = _budgetOverrunAlertsEnabled.asStateFlow()

    fun toggleDelayedMilestoneAlerts(enabled: Boolean) {
        _delayedMilestoneAlertsEnabled.value = enabled
    }

    fun toggleBudgetOverrunAlerts(enabled: Boolean) {
        _budgetOverrunAlertsEnabled.value = enabled
    }

    val allPublicIssues: StateFlow<List<PublicIssue>> = repository.allPublicIssues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDailyReports: StateFlow<List<DailyReport>> = repository.allDailyReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertDailyReport(
        siteName: String,
        cropStatus: String,
        waterLevel: String,
        laborCount: Int,
        weatherCondition: String,
        fieldNotes: String
    ) {
        viewModelScope.launch {
            val report = DailyReport(
                date = System.currentTimeMillis(),
                siteName = siteName,
                cropStatus = cropStatus,
                waterLevel = waterLevel,
                laborCount = laborCount,
                weatherCondition = weatherCondition,
                fieldNotes = fieldNotes
            )
            repository.insertDailyReport(report)
        }
    }

    fun insertPublicIssue(description: String, photoUri: String, location: String?) {
        viewModelScope.launch {
            val issue = PublicIssue(
                description = description,
                photoUri = photoUri,
                location = location,
                timestamp = System.currentTimeMillis()
            )
            repository.insertPublicIssue(issue)
        }
    }

    val allProjects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjectsWithDetails: StateFlow<List<ProjectWithDetails>> = repository.allProjectsWithDetails
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContractors: StateFlow<List<Contractor>> = repository.allContractors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getProjectWithDetails(projectId: Int): StateFlow<ProjectWithDetails?> {
        return repository.getProjectWithDetails(projectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun updateMilestone(milestone: Milestone) {
        viewModelScope.launch {
            repository.updateMilestone(milestone)
        }
    }

    fun refreshProjects() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(1500) // Simulate network sync delay
            _isRefreshing.value = false
        }
    }

    suspend fun seedDatabaseIfEmpty() {
        val currentProjects = repository.allProjects.first()
        if (currentProjects.isEmpty()) {
            val now = System.currentTimeMillis()
            val thirtyDays = 30L * 24 * 60 * 60 * 1000
            
            // Seed Contractors
            repository.insertContractor(Contractor(name = "L&T Constructions", rating = 4.5f, activeProjectsCount = 1))
            repository.insertContractor(Contractor(name = "Rural Build Co.", rating = 3.2f, activeProjectsCount = 1))

            // Seed Projects
            val p1Id = repository.insertProject(
                Project(name = "Village Road Phase 1", location = "District A", description = "Connecting rural areas to highway", status = "In Progress", plannedBudget = 1500000.0, startDate = now - thirtyDays, estimatedEndDate = now + thirtyDays)
            ).toInt()

            val p2Id = repository.insertProject(
                Project(name = "Community Water Tank", location = "District B", description = "Drinking water supply", status = "Delayed", plannedBudget = 500000.0, startDate = now - (2*thirtyDays), estimatedEndDate = now - (5 * 24 * 60 * 60 * 1000))
            ).toInt()

            // Seed Milestones
            repository.insertMilestone(Milestone(projectId = p1Id, title = "Land Clearing", expectedCompletionDate = now - 20L * 24 * 60 * 60 * 1000, isCompleted = true, actualCompletionDate = now - 18L * 24 * 60 * 60 * 1000))
            repository.insertMilestone(Milestone(projectId = p1Id, title = "Base Layer", expectedCompletionDate = now - 5L * 24 * 60 * 60 * 1000, isCompleted = false))
            repository.insertMilestone(Milestone(projectId = p1Id, title = "Asphalting", expectedCompletionDate = now + 15L * 24 * 60 * 60 * 1000, isCompleted = false))

            repository.insertMilestone(Milestone(projectId = p2Id, title = "Foundation", expectedCompletionDate = now - 40L * 24 * 60 * 60 * 1000, isCompleted = true, actualCompletionDate = now - 35L * 24 * 60 * 60 * 1000))
            repository.insertMilestone(Milestone(projectId = p2Id, title = "Structure Setup", expectedCompletionDate = now - 20L * 24 * 60 * 60 * 1000, isCompleted = false)) // Overdue

            // Seed Expenses
            repository.insertExpense(Expense(projectId = p1Id, amount = 200000.0, date = now - 25L * 24 * 60 * 60 * 1000, purpose = "Initial Material Procurement"))
            repository.insertExpense(Expense(projectId = p1Id, amount = 150000.0, date = now - 15L * 24 * 60 * 60 * 1000, purpose = "Labor cost"))
            
            repository.insertExpense(Expense(projectId = p2Id, amount = 300000.0, date = now - 45L * 24 * 60 * 60 * 1000, purpose = "Foundation Material"))
            repository.insertExpense(Expense(projectId = p2Id, amount = 250000.0, date = now - 30L * 24 * 60 * 60 * 1000, purpose = "Structure Cost")) // This will make actual spend > planned (550k > 500k)
        }
    }

    fun addNewProject(name: String, location: String, description: String, budget: Double) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val thirtyDays = 30L * 24 * 60 * 60 * 1000
            val p = com.example.data.Project(
                name = name,
                location = location,
                description = description,
                status = "In Progress",
                plannedBudget = budget,
                startDate = now,
                estimatedEndDate = now + thirtyDays
            )
            repository.insertProject(p)
        }
    }
}

class ViewModelFactory(private val repository: GramVikasRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GramVikasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GramVikasViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
