package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Idea
import com.example.data.IdeaRepository
import com.example.data.Project
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IdeaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: IdeaRepository

    val allProjects: StateFlow<List<Project>>
    val allIdeas: StateFlow<List<Idea>>
    val unorganizedIdeas: StateFlow<List<Idea>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = IdeaRepository(database.projectDao(), database.ideaDao())
        
        allProjects = repository.allProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        allIdeas = repository.allIdeas.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        unorganizedIdeas = repository.unorganizedIdeas.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addProject(name: String, description: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertProject(Project(name = name, description = description, colorHex = colorHex))
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            repository.updateProject(project)
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }

    fun addIdea(title: String, description: String = "", projectId: Int? = null, priority: String = "Medium", difficulty: String = "Medium") {
        viewModelScope.launch {
            repository.insertIdea(Idea(
                title = title,
                description = description,
                projectId = projectId,
                priority = priority,
                difficulty = difficulty
            ))
        }
    }

    fun updateIdea(idea: Idea) {
        viewModelScope.launch {
            repository.updateIdea(idea)
        }
    }

    fun toggleIdeaDone(idea: Idea) {
        viewModelScope.launch {
            repository.updateIdea(idea.copy(isDone = !idea.isDone))
        }
    }

    fun deleteIdea(idea: Idea) {
        viewModelScope.launch {
            repository.deleteIdea(idea)
        }
    }

    fun moveIdeaToProject(ideaId: Int, targetProjectId: Int?) {
        viewModelScope.launch {
            repository.moveIdeaToProject(ideaId, targetProjectId)
        }
    }
}
