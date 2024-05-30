package org.music.entity

import lombok.Data

import scala.collection.mutable.ArrayBuffer

@Data
class NoteEntity(var messageType: Int, var pianoKey: String, var volume: Int, var tick: ArrayBuffer[Long])

