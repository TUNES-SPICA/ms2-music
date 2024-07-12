package org.music.entity.track

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * 解析后的 MIDI 音轨实体
 *
 * @param tracks 不同通道的音符集合
 */
class TracksEntity(tracks: ArrayBuffer[NoteEntity], bpm: ArrayBuffer[NoteEntity], ppq: Int) {

  def getTracks: ArrayBuffer[NoteEntity] = tracks

  def getBpm: ArrayBuffer[NoteEntity] = bpm

  def getPpq: Int = ppq

}
