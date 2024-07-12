package org.music.operator

import org.music.entity.mml.MS2NoteEntity

import scala.collection.mutable.ArrayBuffer

object PatternDrivenStringCompression {

  def compression(line: String): String = {

    val p = ArrayBuffer[MS2NoteEntity]()

    // ===== 执行第一次遍历，将字符转换为 MML 音符 =====

    var point = 0

    while (point < line.length) {

      var note: String = null
      var value: String = null
      if (line(point) == 0) {
        note = "&"
      }
      else if (line(point) < 10) {
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

      p += MS2NoteEntity.init(note, value)
    }

    // ===== 执行第二次遍历，将相邻的字符进行 L 合并

    // ===== 执行第 3~n 次遍历，将两个 L 段落同音符时值出现过三次以上的音符进行 L 合并

    ""
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
