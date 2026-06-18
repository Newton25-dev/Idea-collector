package com.example.data

import kotlinx.coroutines.flow.Flow

class IdeaRepository(
    private val projectDao: ProjectDao,
    private val ideaDao: IdeaDao
) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()
    val allIdeas: Flow<List<Idea>> = ideaDao.getAllIdeas()
    val unorganizedIdeas: Flow<List<Idea>> = ideaDao.getUnorganizedIdeas()

    fun getIdeasByProject(projectId: Int): Flow<List<Idea>> = ideaDao.getIdeasByProject(projectId)

    suspend fun insertProject(project: Project): Long = projectDao.insertProject(project)

    suspend fun updateProject(project: Project) = projectDao.updateProject(project)

    suspend fun deleteProject(project: Project) {
        // First unorganize ideas tied to this project so they go to Unorganized (Inbox)
        ideaDao.unorganizeIdeasForProject(project.id)
        // Then delete the project itself
        projectDao.deleteProject(project)
    }

    suspend fun insertIdea(idea: Idea): Long = ideaDao.insertIdea(idea)

    suspend fun updateIdea(idea: Idea) = ideaDao.updateIdea(idea)

    suspend fun deleteIdea(idea: Idea) = ideaDao.deleteIdea(idea)

    suspend fun moveIdeaToProject(ideaId: Int, targetProjectId: Int?) =
        ideaDao.moveIdeaToProject(ideaId, targetProjectId)
}
