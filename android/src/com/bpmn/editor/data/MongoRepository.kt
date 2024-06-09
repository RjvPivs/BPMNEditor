package com.bpmn.editor.data

import com.bpmn.editor.model.Actor
import com.bpmn.editor.model.Scheme
import kotlinx.coroutines.flow.Flow

interface MongoRepository {
    suspend fun getScheme(name: String): Scheme?
    fun getSchemes(): Flow<List<Scheme>>
    suspend fun insertActor(scheme: Scheme, actor: Actor)
    suspend fun insertScheme(scheme: Scheme)
    suspend fun deleteScheme(id: String)
    suspend fun deleteActor(scheme: Scheme, actor: Actor)
    suspend fun updateActor(scheme: Scheme, actor: Actor)
    suspend fun getActor(actor: Actor): Actor?
    suspend fun updateSchemeName(name: String, newName: String)
}