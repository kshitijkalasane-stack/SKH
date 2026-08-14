package com.example.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ProjectWithDetails
import com.example.ui.GramVikasViewModel
import java.text.NumberFormat
import java.util.Locale
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: GramVikasViewModel,
    onNavigateToProjects: () -> Unit,
    onNavigateToContractors: () -> Unit,
    onProjectClick: (Int) -> Unit
) {
    val projectsWithDetails by viewModel.allProjectsWithDetails.collectAsStateWithLifecycle()
    val contractors by viewModel.allContractors.collectAsStateWithLifecycle()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    val totalBudget = projectsWithDetails.sumOf { it.project.plannedBudget }
    val delayedProjects = projectsWithDetails.count { it.project.status == "Delayed" }
    val completedProjects = projectsWithDetails.count { it.project.status == "Completed" }
    val inProgressProjects = projectsWithDetails.count { it.project.status == "In Progress" }
    val planningProjects = projectsWithDetails.count { it.project.status == "Planning" }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_dashboard_bg_1786716210155),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Dark translucent overlay for maximum contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF3B82F6), Color(0xFFEC4899))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("GramVikas Dashboard", fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Hero Welcome Banner with Vibrant Gradient
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF4F46E5), // Indigo
                                            Color(0xFF7C3AED), // Purple
                                            Color(0xFF06B6D4)  // Cyan
                                        )
                                    )
                                )
                                .padding(22.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = Color.White.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(
                                            " ADMIN & ENGINEER PORTAL ",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Welcome Back!",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Real-time village infrastructure & project intelligence.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Colorful Metrics Grid Title
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Overview Metrics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Live Data", style = MaterialTheme.typography.labelSmall, color = Color.White)
                            }
                        }
                    }
                }

                // 2x2 Vibrant Color Summary Cards
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ColorfulCard(
                                title = "Total Projects",
                                value = projectsWithDetails.size.toString(),
                                icon = Icons.Default.AccountTree,
                                modifier = Modifier.weight(1f),
                                gradientColors = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6)), // Deep Royal Blue to Bright Blue
                                iconBadgeColor = Color(0xFF60A5FA),
                                onClick = onNavigateToProjects
                            )

                            ColorfulCard(
                                title = "Total Budget",
                                value = if (totalBudget > 0) currencyFormatter.format(totalBudget) else "₹0",
                                icon = Icons.Default.Payments,
                                modifier = Modifier.weight(1f),
                                gradientColors = listOf(Color(0xFF064E3B), Color(0xFF10B981)), // Dark Emerald to Vivid Green
                                iconBadgeColor = Color(0xFF34D399),
                                onClick = onNavigateToProjects
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ColorfulCard(
                                title = "Delayed Alerts",
                                value = delayedProjects.toString(),
                                icon = Icons.Default.Warning,
                                modifier = Modifier.weight(1f),
                                gradientColors = listOf(Color(0xFF7F1D1D), Color(0xFFEF4444)), // Crimson Red to Coral
                                iconBadgeColor = Color(0xFFFCA5A5),
                                onClick = onNavigateToProjects
                            )

                            ColorfulCard(
                                title = "Contractors",
                                value = "${contractors.size} Directory",
                                icon = Icons.Default.Groups,
                                modifier = Modifier.weight(1f),
                                gradientColors = listOf(Color(0xFF4C1D95), Color(0xFF8B5CF6)), // Deep Purple to Bright Violet
                                iconBadgeColor = Color(0xFFC4B5FD),
                                onClick = onNavigateToContractors
                            )
                        }
                    }
                }

                // Colorful Status Breakdown Progress Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                "Project Status Distribution",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Multi-colored segmented bar
                            val total = projectsWithDetails.size.coerceAtLeast(1).toFloat()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                if (completedProjects > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(completedProjects / total)
                                            .fillMaxHeight()
                                            .background(Color(0xFF10B981)) // Green
                                    )
                                }
                                if (inProgressProjects > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(inProgressProjects / total)
                                            .fillMaxHeight()
                                            .background(Color(0xFF3B82F6)) // Blue
                                    )
                                }
                                if (planningProjects > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(planningProjects / total)
                                            .fillMaxHeight()
                                            .background(Color(0xFFF59E0B)) // Amber
                                    )
                                }
                                if (delayedProjects > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(delayedProjects / total)
                                            .fillMaxHeight()
                                            .background(Color(0xFFEF4444)) // Red
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Status Legend Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatusBadge(label = "Completed", count = completedProjects, color = Color(0xFF10B981))
                                StatusBadge(label = "In Progress", count = inProgressProjects, color = Color(0xFF3B82F6))
                                StatusBadge(label = "Planning", count = planningProjects, color = Color(0xFFF59E0B))
                                StatusBadge(label = "Delayed", count = delayedProjects, color = Color(0xFFEF4444))
                            }
                        }
                    }
                }

                // Early Warning Alerts Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFFEF4444), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Early Warning Alerts",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Alerts Listing
                val alerts = generateAlerts(projectsWithDetails)
                if (alerts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B).copy(alpha = 0.85f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "All Systems Operational",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "No project overruns or delayed milestones detected.",
                                        color = Color.White.copy(alpha = 0.8f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(alerts) { alert ->
                        AlertCard(alert = alert, onClick = { onProjectClick(alert.projectId) })
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun ColorfulCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradientColors: List<Color>,
    iconBadgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(135.dp),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Navigate",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        value,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(label: String, count: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            "$label ($count)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

data class DashboardAlert(val projectId: Int, val projectName: String, val message: String, val severity: String) // "High", "Medium"

fun generateAlerts(projects: List<ProjectWithDetails>): List<DashboardAlert> {
    val alerts = mutableListOf<DashboardAlert>()
    val now = System.currentTimeMillis()

    for (p in projects) {
        val actualSpend = p.expenses.sumOf { it.amount }
        if (actualSpend > p.project.plannedBudget) {
            alerts.add(DashboardAlert(p.project.id, p.project.name, "Budget Overrun: Actual spend exceeds planned budget.", "High"))
        }

        val overdueMilestones = p.milestones.filter { !it.isCompleted && it.expectedCompletionDate < now }
        if (overdueMilestones.isNotEmpty()) {
            alerts.add(DashboardAlert(p.project.id, p.project.name, "${overdueMilestones.size} Overdue Milestone(s).", "Medium"))
        }
    }
    return alerts
}

@Composable
fun AlertCard(alert: DashboardAlert, onClick: () -> Unit) {
    val isHigh = alert.severity == "High"
    val cardBg = if (isHigh) Color(0xFF450A0A).copy(alpha = 0.9f) else Color(0xFF451A03).copy(alpha = 0.9f)
    val accentBorder = if (isHigh) Color(0xFFEF4444) else Color(0xFFF59E0B)
    val iconTint = if (isHigh) Color(0xFFFCA5A5) else Color(0xFFFDE68A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(accentBorder.copy(alpha = 0.25f))
                    .border(1.dp, accentBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isHigh) Icons.Default.Error else Icons.Default.Warning,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = accentBorder,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isHigh) " HIGH " else " WARNING ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        alert.projectName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    alert.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View Details",
                tint = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}


