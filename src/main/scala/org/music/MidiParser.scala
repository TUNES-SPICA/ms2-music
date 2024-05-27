package org.music

import javax.sound.midi.*


object MidiParser {


  def parseMidiData(sequence: Sequence): Track = {

    val tracks = sequence.getTracks

    for (track <- tracks) {
      for (i <- 0 until track.size) {
        val event = track.get(i)
        val message = event.getMessage
        message match
          case message1: ShortMessage => handleShortMessage(message1, event)
          case message1: SysexMessage => handleSysexMessage(message1)
          case message1: MetaMessage => handleMetaMessage(message1)
          case _ =>
            // 处理其他未知类型的消息
            print("未知类型的 message")
      }
    }
  }

  private def handleShortMessage(sm: ShortMessage, event: MidiEvent): Unit = {
    val command = sm.getCommand
    val channel = sm.getChannel
    val data1 = sm.getData1
    val data2 = sm.getData2
    val tick = event.getTick
    command match {
      case ShortMessage.NOTE_ON =>
        //                System.out.println("NOTE 开 - 通道: " + (channel + 1) + ", 钢琴键: " + midiNoteToPianoKey(data1) + ", 音量: " + data2 + " , 长度: " + (tick - preTick));
        System.out.println("NOTE 开 - 通道: " + (channel + 1) + ", 钢琴键: " + midiNoteToPianoKey(data1) + ", 音量: " + data2 + " , 长度: " + tick)

      case ShortMessage.NOTE_OFF =>
        //                System.out.println("NOTE 关 - 通道: " + (channel + 1) + ", 钢琴键: " + midiNoteToPianoKey(data1) + ", 音量: " + data2 + " , 长度: " + (tick - preTick));
        System.out.println("NOTE 关 - 通道: " + (channel + 1) + ", 钢琴键: " + midiNoteToPianoKey(data1) + ", 音量: " + data2 + " , 长度: " + tick)
        preTick = tick

    }
  }

  private def midiNoteToPianoKey(midiNote: Int) = {
    val rest = "R"
    val noteNames = Array("C", "C+", "D", "D+", "E", "F", "F+", "G", "G+", "A", "A+", "B")
    val pianoKeys = new Array[Array[String]](9, 12)
    for (i <- 0 until pianoKeys.length) {
      for (j <- 0 until pianoKeys(i).length) {
        pianoKeys(i)(j) = "O" + (i + 1) + noteNames(j)
      }
    }
    if (midiNote >= 24 && midiNote <= 120) pianoKeys((midiNote - 24) / 12)(midiNote % 12)
    else rest
  }

  private def handleSysexMessage(sm: SysexMessage): Unit = {
    // 处理系统专用消息
    System.out.println("SysexMessage received. Length: " + sm.getLength)
    // 注意：由于SysexMessage的具体内容可能很长且复杂，这里仅打印长度作为示例
  }

  private def handleMetaMessage(mm: MetaMessage): Unit = {
    // 处理元数据消息
    val `type` = mm.getType
    val data = mm.getData
    `type` match {
      case 81 =>
        // 处理BPM变更
        val mpq = (data(0) & 0xFF) << 16 | (data(1) & 0xFF) << 8 | data(2) & 0xFF
        val bpm = 60000000f / mpq
        System.out.println("TEMPO Change - BPM: " + bpm)

      case 89 =>
        // 处理调号和调式
        System.out.println("KEY SIGNATURE")

      case _ =>
        System.out.println("其他MetaMessage类型: " + `type`)
    }
  }

}
