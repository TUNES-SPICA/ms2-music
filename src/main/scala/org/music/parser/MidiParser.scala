package org.music.parser

import org.music.entity.NoteEntity

import javax.sound.midi.{MetaMessage, Sequence, ShortMessage, SysexMessage}
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

object MidiParser {

  def toMML(audioTrack: ArrayBuffer[NoteEntity]) = {
    // 对 start 进行升序排序
    val orderNotes = audioTrack.sorted(Ordering.by((note: NoteEntity) => note.startTick))

    // ===== 准备参数
    // ----- 轨道集合
    val lines = ArrayBuffer[ArrayBuffer[NoteEntity]]()
    // ----- 重叠空间，此空间用于判断线程是否有重叠的音符
    var overlapIntervals = ArrayBuffer[NoteEntity]()
    // ----- 单轨
    val line = ArrayBuffer[NoteEntity]()

    // ===== 将全音符音轨，根据重叠区域划分为不同的音轨
    orderNotes.foreach(note => {
      breakable {
        for (line <- lines) {
          if (note.startTick >= line.last.endTick) {
            line += note
            break
          }
        }
        lines += new ArrayBuffer[NoteEntity]
        lines.last += note
      }
    })

    lines
  }

  /**
   * 解析 midi
   *
   * @param sequence midi音轨序列
   * @return
   */
  def parseMidiData(sequence: Sequence): ArrayBuffer[NoteEntity] = {
    val audioTrack = ArrayBuffer[NoteEntity]()

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
                audioTrack += head
              }
            }
          }
          case sm: SysexMessage => handleSysexMessage(sm)
          case mm: MetaMessage => handleMetaMessage(mm)
          case _ => // 处理其他未知类型的消息
      }
    }

    audioTrack
  }

  /**
   * 处理系统消息
   *
   * @param sm SysexMessage
   */
  private def handleSysexMessage(sm: SysexMessage): Unit = {
    System.out.println("SysexMessage received. Length: " + sm.getLength)
  }

  /**
   * 处理 MIDI 元数据消息
   *
   * @param mm mete message
   */
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





