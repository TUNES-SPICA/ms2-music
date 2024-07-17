package org.music.entity.mml

import scala.collection.mutable.ArrayBuffer

/**
 * mml 音轨内容
 */
case class MS2MMLEntity(lines: ArrayBuffer[String]) {

  def add(line: String): Unit = {
    lines += line
  }

  def toMML: ArrayBuffer[String] = {
    val mml = ArrayBuffer[String]()

    mml += "<?xml version=\"1.0\" encoding=\"utf-8\"?>"

    mml += "<ms2>"

    for (i <- lines.indices) {
      mml += "<melody>"
      if (i > 0) mml += s"<chord index=${i}>"
      mml += "<![CDATA["
      mml += lines(i)
      mml += "]]>"
      if (i > 0) mml += "</chord>"
      mml += "</melody>"
    }

    mml
  }
}
