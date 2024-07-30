package org.music.operator

import org.music.entity.mml.MS2NoteEntity
import org.music.entity.track.NoteEnum
import org.music.entity.track.NoteEnum.{BLANK, NOTE}
import org.music.parser.PianoKeyMapper

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

object PatternDrivenStringCompression {

  def compression(line: String): String = {

    var p = ArrayBuffer[MS2NoteEntity]()

    // ===== 执行第一次遍历，将字符转换为 MML 音符 =====

    var point = 0

    while (point < line.length) {

      var note: String = null
      var value: String = null
      if (getType(line(point)) == 0) {
        note = "&"
      }
      else if (getType(line(point)) < 10) {
        if (line(point + 1) == '+') {
          note = line(point).toString + "+"
          point = point + 1
        } else {
          note = line(point).toString
        }

        point = point + 1

        if (point < line.length && getType(line(point)) == 10) {
          value = line(point).toString
          if (point + 1 < line.length && getType(line(point + 1)) == 10) {
            point = point + 1
            value = value + line(point)
          }
        }
      }

      point = point + 1

      if (note != null) {
        p += MS2NoteEntity.init(note, value)
      }
    }

    // ===== 合并 & 连接符，展开压缩音符 =====
    {
      var pre: MS2NoteEntity = p(0)
      var noteTime: MS2NoteEntity = null
      for (i <- 1 until p.length - 1) {
        val cur = p(i)
        if (cur.noteType == NoteEnum.LINK) {
          p(i) = MS2NoteEntity.blank()

          pre.keys ++= p(i + 1).keys

          p(i + 1) = MS2NoteEntity.blank()
        } else if (cur.noteType == NoteEnum.NOTE) {

          if (noteTime != null) {
            if (cur.keys == null) cur.keys = ArrayBuffer(noteTime.value.toInt)
          }

          pre = cur
        } else if (cur.noteType == NoteEnum.NOTE_TIME) {
          noteTime = cur
        }
      }
    }

    // ===== 执行第二次遍历，将相邻的字符进行 L 合并
    {
      val newNoteMap: mutable.HashMap[Int, MS2NoteEntity] = mutable.HashMap()

      var prePoint: Int = 0
      var pre: MS2NoteEntity = p(0)
      var cur: MS2NoteEntity = null

      var volume: MS2NoteEntity = null
      var noteTime: String = null
      var noteTimeEnd: MS2NoteEntity = null
      var octave: String = null
      var action: MS2NoteEntity = null
      for (i <- 1 until p.length - 1) {
        cur = p(i)

        cur.noteType match
          case NoteEnum.ACTION =>
            if (action == null) action = cur
            else if (action.value.equals(cur.value)) p(i) = MS2NoteEntity.blank()
          case NoteEnum.OCTAVE =>
            if (octave == null) octave = cur.value
            else {
              if (octave.equals(cur.value)) {
                p(i) = MS2NoteEntity.blank()
              }
              else {
                val preOctave: Int = octave.toInt
                val curOctave: Int = cur.value.toInt

                if (preOctave - curOctave == 1) {
                  cur.keys += cur.value.toInt
                  cur.value = "<"
                  cur.noteType = NoteEnum.OCTAVE2
                }
                else if (curOctave - preOctave == 1) {
                  cur.keys += cur.value.toInt
                  cur.value = ">"
                  cur.noteType = NoteEnum.OCTAVE2
                }

                octave = curOctave.toString
              }
            }
          case NoteEnum.NOTE_TIME =>
            val curNoteTime = cur.value
            if (noteTime == null) noteTime = curNoteTime
            else if (noteTime.equals(curNoteTime)) {
              p(i) = MS2NoteEntity.blank()
              noteTime = curNoteTime
            }
          case NoteEnum.NOTE =>
            if (pre == noteTimeEnd) {
              breakable {
                for (key <- cur.keys) {
                  if (key == noteTime.toInt) {
                    noteTimeEnd = cur
                    break
                  } else {
                    newNoteMap += ((prePoint, MS2NoteEntity.noteTimeEnd()))
                  }
                }
              }
              //            cur.keys.mapInPlace { k => if (k == noteTime.toInt) 0 else k }
            } else {
              val keys: ArrayBuffer[Int] = ArrayBuffer()
              keys ++= pre.keys
              keys ++= cur.keys

              val mostFrequent = keys
                .groupBy(identity)
                .view
                .map { case (k, v) => (k, v.size) }
                .maxBy(_._2)

              if (mostFrequent._2 > 1) {
                val key = mostFrequent._1

                //              cur.keys.mapInPlace { k => if (k == key) 0 else k }
                //              pre.keys.mapInPlace { k => if (k == key) 0 else k }

                if (key.toString != noteTime) {
                  newNoteMap += ((prePoint, MS2NoteEntity.noteTime(key.toString)))
                  noteTime = key.toString
                }

                noteTimeEnd = cur
              }
            }

            prePoint = i
            pre = cur
          case NoteEnum.BLANK | NoteEnum.OTHER | _
          => // 跳过

      }

      val sortedKeys = newNoteMap.keys.toList.sorted(Ordering[Int].reverse)
      for (index <- sortedKeys) {
        p.insert(index, newNoteMap(index))
      }
    }
    // ===== 执行第 3~n 次遍历，将两个 L 段落同音符时值出现过三次以上的音符进行 L 合并
    {
      var count = 1
      while (count > 0) {
        count = 0
        val noteTimeSegment = mutable.Map[Int, Int]()
        val newNoteMap: mutable.HashMap[Int, MS2NoteEntity] = mutable.HashMap()

        var start = 0
        var status = true
        for (i <- p.indices) {
          p(i).noteType match
            case NoteEnum.NOTE_TIME | NoteEnum.NOTE_TIME_END =>
              if (status) {
                start = i
                status = false
              } else {
                noteTimeSegment += ((start, i))
                status = true
              }
            case _ =>
        }

        noteTimeSegment.foreach((start, end) => {
          val slice = p.slice(start, end)
          val keys = slice.flatMap(_.keys)

          if (keys.size > 2) {
            val mostFrequent = keys
              .groupBy(identity)
              .view
              .map { case (k, v) => (k, v.size) }
              .maxBy(_._2)

            if (mostFrequent._2 > 1) {
              val key = mostFrequent._1
              count += mostFrequent._2

              breakable {
                for (i <- start until end) {
                  val cur = p(i)
                  cur.keys.foreach(k => {
                    if (k == key) {

                      newNoteMap += ((i, MS2NoteEntity.noteTime(key.toString)))
                      break
                    }
                  })
                }
              }

              breakable {
                for (i <- end - 1 to start by -1) {
                  val cur = p(i)
                  cur.keys.foreach(k => {
                    if (k == key) {

                      newNoteMap += ((i + 1, MS2NoteEntity.noteTimeEnd()))
                      break
                    }
                  })
                }
              }

            }
          }
        })

        val sortedKeys = newNoteMap.keys.toList.sorted(Ordering[Int].reverse)
        for (index <- sortedKeys) {
          p.insert(index, newNoteMap(index))
        }
      }
    }

    // ===== 删除空白符
    p = p.filter(note => !(note.noteType == NoteEnum.BLANK || note.noteType == NoteEnum.OTHER))

    // ===== 执行最后一次遍历，压缩音阶 =====
    //    {
    //      var point = 0
    //
    //      while (point < p.length) {
    //
    //        var cur, next, nextNext: MS2NoteEntity = null
    //
    //        var curOctave, nextOctave, nextNextOctave: MS2NoteEntity = null
    //
    //        var isContinuous = true
    //
    //        var isGoon = true
    //
    //        val note = p(point)
    //
    //        if (note.noteType == NoteEnum.OCTAVE || note.noteType == NoteEnum.OCTAVE2) {
    //          curOctave = note
    //          point = point + 1
    //          while (point < p.length && isGoon) {
    //            p(point).noteType match
    //              case NoteEnum.NOTE => {
    //                cur = p(point)
    //                isGoon = false
    //              }
    //              case _ =>
    //            point = point + 1
    //          }
    //
    //          isGoon = true
    //          while (point < p.length && isContinuous && isGoon) {
    //            p(point).noteType match
    //              case NoteEnum.NOTE => isContinuous = false
    //              case NoteEnum.OCTAVE | NoteEnum.OCTAVE2 => {
    //                nextOctave = p(point)
    //                isGoon = false
    //              }
    //              case _ =>
    //
    //            point = point + 1
    //          }
    //
    //          var point2 = point
    //          isGoon = true
    //          while (point2 < p.length && isContinuous && isGoon) {
    //            p(point2).noteType match
    //              case NoteEnum.NOTE => {
    //                next = p(point2)
    //                isGoon = false
    //              }
    //              case _ =>
    //            point2 = point2 + 1
    //          }
    //
    //          isGoon = true
    //          while (point2 < p.length && isContinuous && isGoon) {
    //            p(point2).noteType match
    //              case NoteEnum.NOTE => isContinuous = false
    //              case NoteEnum.OCTAVE | NoteEnum.OCTAVE2 => {
    //                nextNextOctave = p(point2)
    //                isGoon = false
    //              }
    //              case _ =>
    //
    //            point2 = point2 + 1
    //          }
    //
    //          isGoon = true
    //          while (point2 < p.length && isContinuous && isGoon) {
    //            p(point2).noteType match
    //              case NoteEnum.NOTE => {
    //                nextNext = p(point2)
    //                isGoon = false
    //              }
    //              case _ =>
    //            point2 = point2 + 1
    //          }
    //
    //          if (isContinuous && cur != null && next != null && nextNext != null) {
    //            val curValue = if (curOctave.noteType == NoteEnum.OCTAVE) curOctave.value.toInt else curOctave.keys(0)
    //            val nextValue = if (nextOctave.noteType == NoteEnum.OCTAVE) nextOctave.value.toInt else nextOctave.keys(0)
    //            val nextNextValue = if (nextNextOctave.noteType == NoteEnum.OCTAVE) nextNextOctave.value.toInt else nextNext.keys(0)
    //
    //            if ((cur.value == "B" || cur.value == "C-") && (next.value == "C" || next.value == "B+") && nextOctave.value == ">") {
    //              next.value = "B+"
    //            } else if ((cur.value == "C" || cur.value == "B+") && (next.value == "B" || next.value == "C-") && nextOctave.value == "<") {
    //              next.value = "C-"
    //            } else {
    //              next.value = "N" + PianoKeyMapper.pianoKeyToMMLNote(nextValue, next.value)
    //            }
    //
    //            nextOctave.noteType = NoteEnum.BLANK
    //            if (curValue == nextNextValue) {
    //              nextNextOctave.noteType = NoteEnum.BLANK
    //            } else if (curValue - nextNextValue == 1) {
    //              if (nextNextOctave.noteType == NoteEnum.OCTAVE) nextNextOctave.keys += nextNextValue
    //              else nextNextOctave.keys(0) = nextNextValue
    //              nextNextOctave.noteType = NoteEnum.OCTAVE
    //              nextNextOctave.value = "<"
    //            } else if (nextNextValue - curValue == 1) {
    //              if (nextNextOctave.noteType == NoteEnum.OCTAVE) nextNextOctave.keys += nextNextValue
    //              else nextNextOctave.keys(0) = nextNextValue
    //              nextNextOctave.noteType = NoteEnum.OCTAVE
    //              nextNextOctave.value = ">"
    //            }
    //
    //          }
    //
    //        }
    //
    //        point += 1
    //      }
    //    }


    // ===== 输出压缩结果 =====
    val result = new StringBuilder()

    var noteTime: String = null
    for (note <- p) {
      note.noteType match
        case NoteEnum.NOTE =>

          val keys = note.keys.toList.sorted(Ordering[Int])

          if (keys.size == 2 && keys.head * 2 == keys(1)) {
            result.append(note.value)
            if (keys.head.toString != noteTime) {
              result.append(keys.head)
            }
            result.append(".")
          } else {
            keys.foreach(key => {
              result.append(note.value)
              if (key.toString != noteTime) {
                result.append(key)
              }
              result.append("&")
            })
            result.setLength(result.length() - 1)
          }

        case NoteEnum.BPM => result.append("T").append(note.value)
        case NoteEnum.NOTE_TIME =>
          if (note.value != noteTime) {
            result.append("L").append(note.value)
            noteTime = note.value
          }
        case NoteEnum.ACTION => result.append("M").append(note.value)
        case NoteEnum.VOLUME => result.append("V").append(note.value)
        case NoteEnum.OCTAVE => result.append("O").append(note.value)
        case NoteEnum.OCTAVE2 => result.append(note.value)
        case NoteEnum.BLANK | NoteEnum.OTHER | NoteEnum.NOTE_TIME_END | _ =>
    }

    result.toString()
  }

  private def getType(char: Char): Int = {
    char match {
      case '&' => 0
      case 'R' | 'r' | 'A' | 'a' | 'B' | 'b' | 'C' | 'c' | 'D' | 'd' | 'E' | 'e' | 'F' | 'f' | 'G' | 'g' | 'N' | 'n' | '+' => 1
      case 'T' | 't' => 2
      case 'V' | 'v' => 3
      case 'O' | 'o' => 4
      case 'M' | 'm' => 5
      case '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | '0' => 10
      case _ => 20
    }

  }
}