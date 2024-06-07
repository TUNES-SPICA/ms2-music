package org.music.parser

import scala.collection.mutable.ArrayBuffer

object BeatMapper {

  def mapBeat(tick: Long, ppq: Long): ArrayBuffer[Long] = {
    val notes = ArrayBuffer[Long]()

    val wholeNote_ppq = ppq << 2
    val wholeNote_size: Long = tick / wholeNote_ppq

    val remainder = tick % wholeNote_ppq

    if (remainder > 0) {
      notes ++= recursiveNearestPowersOfTwo((remainder << 6) / wholeNote_ppq, ArrayBuffer[Long]())

      for (i <- 0L until wholeNote_size) notes += 1
    }

    notes
  }

  private def recursiveNearestPowersOfTwo(remainder: Long, notes: ArrayBuffer[Long]): ArrayBuffer[Long] = {
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
