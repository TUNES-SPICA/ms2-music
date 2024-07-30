package org.music.parser

import org.music.entity.track.{NoteEntity, NoteEnum, TrackSplittingRule, TracksEntity}

import javax.sound.midi.{MetaMessage, Sequence, ShortMessage, SysexMessage}
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

object MidiParser {

  def toMML(tracks: TracksEntity, trackSplittingRule: TrackSplittingRule) = {

    val ppq = tracks.getPpq
    val bpmTrack = tracks.getBpm
    // ===== 对 start 进行升序排序（后续的分轨逻辑需要完全基于 start 进行升序处理
    val audioTrack = tracks.getTracks.sorted(Ordering.by((note: NoteEntity) => note.startTick))

    // ===== 准备参数
    // ----- 轨道集合
    val lines = ArrayBuffer[ArrayBuffer[NoteEntity]]()
    // ----- 重叠空间，此空间用于判断线程是否有重叠的音符
    var overlapIntervals = ArrayBuffer[NoteEntity]()
    // ----- 单轨
    val line = ArrayBuffer[NoteEntity]()

    // ===== 将全音符音轨，根据重叠区域划分为不同的音轨
    audioTrack.foreach(note => {
      breakable {
        for (line <- lines) {
          if (note.startTick >= line.last.endTick) {
            line += note
            break
          }
        }
        lines += ArrayBuffer(note)
      }
    })


    // ===== 填充音轨空白部分 =====
    lines.foreach(line => {

      val newLine = ArrayBuffer[NoteEntity]()
      if (line.head.startTick > 0) newLine += NoteEntity.rest().copy(endTick = line.head.startTick)
      newLine += line(0)

      for (i <- 1 until line.length) {

        val pre = line(i - 1)
        val cur = line(i)

        if (trackSplittingRule.reduceRest) {
          pre.endTick = cur.startTick
        } else {
          if (cur.startTick > pre.endTick) newLine += NoteEntity.rest().copy(startTick = pre.endTick, endTick = cur.startTick)
        }

        newLine += cur
      }

      line.clear()
      line ++= newLine
    })
    // ===== 填充音轨空白部分 =====

    // ===== 输出为 mml
    val ms2Tracks = ArrayBuffer[StringBuilder]()

    lines.foreach(line => {

      val ms2Track = StringBuilder()

      // ===== 应用第一速率 TODO 变速暂未处理
      if (bpmTrack.nonEmpty) ms2Track.append("T").append(bpmTrack(0).volume)
      // ===== 如果设置不应用音量变化，应用默认音量
      if (!trackSplittingRule.changeVolume) ms2Track.append(TrackSplittingRule.getDefaultVolume)

      line.foreach(note => {

        val tick = note.endTick - note.startTick

        if (tick > 0) {
          if (trackSplittingRule.changeVolume) ms2Track.append("V").append(note.volume)

          ms2Track.append(note.pianoKey.octave)

          for (elem <- BeatMapper.mapBeat(tick, ppq)) {
            ms2Track.append(note.pianoKey.key)
            ms2Track.append(elem)
            ms2Track.append("&")
          }

          ms2Track.setLength(ms2Track.length - 1)
        }
      })

      ms2Tracks += ms2Track
    })


    ms2Tracks
  }

  /**
   * 解析 midi
   *
   * @param sequence midi音轨序列
   * @return
   */
  def parseMidiData(sequence: Sequence): TracksEntity = {
    val audioTrack = ArrayBuffer[NoteEntity]()
    val bpmTrack = ArrayBuffer[NoteEntity]()
    val qqp = sequence.getResolution

    var endTick = 0L

    for (track <- sequence.getTracks) {

      val notesMap: mutable.LinkedHashMap[String, ArrayBuffer[NoteEntity]] = mutable.LinkedHashMap()

      for (i <- 0 until track.size) {

        val event = track.get(i)
        val message = event.getMessage
        val tick = event.getTick
        endTick = Math.max(tick, endTick)
        message match
          case sm: SysexMessage => 
          case mm: MetaMessage =>
            // 处理元数据消息
            val mmType = mm.getType
            val data = mm.getData
            mmType match {
              case 81 => // 处理BPM变更
                bpmTrack += NoteEntity.bpm().copy(volume = (60000000 / ((data(0) & 0xFF) << 16 | (data(1) & 0xFF) << 8 | data(2) & 0xFF)), startTick = tick)
              case 89 => // 处理调号和调式
              case _ => // 处理其他未知类型的消息
            }
          case _ => {
            val sm = message.asInstanceOf[ShortMessage]
            val command = sm.getCommand
            val pianoKey = PianoKeyMapper.midiNoteToPianoKey(sm.getData1)
            val volume = VolumeMapper.mapMidiPitch(sm.getData2)
            val channel = sm.getChannel
            if (command == ShortMessage.NOTE_ON) {
              if (volume > 0) {
                notesMap.getOrElseUpdate(s"c${channel}k${pianoKey.key}", ArrayBuffer.empty[NoteEntity])
                  += NoteEntity(NoteEnum.NOTE, pianoKey, volume, event.getTick, -1)
              } else {
                val noteArray = notesMap(s"c${channel}k${pianoKey.key}")
                val head = noteArray.head
                if (head.endTick == -1) {
                  head.endTick = tick
                  noteArray.remove(0)
                  audioTrack += head
                }
              }
            } else if (command == ShortMessage.NOTE_OFF) {
              val noteArray = notesMap(s"c${channel}k${pianoKey.key}")
              val head = noteArray.head
              if (head.endTick == -1) {
                head.endTick = tick
                noteArray.remove(0)
                audioTrack += head
              }
            }
          }

      }
    }

    TracksEntity(audioTrack, bpmTrack, qqp)
  }

}





