package org.music.parser.midi

import scala.collection.mutable.ArrayBuffer

object BeatMapper {

  def mapBeat(tick: Int, ppq: Int): ArrayBuffer[Int] = {
    val wholeNote_ppq = ppq << 2
    val wholeNote_size = tick / wholeNote_ppq

    val remainder = tick % wholeNote_ppq

    val notes = recursiveNearestPowersOfTwo((remainder << 6) / wholeNote_ppq, ArrayBuffer[Int]())

    for (i <- 0 until wholeNote_size) notes += 1

    notes
  }

  private def recursiveNearestPowersOfTwo(remainder: Int, notes: ArrayBuffer[Int]): ArrayBuffer[Int] = {
    var note = remainder
    note |= note >>> 1
    note |= note >>> 2
    note |= note >>> 4
    note |= note >>> 8
    note |= note >>> 16
    note = note + 1 >> 1

    notes += 64 / note

    if (remainder != note) {
      recursiveNearestPowersOfTwo(remainder - note, notes)
    }

    notes
  }


}
