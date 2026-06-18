package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IdeaDao {
    @Query("SELECT * FROM ideas ORDER BY createdAt DESC")
    fun getAllIdeas(): Flow<List<Idea>>

    @Query("SELECT * FROM ideas WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getIdeasByProject(projectId: Int): Flow<List<Idea>>

    @Query("SELECT * FROM ideas WHERE projectId IS NULL ORDER BY createdAt DESC")
    fun getUnorganizedIdeas(): Flow<List<Idea>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(idea: Idea): Long

    @Update
    suspend fun updateIdea(idea: Idea)

    @Delete
    suspend fun deleteIdea(idea: Idea)

    @Query("UPDATE ideas SET projectId = NULL WHERE projectId = :projectId")
    suspend fun unorganizeIdeasForProject(projectId: Int)

    @Query("UPDATE ideas SET projectId = :targetProjectId WHERE id = :ideaId")
    suspend fun moveIdeaToProject(ideaId: Int, targetProjectId: Int?)
}
