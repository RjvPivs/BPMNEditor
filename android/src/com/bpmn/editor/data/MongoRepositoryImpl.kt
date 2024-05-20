package com.bpmn.editor.data

import com.bpmn.editor.model.Actor
import com.bpmn.editor.model.Scheme
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MongoRepositoryImpl(val realm: Realm) : MongoRepository {
    override suspend fun getScheme(name: String): Scheme? {
        return realm.query<Scheme>(query = "name = $0", name).first().find()
    }

    override fun getSchemes(): Flow<List<Scheme>> {
        return realm.query<Scheme>().asFlow().map { it.list }
    }
    override suspend fun getActor(actor: Actor): Actor? {
        return realm.query<Actor>(query = "_id = $0", actor._id).first().find()
    }
    override suspend fun insertActor(scheme: Scheme, actor: Actor) {
        realm.write {
            val query = query<Scheme>(query = "name == $0", scheme.name).first().find()
           query?.actors?.add(actor)
        }
    }
    override suspend fun deleteActor(scheme: Scheme, actor: Actor) {
        realm.write {
            val query = query<Scheme>(query = "name == $0", scheme.name).first().find()
            val query1 = query<Actor>(query = "_id == $0", actor._id).first().find()
            query?.actors?.remove(actor)
            try {
                if (query1 != null) {
                    delete(query1)
                }
            } catch (_: Exception) {

            }
        }
    }

    override suspend fun updateActor(scheme: Scheme, actor: Actor) {
        realm.write {
            val query = this.query<Scheme>(query = "name == $0", scheme.name).first().find()
            if (query != null) {
                query.actors.find { it._id == actor._id }!!.coordX = actor.coordX
                query.actors.find { it._id == actor._id }!!.coordY = actor.coordY
                query.actors.find { it._id == actor._id }!!.type = actor.type
                query.actors.find { it._id == actor._id }!!.height = actor.height
                query.actors.find { it._id == actor._id }!!.width = actor.width
                query.actors.find { it._id == actor._id }!!.sprite = actor.sprite
                query.actors.find { it._id == actor._id }!!.text = actor.text
                query.actors.find { it._id == actor._id }!!.roleX = actor.roleX
                query.actors.find { it._id == actor._id }!!.roleY = actor.roleY
                query.actors.find { it._id == actor._id }!!.scale = actor.scale
                query.actors.find { it._id == actor._id }!!.startWidth = actor.startWidth
            }
            val query1 = this.query<Actor>(query = "_id == $0", actor._id).first().find()
            query1!!.coordX = actor.coordX
            query1.coordY = actor.coordY
            query1.type = actor.type
            query1.height = actor.height
            query1.width = actor.width
            query1.sprite = actor.sprite
            query1.text = actor.text
            query1.roleX = actor.roleX
            query1.roleY = actor.roleY
            query1.scale = actor.scale
            query1.startWidth = actor.startWidth
        }
    }

    override suspend fun insertScheme(scheme: Scheme) {
        realm.write { copyToRealm(scheme) }
    }

    override suspend fun deleteScheme(id: String) {
        realm.write {
            val person = query<Scheme>(query = "name == $0", id).first().find()
            try {
                person?.let { delete(it) }
            } catch (_: Exception) {

            }
        }
    }
}