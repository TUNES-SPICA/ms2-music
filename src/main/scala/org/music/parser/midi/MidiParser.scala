package org.music.parser

import org.music.PianoKeyMapper
import org.music.entity.{NoteEntity, TrackEntity}
import org.music.parser.midi.VolumeMapper

import javax.sound.midi.*


object MidiParser {

  def parseMidiData(sequence: Sequence): TrackEntity = {
    val trackEntity = new TrackEntity(Map.empty)

    for (track <- sequence.getTracks) {
      for (i <- 0 until track.size) {
        val event = track.get(i)
        val message = event.getMessage
        message match
          case sm: ShortMessage => {
            val command = sm.getCommand
            val pianoKey = PianoKeyMapper.midiNoteToPianoKey(sm.getData1)
            val volume = VolumeMapper.mapMidiPitch(sm.getData2)
            //            val tick = event.getTick
            val tick = List[Long]()

            trackEntity.addNote(sm.getChannel, NoteEntity(command, pianoKey, volume, tick))
          }
          case sm: SysexMessage => handleSysexMessage(sm)
          case mm: MetaMessage => handleMetaMessage(mm)
          case _ =>
            // 处理其他未知类型的消息
            print("未知类型的 message")
      }
    }

    trackEntity
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
      case 81 =>
        // 处理BPM变更
        val mpq = (data(0) & 0xFF) << 16 | (data(1) & 0xFF) << 8 | data(2) & 0xFF
        val bpm = 60000000 / mpq
        System.out.println("TEMPO Change - BPM: " + bpm)

      case 89 =>
        // 处理调号和调式
        System.out.println("KEY SIGNATURE")

      case _ =>
        System.out.println("其他MetaMessage类型: " + `type`)
    }
  }

}





