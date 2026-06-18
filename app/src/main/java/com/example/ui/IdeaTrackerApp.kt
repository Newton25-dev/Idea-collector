package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Idea
import com.example.data.Project

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeaTrackerApp(viewModel: IdeaViewModel) {
    val projects by viewModel.allProjects.collectAsStateWithLifecycle()
    val allIdeas by viewModel.allIdeas.collectAsStateWithLifecycle()
    val unorganizedIdeas by viewModel.unorganizedIdeas.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0 = Inbox (Quick Capture), 1 = Projects (Clarify), 2 = Archive (Search/Sort)

    // Modals & Sheets states
    var showAddProjectDialog by remember { mutableStateOf(false) }
    var showEditProjectDialog by remember { mutableStateOf<Project?>(null) }
    var showEditIdeaDialog by remember { mutableStateOf<Idea?>(null) }
    var showMoveIdeaDialog by remember { mutableStateOf<Idea?>(null) }
    
    // Active project view (null means project grid, non-null means inside project view)
    var activeProjectForDetails by remember { mutableStateOf<Project?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb,
                            contentDescription = "Idea Tracker Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Spark",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(borderStroke(MaterialTheme.colorScheme.outlineVariant), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Tabs
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(28.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ideas Tab
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    activeTab = 0
                                    activeProjectForDetails = null
                                }
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val isSelected = activeTab == 0 && activeProjectForDetails == null
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                                contentDescription = "Ideas",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Ideas",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Projects Tab
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    activeTab = 1
                                }
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val isSelected = activeTab == 1 || activeProjectForDetails != null
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Folder else Icons.Outlined.Folder,
                                contentDescription = "Projects",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Projects",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Archive Tab (Done)
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    activeTab = 2
                                    activeProjectForDetails = null
                                }
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val isSelected = activeTab == 2 && activeProjectForDetails == null
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Archive else Icons.Outlined.Archive,
                                contentDescription = "Archive",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Done",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Floating Right Adding Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                if (activeProjectForDetails != null) {
                                    // Trigger project specific dialog if needed, or default project workspace add card triggering
                                } else if (activeTab == 1) {
                                    showAddProjectDialog = true
                                } else {
                                    activeTab = 0
                                    activeProjectForDetails = null
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Item Action",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Keep active view based on state
            Crossfade(
                targetState = if (activeProjectForDetails != null) 3 else activeTab,
                animationSpec = spring(),
                label = "WorkspaceTransition"
            ) { screen ->
                when (screen) {
                    0 -> InboxCaptureScreen(
                        viewModel = viewModel,
                        unorganizedIdeas = unorganizedIdeas,
                        projects = projects,
                        onEditIdea = { showEditIdeaDialog = it },
                        onMoveIdea = { showMoveIdeaDialog = it }
                    )
                    1 -> ProjectsGridScreen(
                        projects = projects,
                        ideas = allIdeas,
                        onProjectSelected = { activeProjectForDetails = it },
                        onAddProjectClick = { showAddProjectDialog = true },
                        onEditProject = { showEditProjectDialog = it },
                        onDeleteProject = { viewModel.deleteProject(it) }
                    )
                    2 -> SearchSortScreen(
                        allIdeas = allIdeas,
                        projects = projects,
                        viewModel = viewModel,
                        onEditIdea = { showEditIdeaDialog = it },
                        onMoveIdea = { showMoveIdeaDialog = it }
                    )
                    3 -> {
                        // Viewing specific project workspace
                        val selectedProj = activeProjectForDetails
                        if (selectedProj != null) {
                            // Find updated version of the project if changed
                            val currentProjInList = projects.find { it.id == selectedProj.id } ?: selectedProj
                            ProjectWorkspaceScreen(
                                project = currentProjInList,
                                projects = projects,
                                ideas = allIdeas.filter { it.projectId == selectedProj.id },
                                onBackClick = { activeProjectForDetails = null },
                                viewModel = viewModel,
                                onEditIdea = { showEditIdeaDialog = it },
                                onMoveIdea = { showMoveIdeaDialog = it }
                            )
                        } else {
                            activeTab = 1
                        }
                    }
                }
            }
        }
    }

    // Modal/Dialog Overlays
    if (showAddProjectDialog) {
        AddEditProjectDialog(
            project = null,
            onDismiss = { showAddProjectDialog = false },
            onSave = { name, desc, color ->
                viewModel.addProject(name, desc, color)
                showAddProjectDialog = false
            }
        )
    }

    if (showEditProjectDialog != null) {
        val proj = showEditProjectDialog!!
        AddEditProjectDialog(
            project = proj,
            onDismiss = { showEditProjectDialog = null },
            onSave = { name, desc, color ->
                viewModel.updateProject(proj.copy(name = name, description = desc, colorHex = color))
                showEditProjectDialog = null
            }
        )
    }

    if (showEditIdeaDialog != null) {
        val idea = showEditIdeaDialog!!
        EditIdeaDialog(
            idea = idea,
            projects = projects,
            onDismiss = { showEditIdeaDialog = null },
            onSave = { title, desc, pId, pri, diff ->
                viewModel.updateIdea(idea.copy(
                    title = title,
                    description = desc,
                    projectId = pId,
                    priority = pri,
                    difficulty = diff
                ))
                showEditIdeaDialog = null
            }
        )
    }

    if (showMoveIdeaDialog != null) {
        val idea = showMoveIdeaDialog!!
        MoveIdeaDialog(
            idea = idea,
            projects = projects,
            onDismiss = { showMoveIdeaDialog = null },
            onMove = { targetProjectId ->
                viewModel.moveIdeaToProject(idea.id, targetProjectId)
                showMoveIdeaDialog = null
            }
        )
    }
}

