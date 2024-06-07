package org.music.entity

import lombok.Data
import org.music.entity.NoteEntity

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

@Data
class TrackEntity(var notes: mutable.Map[Int, mutable.LinkedHashMap[String, ArrayBuffer[NoteEntity]]]) {

  def addNote(channel: Int, note: mutable.LinkedHashMap[String, ArrayBuffer[NoteEntity]]): Unit = {
    val noteList = notes.getOrElse(channel, Map())

    notes += (channel -> note)
  }

}
