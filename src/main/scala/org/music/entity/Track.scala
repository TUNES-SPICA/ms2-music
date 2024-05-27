package org.music.entity

import lombok.Data


@Data
class Track(var notes: List[Note]) {

  def addNote(note: Note): Unit = {
    this.notes = this.notes :+ note
  }

}