// ==================== INBOX SCREEN ====================
@Composable
fun InboxCaptureScreen(
    viewModel: IdeaViewModel,
    unorganizedIdeas: List<Idea>,
    projects: List<Project>,
    onEditIdea: (Idea) -> Unit,
    onMoveIdea: (Idea) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Advanced attributes for capture
    var priority by remember { mutableStateOf("Medium") }
    var difficulty by remember { mutableStateOf("Medium") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Quick Capture Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(24.dp),
            border = borderStroke(MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Capture",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Title Input
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { 
                        Text(
                            "What app idea are you thinking of?", 
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        ) 
                    },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
                )

                // Description Input directly below title
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { 
                        Text(
                            "Add notes, steps, or tech stack context (optional)...", 
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f) // Beautiful soft color
                            )
                        ) 
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 1,
                    maxLines = 4,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    )
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Dropdown Filters
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var expandedPriority by remember { mutableStateOf(false) }
                        var expandedDifficulty by remember { mutableStateOf(false) }

                        // Priority Button
                        Box {
                            FilterChip(
                                selected = priority != "Medium",
                                onClick = { expandedPriority = true },
                                label = { Text("Priority: $priority", fontSize = 11.sp) },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (priority) {
                                                    "High" -> Color(0xFFEF4444)
                                                    "Medium" -> Color(0xFFF59E0B)
                                                    else -> Color(0xFF10B981)
                                                }
                                            )
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            DropdownMenu(
                                expanded = expandedPriority,
                                onDismissRequest = { expandedPriority = false }
                            ) {
                                listOf("Low", "Medium", "High").forEach { level ->
                                    DropdownMenuItem(
                                        text = { Text(level) },
                                        onClick = {
                                            priority = level
                                            expandedPriority = false
                                        }
                                    )
                                }
                            }
                        }

                        // Difficulty Button
                        Box {
                            FilterChip(
                                selected = difficulty != "Medium",
                                onClick = { expandedDifficulty = true },
                                label = { Text("Difficulty: $difficulty", fontSize = 11.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            DropdownMenu(
                                expanded = expandedDifficulty,
                                onDismissRequest = { expandedDifficulty = false }
                            ) {
                                listOf("Easy", "Medium", "Hard").forEach { level ->
                                    DropdownMenuItem(
                                        text = { Text(level) },
                                        onClick = {
                                            difficulty = level
                                            expandedDifficulty = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                viewModel.addIdea(
                                    title = title.trim(),
                                    description = description.trim(),
                                    projectId = null, // Inbox gets placed in Inbox directly
                                    priority = priority,
                                    difficulty = difficulty
                                )
                                // Reset fields
                                title = ""
                                description = ""
                                priority = "Medium"
                                difficulty = "Medium"
                                focusManager.clearFocus()
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Capture", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Ideas list (Unorganized Inbox)
        Text(
            text = "📬 Unorganized Ideas (${unorganizedIdeas.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (unorganizedIdeas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateIllustration(
                    title = "Your Mind is Clear!",
                    message = "Awesome! Any flash of app creativity? Capture it above so you never forget it."
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(unorganizedIdeas, key = { it.id }) { idea ->
                    IdeaCard(
                        idea = idea,
                        projectName = null,
                        projectColor = null,
                        onToggleDone = { viewModel.toggleIdeaDone(idea) },
                        onEdit = { onEditIdea(idea) },
                        onMove = { onMoveIdea(idea) },
                        onDelete = { viewModel.deleteIdea(idea) }
                    )
                }
            }
        }
    }
}

// ==================== PROJECTS GRID SCREEN ====================
@Composable
fun ProjectsGridScreen(
    projects: List<Project>,
    ideas: List<Idea>,
    onProjectSelected: (Project) -> Unit,
    onAddProjectClick: () -> Unit,
    onEditProject: (Project) -> Unit,
    onDeleteProject: (Project) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🗂️ Clarify Spaces",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Group, organize, and structure app scopes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onAddProjectClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CreateNewFolder, "Create Project")
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Project")
            }
        }

        if (projects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateIllustration(
                    title = "No Project Spaces Yet",
                    message = "Projects help map ideas to real applications. Create a project above like 'E-Commerce AI' or 'Habit Game'!"
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(projects, key = { it.id }) { project ->
                    val projectIdeas = ideas.filter { it.projectId == project.id }
                    val completed = projectIdeas.count { it.isDone }
                    val total = projectIdeas.size

                    ProjectCard(
                        project = project,
                        totalIdeas = total,
                        completedIdeas = completed,
                        onClick = { onProjectSelected(project) },
                        onEdit = { onEditProject(project) },
                        onDelete = { onDeleteProject(project) }
                    )
                }
            }
        }
    }
}

// ==================== PROJECT WORKSPACE ====================
@Composable
fun ProjectWorkspaceScreen(
    project: Project,
    projects: List<Project>,
    ideas: List<Idea>,
    onBackClick: () -> Unit,
    viewModel: IdeaViewModel,
    onEditIdea: (Idea) -> Unit,
    onMoveIdea: (Idea) -> Unit
) {
    val projectColor = remember(project.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(project.colorHex))
        } catch (_: Exception) {
            Color(0xFF4F46E5)
        }
    }
    
    val totalCount = ideas.size
    val completedCount = ideas.count { it.isDone }
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    var showQuickAddIdeaInProject by remember { mutableStateOf(false) }
    var inProjectIdeaTitle by remember { mutableStateOf("") }
    var inProjectIdeaDesc by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Back Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Back to spaces",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Project Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = projectColor.copy(alpha = 0.08f)),
            border = borderStroke(projectColor.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (project.description.isNotBlank()) {
                            Text(
                                text = project.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(projectColor)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$completedCount/$totalCount ideas completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% Done",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = projectColor
                    )
                }
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = projectColor,
                    trackColor = projectColor.copy(alpha = 0.2f)
                )
            }
        }

        // Action Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💡 Workspace ideas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { showQuickAddIdeaInProject = true },
                colors = ButtonDefaults.buttonColors(containerColor = projectColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, "Add to project")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Idea", fontSize = 12.sp)
            }
        }

        // Expanded Quick Add in Project Form
        AnimatedVisibility(
            visible = showQuickAddIdeaInProject,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = inProjectIdeaTitle,
                        onValueChange = { inProjectIdeaTitle = it },
                        placeholder = { Text("Idea Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inProjectIdeaDesc,
                        onValueChange = { inProjectIdeaDesc = it },
                        placeholder = { Text("Short details...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            showQuickAddIdeaInProject = false
                            inProjectIdeaTitle = ""
                            inProjectIdeaDesc = ""
                        }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (inProjectIdeaTitle.isNotBlank()) {
                                    viewModel.addIdea(
                                        title = inProjectIdeaTitle.trim(),
                                        description = inProjectIdeaDesc.trim(),
                                        projectId = project.id
                                    )
                                    inProjectIdeaTitle = ""
                                    inProjectIdeaDesc = ""
                                    showQuickAddIdeaInProject = false
                                }
                            },
                            enabled = inProjectIdeaTitle.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = projectColor)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }

        if (ideas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateIllustration(
                    title = "This Workspace is Empty",
                    message = "Ready to start planning? Add a new idea here, or move an idea from the main Inbox capture space."
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(ideas, key = { it.id }) { idea ->
                    IdeaCard(
                        idea = idea,
                        projectName = null,
                        projectColor = null,
                        onToggleDone = { viewModel.toggleIdeaDone(idea) },
                        onEdit = { onEditIdea(idea) },
                        onMove = { onMoveIdea(idea) },
                        onDelete = { viewModel.deleteIdea(idea) }
                    )
                }
            }
        }
    }
}

