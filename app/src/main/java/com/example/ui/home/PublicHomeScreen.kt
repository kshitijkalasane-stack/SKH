package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.GramVikasViewModel

val DeepGreen = Color(0xFF113D29)
val CreamBg = Color(0xFFFCFAF2)
val WarningBg = Color(0xFFFFF7E3)
val WarningBorder = Color(0xFFF4D17F)
val WarningText = Color(0xFF915C19)
val PillGreenBg = Color(0xFFE6F6ED)
val PillGreenText = Color(0xFF28814F)
val CardYellowBg = Color(0xFFFDF1CD)
val CardYellowHeader = Color(0xFFB57A24)

@Composable
fun PublicHomeScreen(
    viewModel: GramVikasViewModel,
    onLoginClick: () -> Unit,
    onReportIssueClick: () -> Unit
) {
    val projectsWithDetails by viewModel.allProjectsWithDetails.collectAsStateWithLifecycle()
    val totalProjects = projectsWithDetails.size
    val activeProjects = projectsWithDetails.count { it.project.status == "In Progress" }

    Scaffold(
        topBar = {
            PublicTopBar(onLoginClick = onLoginClick)
        },
        containerColor = CreamBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Disclaimer Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(WarningBg)
                        .border(1.dp, WarningBorder, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = WarningText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "This prototype uses fictional static demo information. It is not an official government record.",
                        color = WarningText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Live Dashboard Pill
            item {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(PillGreenBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PillGreenText)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE PUBLIC DASHBOARD • UPDATED 12 AUG 2026",
                        color = PillGreenText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Hero Typography
            item {
                Text(
                    text = "See every village project move forward.",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    lineHeight = 40.sp,
                    color = Color(0xFF222222)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "A public record of work, money, milestones, and proof — built for every citizen.",
                    fontSize = 16.sp,
                    color = Color(0xFF555555),
                    lineHeight = 24.sp
                )
            }

            // Search Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search by village or project name...",
                        color = Color(0xFFAAAAAA),
                        fontSize = 16.sp
                    )
                }
            }

            // Tags Row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoTag(icon = Icons.Outlined.PhotoCamera, text = "Photo-verified updates")
                    InfoTag(icon = Icons.Outlined.ReceiptLong, text = "Open budget records")
                }
            }

            // Dark Green Field Note Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DeepGreen)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "FIELD NOTE / 01",
                        color = Color(0xFF81B59A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Information is a public asset.",
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Every milestone is recorded so citizens can see the same story as the office.",
                        color = Color(0xFFD0E0D8),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ugaon • Niphad • Saykheda",
                            color = Color(0xFFD0E0D8),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Yellow Terminal Card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardYellowBg)
                        .clickable { onReportIssueClick() }
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE89831)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PUBLIC ISSUE & PHOTO TERMINAL",
                            color = CardYellowHeader,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Report Road Damage & Drainage Issues",
                            color = WarningText,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tag @Sarpanch_Pradeep_Patil • Citizens upvote & track action",
                            color = CardYellowHeader,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = WarningText
                    )
                }
            }
        }
    }
}

@Composable
fun PublicTopBar(onLoginClick: () -> Unit) {
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("मराठी") }
    val regionalLanguages = listOf("English", "मराठी", "हिन्दी", "ગુજરાતી", "ಕನ್ನಡ", "தமிழ்", "తెలుగు")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepGreen)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .windowInsetsPadding(WindowInsets.statusBars),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "ग्रा", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "GramVikas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Public Transparency Portal", color = Color(0xFFA0C2B0), fontSize = 12.sp)
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Regional Language Dropdown
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFFA0C2B0), RoundedCornerShape(20.dp))
                        .clickable { languageMenuExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = selectedLanguage, color = Color.White, fontSize = 12.sp)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Language", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                DropdownMenu(
                    expanded = languageMenuExpanded,
                    onDismissRequest = { languageMenuExpanded = false }
                ) {
                    regionalLanguages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                selectedLanguage = language
                                languageMenuExpanded = false
                            }
                        )
                    }
                }
            }
            
            // Login Fill Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2C634B))
                    .clickable { onLoginClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Login", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun InfoTag(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFD0E0D8), RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DeepGreen,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = DeepGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 12.sp, color = Color(0xFF555555), fontWeight = FontWeight.Medium)
            Icon(imageVector = icon, contentDescription = null, tint = DeepGreen, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
    }
}
