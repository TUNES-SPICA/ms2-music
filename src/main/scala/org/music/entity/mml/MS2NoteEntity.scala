package org.music.entity.mml

import org.music.entity.track.NoteEnum
import org.music.entity.track.NoteEnum.VOLUME

import scala.collection.mutable.ArrayBuffer

/**
 * mml 音符实体对象
 *
 * @param noteType [[org.music.entity.track.NoteEnum 音符类型]]
 * @param value    内容
 * @param keys     音符值
 */
case class MS2NoteEntity(var noteType: NoteEnum, var value: String, keys: ArrayBuffer[String])

object MS2NoteEntity {

  def init(note: String, value: String): MS2NoteEntity = {
    note(0) match
      case 'R' | 'r' | 'A' | 'a' | 'B' | 'b' | 'C' | 'c' | 'D' | 'd' | 'E' | 'e' | 'F' | 'f' | 'G' | 'g' | 'N' | 'n' => default().copy(value = note, keys = ArrayBuffer(value))
      case 'T' | 't' => default().copy(noteType = NoteEnum.BPM)
      case 'V' | 'v' => default().copy(noteType = NoteEnum.VOLUME)
      case 'O' | 'o' => default().copy(noteType = NoteEnum.OCTAVE)
      case 'M' | 'm' => default().copy(noteType = NoteEnum.ACTION)
      case '&' => default().copy(noteType = NoteEnum.LINK)
      case _ => default().copy(noteType = NoteEnum.OTHER)
  }

  private def default(): MS2NoteEntity = {
    MS2NoteEntity(NoteEnum.NOTE, "", ArrayBuffer())
  }

}
