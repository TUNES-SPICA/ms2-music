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
    val note = nearestPowerOfTwo(remainder)

    notes += 64 / note

    if (remainder != note) {
      recursiveNearestPowersOfTwo(remainder - note, notes)
    }

    notes
  }

  private def nearestPowerOfTwo(cap: Int): Int = {
    var n: Int = cap
    n |= n >>> 1
    n |= n >>> 2
    n |= n >>> 4
    n |= n >>> 8
    n |= n >>> 16

    n + 1 >> 1
  }


}
