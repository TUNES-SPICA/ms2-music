package org.music.entity

import lombok.Data

@Data
class TrackEntity(var notes: Map[Int, List[NoteEntity]]) {

  def addNote(channel: Int, note: NoteEntity): Unit = {
    val noteList = notes.getOrElse(channel, List())

    notes += (channel -> (noteList :+ note))
  }

}
