package com.bpmn.editor.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class Actor() : RealmObject {
    @Index
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var type: String = ""
    var sprite: String = ""
    var coordX: Float = 0f
    var coordY: Float = 0f
    var width: Float = 0f
    var height: Float = 0f
    var text: String = ""
    var roleX: Float = 0f
    var roleY: Float = 0f
    var scale: Float = 1f
    var startWidth: Float = 0f
}