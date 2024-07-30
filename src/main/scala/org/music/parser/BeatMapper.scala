package org.music.parser

import scala.collection.mutable.ArrayBuffer

/**
 * 音符时值解析类
 *
 * 在 MIDI 中，没有对应的音符时值，只有一段间隔的 tick 时间。因此需要解析这段 tick 时间来换算成对应的音符时值
 */
object BeatMapper {

  /**
   * 解析音符时值
   *
   * @param tick tick 时长
   * @param ppq  MIDI 中的 ppq，这项参数代表一个四分之一音符所占用的 tick 时长
   * @return 音符时值
   * @note 传入的 tick 与 ppq 应为公约数，否则会造成音符时值精度异常
   */
  def mapBeat(tick: Long, ppq: Long): ArrayBuffer[Long] = {

    val notes = ArrayBuffer[Long]()

    val wholeNote_ppq = ppq << 2
    val wholeNote_size: Long = tick / wholeNote_ppq

    val remainder = tick % wholeNote_ppq

    for (i <- 0L until wholeNote_size) notes += 1

    if (remainder > 0) {
      notes ++= recursiveNearestPowersOfTwo((remainder << 6) / wholeNote_ppq, ArrayBuffer[Long]())

    }


    notes
  }

  /**
   * 将指定数值分解，递归寻找最接近给定剩余值的2的幂次
   *
   * @param remainder 当前的剩余值，表示待分解的数值。
   * @param notes     用于收集计算结果。
   * @return 递归最接近给定剩余值的2的幂次的结果
   * @note 此方法为递归方法，调用时传入的数值应该为2的幂次。
   */
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
