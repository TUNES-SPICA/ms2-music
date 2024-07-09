package org.music.parser

object PianoKeyMapper {

  private val pianoKeys: Array[Array[String]] = Array.ofDim[String](9, 12)

  private val rest = "R"

  def midiNoteToPianoKey(midiNote: Int): String = {
    if (midiNote >= 24 && midiNote <= 120) PianoKeyMapper.pianoKeys((midiNote - 24) / 12)(midiNote % 12)
    else rest
  }

  private def initPianoKeys(): Unit = {
    val noteNames = Array("C", "C+", "D", "D+", "E", "F", "F+", "G", "G+", "A", "A+", "B")

    for (i <- pianoKeys.indices) {

      for (j <- pianoKeys(i).indices) {
        pianoKeys(i)(j) = "O" + (i + 1) + noteNames(j)
      }

    }
  }

  initPianoKeys()

}