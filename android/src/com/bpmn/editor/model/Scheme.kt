package com.bpmn.editor.model


import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.Date

class Scheme() : RealmObject {
    @Index
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var name: String = ""
    var date: String = ""
    var actors: RealmList<Actor> = realmListOf()
}