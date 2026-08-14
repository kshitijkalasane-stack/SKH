package com.example.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ProjectWithDetails
import com.example.ui.GramVikasViewModel
import com.example.ui.login.Role
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: GramVikasViewModel,
    onNavigateToProjects: () -> Unit,
    onNavigateToContractors: () -> Unit,
    onProjectClick: (Int) -> Unit,
    onNavigateToDailyReports: () -> Unit,
    onNavigateToIssues: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val projectsWithDetails by viewModel.allProjectsWithDetails.collectAsStateWithLifecycle()
    val contractors by viewModel.allContractors.collectAsStateWithLifecycle()
    val publicIssues by viewModel.allPublicIssues.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    val totalBudget = projectsWithDetails.sumOf { it.project.plannedBudget }
    val totalExpenses = projectsWithDetails.sumOf { pwd -> pwd.expenses.sumOf { it.amount } }
    val delayedProjects = projectsWithDetails.count { it.project.status == "Delayed" }
    val completedProjects = projectsWithDetails.count { it.project.status == "Completed" }
    val inProgressProjects = projectsWithDetails.count { it.project.status == "In Progress" }
    val planningProjects = projectsWithDetails.count { it.project.status == "Planning" }

    val allMilestones = projectsWithDetails.flatMap { it.milestones }
    val totalMilestones = allMilestones.size
    val completedMilestones = allMilestones.count { it.isCompleted }
    val overdueMilestones = allMilestones.filter { !it.isCompleted && it.expectedCompletionDate < System.currentTimeMillis() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            GramVikasDrawerContent(
                currentRole = currentRole,
                onNavigateToDailyReports = onNavigateToDailyReports,
                onNavigateToProjects = onNavigateToProjects,
                onNavigateToContractors = onNavigateToContractors,
                onNavigateToIssues = onNavigateToIssues,
                onNavigateToSettings = onNavigateToSettings,
                onLogout = onLogout,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = currentRole.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "GramVikas Dashboard",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp
                                )
                                Text(
                                    "${currentRole.title} Portal",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Slidebar Drawer", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Simple Hero Banner
            item {
                RoleHeroBannerSimple(role = currentRole)
            }

            // Metrics Header
            item {
                Text(
                    text = when (currentRole) {
                        Role.ADMIN -> "System & Financial Overview"
                        Role.PROJECT_HEAD -> "District Progress Overview"
                        Role.SITE_ENGINEER -> "Field Execution Overview"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Metrics Cards
            item {
                when (currentRole) {
                    Role.ADMIN -> {
                        AdminMetricsSimple(
                            totalProjects = projectsWithDetails.size,
                            totalBudgetStr = currencyFormatter.format(totalBudget),
                            budgetOverrunCount = projectsWithDetails.count { pwd ->
                                pwd.expenses.sumOf { it.amount } > pwd.project.plannedBudget
                            },
                            contractorsCount = contractors.size,
                            onNavigateToProjects = onNavigateToProjects,
                            onNavigateToContractors = onNavigateToContractors
                        )
                    }
                    Role.PROJECT_HEAD -> {
                        HeadMetricsSimple(
                            milestoneCompletionPct = if (totalMilestones > 0) ((completedMilestones.toFloat() / totalMilestones) * 100).toInt() else 0,
                            overdueMilestoneCount = overdueMilestones.size,
                            delayedProjectsCount = delayedProjects,
                            activeSupervisedSites = inProgressProjects + planningProjects,
                            onNavigateToProjects = onNavigateToProjects
                        )
                    }
                    Role.SITE_ENGINEER -> {
                        EngineerMetricsSimple(
                            assignedWorkOrders = allMilestones.count { !it.isCompleted },
                            photoProofCount = completedMilestones,
                            nearbyIssuesCount = publicIssues.size,
                            siteSafetyStatus = "Verified",
                            onNavigateToProjects = onNavigateToProjects
                        )
                    }
                }
            }

            // Status Distribution
            item {
                ProjectStatusSimpleCard(
                    completed = completedProjects,
                    inProgress = inProgressProjects,
                    planning = planningProjects,
                    delayed = delayedProjects,
                    total = projectsWithDetails.size
                )
            }

            // Quick Actions
            item {
                RoleQuickActionsSimple(
                    role = currentRole,
                    onNavigateToProjects = onNavigateToProjects,
                    onNavigateToContractors = onNavigateToContractors
                )
            }

            // Role-Specific Detail Section
            when (currentRole) {
                Role.ADMIN -> {
                    item {
                        AdminFinancialSimpleSection(
                            projectsWithDetails = projectsWithDetails,
                            currencyFormatter = currencyFormatter,
                            onProjectClick = onProjectClick
                        )
                    }
                }
                Role.PROJECT_HEAD -> {
                    item {
                        HeadMilestoneSimpleSection(
                            projectsWithDetails = projectsWithDetails,
                            onProjectClick = onProjectClick
                        )
                    }
                }
                Role.SITE_ENGINEER -> {
                    item {
                        EngineerWorkOrdersSimpleSection(
                            projectsWithDetails = projectsWithDetails,
                            onProjectClick = onProjectClick
                        )
                    }
                }
            }

            // Alerts Header
            item {
                Text(
                    text = "Operational Alerts & Notices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Alerts Listing
            val alerts = generateRoleAlerts(projectsWithDetails, currentRole)
            if (alerts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "No Active Alerts",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    "All operations within normal parameters for ${currentRole.title}.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            } else {
                items(alerts) { alert ->
                    AlertCardSimple(alert = alert, onClick = { onProjectClick(alert.projectId) })
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
  }
}

@Composable
fun RoleHeroBannerSimple(role: Role) {
    val (badge, title, subtitle) = when (role) {
        Role.ADMIN -> Triple("ADMINISTRATOR", "Administrator Portal", "System control, financial audits & contractor governance.")
        Role.PROJECT_HEAD -> Triple("PROJECT HEAD", "Project Head Portal", "District schedule supervision & milestone bottleneck tracking.")
        Role.SITE_ENGINEER -> Triple("SITE ENGINEER", "Site Engineer Portal", "Field execution logging & photo proof verification.")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "  $badge  ",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AdminMetricsSimple(
    totalProjects: Int,
    totalBudgetStr: String,
    budgetOverrunCount: Int,
    contractorsCount: Int,
    onNavigateToProjects: () -> Unit,
    onNavigateToContractors: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SimpleMetricCard(
                title = "Total Projects",
                value = "$totalProjects",
                icon = Icons.Default.AccountTree,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
            SimpleMetricCard(
                title = "Total Budget",
                value = totalBudgetStr,
                icon = Icons.Default.Payments,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SimpleMetricCard(
                title = "Budget Overruns",
                value = "$budgetOverrunCount",
                icon = Icons.Default.Warning,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
            SimpleMetricCard(
                title = "Contractors",
                value = "$contractorsCount",
                icon = Icons.Default.Groups,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToContractors
            )
        }
    }
}

@Composable
fun HeadMetricsSimple(
    milestoneCompletionPct: Int,
    overdueMilestoneCount: Int,
    delayedProjectsCount: Int,
    activeSupervisedSites: Int,
    onNavigateToProjects: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SimpleMetricCard(
                title = "Milestone Progress",
                value = "$milestoneCompletionPct%",
                icon = Icons.Default.AssignmentTurnedIn,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
            SimpleMetricCard(
                title = "Overdue Stages",
                value = "$overdueMilestoneCount",
                icon = Icons.Default.Timer,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SimpleMetricCard(
                title = "Delayed Projects",
                value = "$delayedProjectsCount",
                icon = Icons.Default.ErrorOutline,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
            SimpleMetricCard(
                title = "Active Sites",
                value = "$activeSupervisedSites",
                icon = Icons.Default.LocationOn,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
        }
    }
}

@Composable
fun EngineerMetricsSimple(
    assignedWorkOrders: Int,
    photoProofCount: Int,
    nearbyIssuesCount: Int,
    siteSafetyStatus: String,
    onNavigateToProjects: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SimpleMetricCard(
                title = "Pending Work Orders",
                value = "$assignedWorkOrders",
                icon = Icons.Default.Engineering,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
            SimpleMetricCard(
                title = "Photo Proofs",
                value = "$photoProofCount",
                icon = Icons.Default.PhotoCamera,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SimpleMetricCard(
                title = "Citizen Reports",
                value = "$nearbyIssuesCount",
                icon = Icons.Default.ReportProblem,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
            SimpleMetricCard(
                title = "Site Readiness",
                value = siteSafetyStatus,
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProjects
            )
        }
    }
}

@Composable
fun SimpleMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ProjectStatusSimpleCard(
    completed: Int,
    inProgress: Int,
    planning: Int,
    delayed: Int,
    total: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Project Status Distribution",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            val totalF = total.coerceAtLeast(1).toFloat()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (completed > 0) {
                    Box(modifier = Modifier.weight(completed / totalF).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                }
                if (inProgress > 0) {
                    Box(modifier = Modifier.weight(inProgress / totalF).fillMaxHeight().background(MaterialTheme.colorScheme.secondary))
                }
                if (planning > 0) {
                    Box(modifier = Modifier.weight(planning / totalF).fillMaxHeight().background(MaterialTheme.colorScheme.tertiary))
                }
                if (delayed > 0) {
                    Box(modifier = Modifier.weight(delayed / totalF).fillMaxHeight().background(MaterialTheme.colorScheme.error))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusItemSimple(label = "Completed", count = completed)
                StatusItemSimple(label = "In Progress", count = inProgress)
                StatusItemSimple(label = "Planning", count = planning)
                StatusItemSimple(label = "Delayed", count = delayed)
            }
        }
    }
}

@Composable
fun StatusItemSimple(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RoleQuickActionsSimple(
    role: Role,
    onNavigateToProjects: () -> Unit,
    onNavigateToContractors: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (role) {
                    Role.ADMIN -> {
                        SimpleActionButton(label = "Projects", icon = Icons.Default.AccountTree, modifier = Modifier.weight(1f), onClick = onNavigateToProjects)
                        SimpleActionButton(label = "Contractors", icon = Icons.Default.Groups, modifier = Modifier.weight(1f), onClick = onNavigateToContractors)
                        SimpleActionButton(label = "Add Project", icon = Icons.Default.Add, modifier = Modifier.weight(1f), onClick = onNavigateToProjects)
                    }
                    Role.PROJECT_HEAD -> {
                        SimpleActionButton(label = "Milestones", icon = Icons.Default.AssignmentTurnedIn, modifier = Modifier.weight(1f), onClick = onNavigateToProjects)
                        SimpleActionButton(label = "Delays", icon = Icons.Default.Warning, modifier = Modifier.weight(1f), onClick = onNavigateToProjects)
                        SimpleActionButton(label = "Projects", icon = Icons.Default.AccountTree, modifier = Modifier.weight(1f), onClick = onNavigateToProjects)
                    }
                    Role.SITE_ENGINEER -> {
                        SimpleActionButton(label = "Work Orders", icon = Icons.Default.Engineering, modifier = Modifier.weight(1f), onClick = onNavigateToProjects)
                        SimpleActionButton(label = "Photo Proof", icon = Icons.Default.PhotoCamera, modifier = Modifier.weight(1f), onClick = onNavigateToProjects)
                        SimpleActionButton(label = "Issues", icon = Icons.Default.ReportProblem, modifier = Modifier.weight(1f), onClick = onNavigateToProjects)
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AdminFinancialSimpleSection(
    projectsWithDetails: List<ProjectWithDetails>,
    currencyFormatter: NumberFormat,
    onProjectClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Budget vs Actual Audit",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            projectsWithDetails.take(4).forEach { pwd ->
                val spent = pwd.expenses.sumOf { it.amount }
                val budget = pwd.project.plannedBudget
                val isOverrun = spent > budget

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProjectClick(pwd.project.id) }
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(pwd.project.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                        Text(
                            text = if (isOverrun) "Over Budget" else "On Track",
                            color = if (isOverrun) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Spent: ${currencyFormatter.format(spent)} / Budget: ${currencyFormatter.format(budget)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun HeadMilestoneSimpleSection(
    projectsWithDetails: List<ProjectWithDetails>,
    onProjectClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Milestone Stage Overview",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            projectsWithDetails.take(4).forEach { pwd ->
                val totalM = pwd.milestones.size
                val compM = pwd.milestones.count { it.isCompleted }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProjectClick(pwd.project.id) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pwd.project.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                        Text("$compM of $totalM Stages Completed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun EngineerWorkOrdersSimpleSection(
    projectsWithDetails: List<ProjectWithDetails>,
    onProjectClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Active Field Work Orders",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            val uncompleted = projectsWithDetails.flatMap { pwd ->
                pwd.milestones.filter { !it.isCompleted }.map { m -> Pair(pwd.project, m) }
            }

            if (uncompleted.isEmpty()) {
                Text("No pending work orders.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                uncompleted.take(4).forEach { (project, milestone) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProjectClick(project.id) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(milestone.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                            Text("Site: ${project.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                "Verify",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

data class DashboardAlert(val projectId: Int, val projectName: String, val message: String, val severity: String)

fun generateRoleAlerts(projects: List<ProjectWithDetails>, role: Role): List<DashboardAlert> {
    val alerts = mutableListOf<DashboardAlert>()
    for (pwd in projects) {
        val spent = pwd.expenses.sumOf { it.amount }
        val budget = pwd.project.plannedBudget
        val overdueMilestones = pwd.milestones.filter { !it.isCompleted && it.expectedCompletionDate < System.currentTimeMillis() }

        when (role) {
            Role.ADMIN -> {
                if (spent > budget) {
                    alerts.add(DashboardAlert(pwd.project.id, pwd.project.name, "Budget exceeded by ₹${spent - budget}", "High"))
                }
            }
            Role.PROJECT_HEAD -> {
                if (overdueMilestones.isNotEmpty() || pwd.project.status == "Delayed") {
                    alerts.add(DashboardAlert(pwd.project.id, pwd.project.name, "${overdueMilestones.size} milestone stages overdue", "Warning"))
                }
            }
            Role.SITE_ENGINEER -> {
                if (pwd.project.status == "Delayed" || overdueMilestones.isNotEmpty()) {
                    alerts.add(DashboardAlert(pwd.project.id, pwd.project.name, "Action required on pending site tasks", "Warning"))
                }
            }
        }
    }
    return alerts
}

@Composable
fun AlertCardSimple(alert: DashboardAlert, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (alert.severity == "High") Icons.Default.Error else Icons.Default.Warning,
                contentDescription = null,
                tint = if (alert.severity == "High") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    alert.projectName,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun GramVikasDrawerContent(
    currentRole: Role,
    onNavigateToDailyReports: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToContractors: () -> Unit,
    onNavigateToIssues: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "GramVikas Portal",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                    Text(
                        currentRole.title,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                label = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                selected = true,
                onClick = onCloseDrawer
            )
            Spacer(modifier = Modifier.height(4.dp))
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                label = { Text("Daily Update Report", fontWeight = FontWeight.Bold) },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onNavigateToDailyReports()
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.AccountTree, contentDescription = null) },
                label = { Text("Projects & Sites") },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onNavigateToProjects()
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                label = { Text("Contractors & Labor") },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onNavigateToContractors()
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.ReportProblem, contentDescription = null) },
                label = { Text("Citizen Reports / Issues") },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onNavigateToIssues()
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                label = { Text("Settings") },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onNavigateToSettings()
                }
            )

            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                label = { Text("Logout", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onLogout()
                }
            )
        }
    }