// ==================== SEARCH AND SORT SCREEN ====================
@Composable
fun SearchSortScreen(
    allIdeas: List<Idea>,
    projects: List<Project>,
    viewModel: IdeaViewModel,
    onEditIdea: (Idea) -> Unit,
    onMoveIdea: (Idea) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("Date") } // Date, Priority, Difficulty, Status
    var selectedStatusFilter by remember { mutableStateOf("All") } // All, Active, Completed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🔍 Explore & Sort",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search title, descriptions, tags...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filters UI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Filters
            Row {
                listOf("All", "Active", "Done").forEach { filter ->
                    InputChip(
                        selected = selectedStatusFilter == filter,
                        onClick = { selectedStatusFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            // Sort Selector Button inside dropdown menu style
            var expandedSortMenu by remember { mutableStateOf(false) }
            Box {
                Button(
                    onClick = { expandedSortMenu = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Sort, null, Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sort: $sortBy", fontSize = 11.sp)
                }

                DropdownMenu(
                    expanded = expandedSortMenu,
                    onDismissRequest = { expandedSortMenu = false }
                ) {
                    listOf("Date", "Priority", "Difficulty", "Completeness").forEach { sortType ->
                        DropdownMenuItem(
                            text = { Text(sortType) },
                            onClick = {
                                sortBy = sortType
                                expandedSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Process search and filters
        val filteredIdeas = remember(allIdeas, searchQuery, selectedStatusFilter, sortBy, projects) {
            var temp = allIdeas.filter {
                (it.title.contains(searchQuery, ignoreCase = true) ||
                 it.description.contains(searchQuery, ignoreCase = true) ||
                 it.priority.contains(searchQuery, ignoreCase = true) ||
                 it.difficulty.contains(searchQuery, ignoreCase = true))
            }

            temp = when (selectedStatusFilter) {
                "Active" -> temp.filter { !it.isDone }
                "Done" -> temp.filter { it.isDone }
                else -> temp
            }

            when (sortBy) {
                "Priority" -> {
                    // Sort High -> Medium -> Low
                    temp.sortedBy {
                        when (it.priority) {
                            "High" -> 0
                            "Medium" -> 1
                            "Low" -> 2
                            else -> 3
                        }
                    }
                }
                "Difficulty" -> {
                    // Hard -> Medium -> Easy
                    temp.sortedBy {
                        when (it.difficulty) {
                            "Hard" -> 0
                            "Medium" -> 1
                            "Easy" -> 2
                            else -> 3
                        }
                    }
                }
                "Completeness" -> temp.sortedBy { it.isDone }
                else -> temp.sortedByDescending { it.createdAt } // Date (Default)
            }
        }

        if (filteredIdeas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateIllustration(
                    title = "No Matches Found",
                    message = "Try modifying your search text, or adjusting filters for Active / Completed ideas."
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredIdeas, key = { it.id }) { idea ->
                    // Resolve project context
                    val project = projects.find { it.id == idea.projectId }
                    IdeaCard(
                        idea = idea,
                        projectName = project?.name,
                        projectColor = project?.colorHex,
                        onToggleDone = { viewModel.toggleIdeaDone(idea) },
                        onEdit = { onEditIdea(idea) },
                        onMove = { onMoveIdea(idea) },
                        onDelete = { viewModel.deleteIdea(idea) }
                    )
                }
            }
        }
    }
}

// ==================== DESIGN COMPONENTS ====================

@Composable
fun IdeaCard(
    idea: Idea,
    projectName: String?,
    projectColor: String?,
    onToggleDone: () -> Unit,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit
) {
    val projColor = remember(projectColor) {
        if (projectColor != null) {
            try {
                Color(android.graphics.Color.parseColor(projectColor))
            } catch (_: Exception) {
                null
            }
        } else null
    }

    val priorityColor = when (idea.priority) {
        "High" -> Color(0xFFEF4444)
        "Medium" -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    val difficultyBg = when (idea.difficulty) {
        "Hard" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        "Medium" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleDone),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (idea.isDone) {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = borderStroke(
            color = if (idea.isDone) {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox Radio Selector
            IconButton(
                onClick = onToggleDone,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = if (idea.isDone) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Toggle status",
                    tint = if (idea.isDone) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            // Idea Details
            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = idea.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (idea.isDone) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (idea.isDone) TextDecoration.LineThrough else TextDecoration.None
                )

                // Description
                if (idea.description.isNotBlank()) {
                    Text(
                        text = idea.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (idea.isDone) {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Attributes List (Row of Chips)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Priority Circle Dot / Text
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(priorityColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(priorityColor)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(idea.priority, fontSize = 10.sp, color = priorityColor, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Difficulty Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(difficultyBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Level: ${idea.difficulty}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Project indicator badge
                    if (projectName != null && projColor != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(projColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = projectName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = projColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Options triggers
            var expandedMenu by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expandedMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options Dropdown",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                DropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Idea") },
                        leadingIcon = { Icon(Icons.Default.Edit, "Edit") },
                        onClick = {
                            onEdit()
                            expandedMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Move Space") },
                        leadingIcon = { Icon(Icons.Default.DriveFileMove, "Move selection") },
                        onClick = {
                            onMove()
                            expandedMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Idea", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error) },
                        onClick = {
                            onDelete()
                            expandedMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: Project,
    totalIdeas: Int,
    completedIdeas: Int,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val projectColor = remember(project.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(project.colorHex))
        } catch (_: Exception) {
            Color(0xFF4F46E5)
        }
    }

    val progress = if (totalIdeas > 0) completedIdeas.toFloat() / totalIdeas else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = borderStroke(MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Colored Indicator Node
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(projectColor)
                )

                // Small Actions dropdown
                var expMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { expMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Project actions",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    DropdownMenu(expanded = expMenu, onDismissRequest = { expMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Edit Name/Color") },
                            leadingIcon = { Icon(Icons.Default.Edit, null) },
                            onClick = {
                                onEdit()
                                expMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Project", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                onDelete()
                                expMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Project Title
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Project description
            if (project.description.isNotBlank()) {
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$completedIdeas/$totalIdeas done",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = projectColor,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = projectColor,
                trackColor = projectColor.copy(alpha = 0.15f)
            )
        }
    }
}

// Custom empty screen visual indicator
@Composable
fun EmptyStateIllustration(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(100.dp)) {
            val centerOffset = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 3.5f
            val dotRadius = radius / 12f

            // Clean modern concentric circles and structural dotted grid
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.3f),
                radius = radius * 1.5f,
                center = centerOffset,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = Color.DarkGray.copy(alpha = 0.15f),
                radius = radius,
                center = centerOffset,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawCircle(
                color = Color.DarkGray.copy(alpha = 0.4f),
                radius = dotRadius,
                center = centerOffset
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, start = 12.dp, end = 12.dp)
        )
    }
}

// Helpers for cleaner styling
fun borderStroke(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)

// ==================== MODALS & DIALOG ASSEMBLIES ====================

@Composable
fun AddEditProjectDialog(
    project: Project?,
    onDismiss: () -> Unit,
    onSave: (name: String, desc: String, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf(project?.name ?: "") }
    var desc by remember { mutableStateOf(project?.description ?: "") }
    var selectedColor by remember { mutableStateOf(project?.colorHex ?: "#4F46E5") }

    val colorPalette = listOf(
        "#4F46E5", // Elegant Blue-Indigo
        "#10B981", // Emerald
        "#F59E0B", // Amber
        "#EF4444", // Coral Red
        "#8B5CF6", // Violet
        "#EC4899", // Vivid Pink
        "#6B7280"  // Charcoal Grey
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (project == null) "Create Project Space" else "Edit Project Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Name") },
                    placeholder = { Text("e.g., Cooking Recipe App") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    placeholder = { Text("Describe target features or tech...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    "Select Color Tag",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorPalette.forEach { hex ->
                        val parsedColor = remember(hex) { Color(android.graphics.Color.parseColor(hex)) }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(parsedColor)
                                .clickable { selectedColor = hex }
                                .border(
                                    width = if (selectedColor == hex) 3.dp else 0.dp,
                                    color = if (selectedColor == hex) MaterialTheme.colorScheme.outline else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name.trim(), desc.trim(), selectedColor)
                            }
                        },
                        enabled = name.isNotBlank(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun EditIdeaDialog(
    idea: Idea,
    projects: List<Project>,
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, projectId: Int?, priority: String, difficulty: String) -> Unit
) {
    var title by remember { mutableStateOf(idea.title) }
    var desc by remember { mutableStateOf(idea.description) }
    var selectedProjectId by remember { mutableStateOf<Int?>(idea.projectId) }
    var priority by remember { mutableStateOf(idea.priority) }
    var difficulty by remember { mutableStateOf(idea.difficulty) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = "Edit Idea Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Idea Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Priority Select
                    Text("Priority", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Low", "Medium", "High").forEach { level ->
                            FilterChip(
                                selected = priority == level,
                                onClick = { priority = level },
                                label = { Text(level) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Difficulty Select
                    Text("Difficulty", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Easy", "Medium", "Hard").forEach { level ->
                            FilterChip(
                                selected = difficulty == level,
                                onClick = { difficulty = level },
                                label = { Text(level) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Associated Project Select
                    Text("Assigned Project Space", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    
                    var expandProjectMenu by remember { mutableStateOf(false) }
                    val currentProjectName = remember(selectedProjectId, projects) {
                        if (selectedProjectId == null) "📬 Unorganized (Inbox)"
                        else projects.find { it.id == selectedProjectId }?.name ?: "📬 Unorganized (Inbox)"
                    }

                    Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                        OutlinedButton(
                            onClick = { expandProjectMenu = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(currentProjectName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        DropdownMenu(
                            expanded = expandProjectMenu,
                            onDismissRequest = { expandProjectMenu = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            DropdownMenuItem(
                                text = { Text("📬 Unorganized (Inbox)") },
                                onClick = {
                                    selectedProjectId = null
                                    expandProjectMenu = false
                                }
                            )
                            projects.forEach { proj ->
                                DropdownMenuItem(
                                    text = { Text(proj.name) },
                                    onClick = {
                                        selectedProjectId = proj.id
                                        expandProjectMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    onSave(title.trim(), desc.trim(), selectedProjectId, priority, difficulty)
                                }
                            },
                            enabled = title.isNotBlank(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoveIdeaDialog(
    idea: Idea,
    projects: List<Project>,
    onDismiss: () -> Unit,
    onMove: (projectId: Int?) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Clarify Workspace",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Move idea \"${idea.title}\" to an active project group:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Render list of projects to click instantly
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        Surface(
                            onClick = { onMove(null) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (idea.projectId == null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth().clickable { onMove(null) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MoveToInbox, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("📬 Move to Inbox (Unorganized)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }

                    items(projects) { proj ->
                        val projColor = try {
                            Color(android.graphics.Color.parseColor(proj.colorHex))
                        } catch (_: Exception) {
                            Color(0xFF4F46E5)
                        }

                        Surface(
                            onClick = { onMove(proj.id) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (idea.projectId == proj.id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth().clickable { onMove(proj.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(projColor)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(proj.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
