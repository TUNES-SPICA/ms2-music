package org.music.parser

import org.music.entity.NoteEntity

import javax.sound.midi.{MetaMessage, Sequence, ShortMessage, SysexMessage}
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object MidiParser {

  def parseMidiData(sequence: Sequence) = {
    val array = ArrayBuffer[NoteEntity]()

    for (track <- sequence.getTracks) {
      val notesMap: mutable.LinkedHashMap[String, ArrayBuffer[NoteEntity]] = mutable.LinkedHashMap()
      for (i <- 0 until track.size) {
        val event = track.get(i)
        val message = event.getMessage
        message match
          case sm: ShortMessage => {
            val command = sm.getCommand
            val pianoKey = PianoKeyMapper.midiNoteToPianoKey(sm.getData1)
            val volume = VolumeMapper.mapMidiPitch(sm.getData2)
            val tick = event.getTick
            if (command == ShortMessage.NOTE_ON) {
              val noteValue = notesMap.getOrElseUpdate(s"c${sm.getChannel}k${pianoKey}", ArrayBuffer.empty[NoteEntity])
              noteValue += NoteEntity(command, pianoKey, volume, event.getTick, -1, ArrayBuffer())
            } else if (command == ShortMessage.NOTE_OFF) {
              val noteArray = notesMap(s"c${sm.getChannel}k${pianoKey}")
              val head = noteArray.head
              if (head.endTick == -1) {
                head.endTick = tick
                noteArray.remove(0)
                array += head
              }
            }
          }
          case sm: SysexMessage => handleSysexMessage(sm)
          case mm: MetaMessage => handleMetaMessage(mm)
          case _ => // 处理其他未知类型的消息
      }
    }

    array
  }

  /**
   * 处理系统消息
   *
   * @param sm SysexMessage
   */
  private def handleSysexMessage(sm: SysexMessage): Unit = {
    System.out.println("SysexMessage received. Length: " + sm.getLength)
  }

  private def handleMetaMessage(mm: MetaMessage): Unit = {
    // 处理元数据消息
    val `type` = mm.getType
    val data = mm.getData
    `type` match {
      case 81 => // 处理BPM变更
        println("TEMPO Change - BPM: " + 60000000 / ((data(0) & 0xFF) << 16 | (data(1) & 0xFF) << 8 | data(2) & 0xFF))
      case 89 => // 处理调号和调式
        println("KEY SIGNATURE")
      case _ => // 处理其他未知类型的消息
    }
  }

}





