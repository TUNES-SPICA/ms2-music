package org.music.parser.midi

import scala.collection.mutable.ArrayBuffer

object BeatMapper {

  def mapBeat(tick: Int, ppq: Int): ArrayBuffer[Int] = {
    val wholeNote_ppq = ppq << 2
    val wholeNote_size = tick / wholeNote_ppq

    val remainder = tick % wholeNote_ppq

    val notes = recursiveNearestPowersOfTwo(remainder, ArrayBuffer[Int]())

    for (i <- 0 until wholeNote_size) notes += 1

    notes
  }

  def main(args: Array[String]): Unit = {
    val notes = mapBeat(608, 64)


  }

  private def recursiveNearestPowersOfTwo(remainder: Int, notes: ArrayBuffer[Int]): ArrayBuffer[Int] = {
    val note = nearestPowerOfTwo(remainder)
    if (remainder == note) {
      notes += note
      println("note" + note)
    }
    else {
      notes += note / 2
      println("note" + note / 2)
      recursiveNearestPowersOfTwo(note - remainder, notes)
    }
    notes
  }

  private def nearestPowerOfTwo(cap: Int): Int = {
    var n: Int = cap - 1
    n |= n >>> 1
    n |= n >>> 2
    n |= n >>> 4
    n |= n >>> 8
    n |= n >>> 16

    n + 1
  }


}
