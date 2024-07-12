package org.music.parser

import org.music.entity.track.PianoKeyEntity

object PianoKeyMapper {

  private val pianoKeys: Array[String] = Array("C", "C+", "D", "D+", "E", "F", "F+", "G", "G+", "A", "A+", "B")

  private val rest = "R"

  def midiNoteToPianoKey(midiNote: Int): PianoKeyEntity = {
    if (midiNote >= 24 && midiNote <= 120) {
      val o = ((midiNote - 24) / 12) + 1
      PianoKeyEntity(PianoKeyMapper.pianoKeys(midiNote % 12), "O" + o)
    }
    else PianoKeyEntity(rest, "")
  }

}